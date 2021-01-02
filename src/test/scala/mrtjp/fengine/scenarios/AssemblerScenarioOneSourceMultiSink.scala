package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, PortlessWireTileImpl, TTestFETileMap}

/**
  * Test Case: Single driver controlling multiple recievers
  *
  * Contains:
  *     * (A) Reciever Tile (inMask = bitSouth)
  *     * (B) Reciever Tile (inMask = bitSouth)
  *     * (C) Reciever Tile (inMask = bitSouth)
  *     * (Y) Driver Tile (outMask = bitNorth|bitEast|bitWest)
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
class AssemblerScenarioOneSourceMultiSink extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 4
        override val renderHeight = 3

        val sinkACoord:TileCoord = TileCoord(0, 0, 0)
        val sinkBCoord:TileCoord = TileCoord(1, 0, 0)
        val sinkCCoord:TileCoord = TileCoord(3, 0, 0)
        val sourceCoord:TileCoord = TileCoord(2, 2, 0)

        // Add the driving tiles
        val sinkAGate = new PortlessGateTileImpl("A", inDirMask = bitSouth, outDirMask = 0)
        val sinkBGate = new PortlessGateTileImpl("B", inDirMask = bitSouth, outDirMask = 0)
        val sinkCGate = new PortlessGateTileImpl("C", inDirMask = bitSouth, outDirMask = 0)
        addTile(sinkACoord, sinkAGate)
        addTile(sinkBCoord, sinkBGate)
        addTile(sinkCCoord, sinkCGate)

        // Add the reciever tile
        val sourceGate = new PortlessGateTileImpl("Y", inDirMask = 0, outDirMask = bitNorth|bitEast|bitWest)
        addTile(sourceCoord, sourceGate)

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

    override val rootMap:TTestFETileMap = map
    override val expectedGates = Seq(
        map.sinkAGate.gate,
        map.sinkBGate.gate,
        map.sinkCGate.gate,
        map.sourceGate.gate
    )
    override val expectedRegisters = Seq(
        map.sourceGate.registers(dirNorth),
        map.sourceGate.registers(dirEast),
        map.sourceGate.registers(dirWest)
    )

    override val expectedRelationships = Seq(
        (map.sinkAGate.gate, Seq(map.sourceGate.registers(dirWest)), Seq()),
        (map.sinkBGate.gate, Seq(map.sourceGate.registers(dirNorth)), Seq()),
        (map.sinkCGate.gate, Seq(map.sourceGate.registers(dirEast)), Seq()),
        (map.sourceGate.gate, Seq(), Seq(
            map.sourceGate.registers(dirNorth),
            map.sourceGate.registers(dirEast),
            map.sourceGate.registers(dirWest)
        ))
    )
}
