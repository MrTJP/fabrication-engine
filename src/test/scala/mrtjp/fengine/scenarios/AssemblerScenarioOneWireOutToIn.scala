package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, PortlessWireTileImpl, TTestFETileMap}

/**
  * Test Case: Single output gate connected to single input gate with wire
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitSouth)
  *     * (Y) Input Tile (inMask = bitNorth)
  *     * (w) Wire (connMask = bitNorth|bitSouth)
  *
  * Layout (Spans 1x3x1)
  *    *Z -- 0 -- 1 (x)
  *     |
  *     0    X
  *     |
  *     1    w
  *     |
  *     2    Y
  *     (y)
  */
class AssemblerScenarioOneWireOutToIn extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 3

        // Add the driving tile at (0, 0)
        val sourceCoord = new TileCoord(0, 0, 0)
        val sourceGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceCoord, sourceGate)

        // Add the driven tile at (0, 4)
        val sinkCoord = new TileCoord(0, 2, 0)
        val sinkGate = new PortlessGateTileImpl("Y", inDirMask = bitNorth, outDirMask = 0)
        addTile(sinkCoord, sinkGate)

        // Add wire at (0,1)
        addTile(new TileCoord(0, 1, 0), new PortlessWireTileImpl(bitNorth|bitSouth))
    }

    override val rootMap:TTestFETileMap = map
    override val expectedGates = Seq(map.sourceGate.gate, map.sinkGate.gate)
    override val expectedRegisters = Seq(map.sourceGate.registers(dirSouth))

    override val expectedRelationships = Seq(
        (map.sourceGate.gate, Seq(), Seq(map.sourceGate.registers(dirSouth))),
        (map.sinkGate.gate, Seq(map.sourceGate.registers(dirSouth)), Seq())
    )
}
