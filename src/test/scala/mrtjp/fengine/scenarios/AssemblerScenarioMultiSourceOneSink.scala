package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, PortlessWireTileImpl, TTestFETileMap}

/**
  * Test Case: Multiple drivers controlling single reciever
  *
  * Contains:
  *     * (A) Driver Tile (outMask = bitSouth)
  *     * (B) Driver Tile (outMask = bitSouth)
  *     * (C) Driver Tile (outMask = bitSouth)
  *     * (Y) Reciever Tile (inMask = bitNorth|bitEast|bitWest)
  *     * (w) Wire (connMask = varies, inPortFilterMask = 0xFFFF, outPortMixMask = 0xFFFF)
  *
  * Layout (Spans 4x3x1)
  *    *Z -- 0 -- 1 -- 2 -- 3 -- (x)
  *     |
  *     0    A    B         C
  *     |
  *     1    w  % w    w  % w
  *     |         %
  *     2    w    w    Y    w
  *     |
  *     (y)
  */
class AssemblerScenarioMultiSourceOneSink extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 4
        override val renderHeight = 3

        val sourceACoord:TileCoord = TileCoord(0, 0, 0)
        val sourceBCoord:TileCoord = TileCoord(1, 0, 0)
        val sourceCCoord:TileCoord = TileCoord(3, 0, 0)

        val sinkCoord:TileCoord = TileCoord(2, 2, 0)

        // Add the driving tiles
        val sourceAGate = new PortlessGateTileImpl("A", inDirMask = 0, outDirMask = bitSouth)
        val sourceBGate = new PortlessGateTileImpl("B", inDirMask = 0, outDirMask = bitSouth)
        val sourceCGate = new PortlessGateTileImpl("C", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceACoord, sourceAGate)
        addTile(sourceBCoord, sourceBGate)
        addTile(sourceCCoord, sourceCGate)

        // Add the reciever tile
        val sinkGate = new PortlessGateTileImpl("Y", inDirMask = bitNorth|bitEast|bitWest, outDirMask = 0)
        addTile(sinkCoord, sinkGate)

        // Connect A(south) to Y(west)
        addTile(TileCoord(0, 1, 0), new PortlessWireTileImpl(bitNorth|bitSouth))
        addTile(TileCoord(0, 2, 0), new PortlessWireTileImpl(bitNorth|bitEast))
        addTile(TileCoord(1, 2, 0), new PortlessWireTileImpl(bitEast|bitWest))
        // Connect B(south) to Y(north)
        addTile(TileCoord(1, 1, 0), new PortlessWireTileImpl(bitNorth|bitEast))
        addTile(TileCoord(2, 1, 0), new PortlessWireTileImpl(bitWest|bitSouth))
        // Connect C(south) to Y(east)
        addTile(TileCoord(3, 1, 0), new PortlessWireTileImpl(bitNorth|bitSouth))
        addTile(TileCoord(3, 2, 0), new PortlessWireTileImpl(bitNorth|bitWest))
    }

    override def rootMap:TTestFETileMap = map

    override val expectedGates = Seq(
        map.sourceAGate.gate,
        map.sourceBGate.gate,
        map.sourceCGate.gate,
        map.sinkGate.gate
    )

    override val expectedRegisters = Seq(
        map.sourceAGate.registers(dirSouth),
        map.sourceBGate.registers(dirSouth),
        map.sourceCGate.registers(dirSouth)
    )

    override val expectedRelationships = Seq(
        (map.sourceAGate.gate, Seq(), Seq(map.sourceAGate.registers(dirSouth))),
        (map.sourceBGate.gate, Seq(), Seq(map.sourceBGate.registers(dirSouth))),
        (map.sourceCGate.gate, Seq(), Seq(map.sourceCGate.registers(dirSouth))),
        (map.sinkGate.gate, Seq(
            map.sourceAGate.registers(dirSouth),
            map.sourceBGate.registers(dirSouth),
            map.sourceCGate.registers(dirSouth)
        ), Seq())
    )
}
