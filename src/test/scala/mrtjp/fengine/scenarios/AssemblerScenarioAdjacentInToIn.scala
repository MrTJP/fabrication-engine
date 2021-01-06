package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord.{bitNorth, bitSouth}
import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, TTestFETileMap}

/**
  * Test Case: Two input gates with adjacent inputs
  *
  * Contains:
  *     * (X) Input Tile (inMask = bitSouth)
  *     * (Y) Input Tile (inMask = bitNorth)
  *
  * Layout (Spans 1x2x1)
  *    *Z -- 0 -- (x)
  *     |
  *     0    X
  *     |
  *     1    Y
  *     (y)
  */
class AssemblerScenarioAdjacentInToIn extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 2

        // Add the driving tile at (0, 0)
        val sinkXCoord = new TileCoord(0, 0, 0)
        val sinkXGate = new PortlessGateTileImpl("X", inDirMask = bitSouth, outDirMask = 0)
        addTile(sinkXCoord, sinkXGate)

        // Add the driven tile at (0, 4)
        val sinkYCoord = new TileCoord(0, 1, 0)
        val sinkYGate = new PortlessGateTileImpl("Y", inDirMask = bitNorth, outDirMask = 0)
        addTile(sinkYCoord, sinkYGate)
    }

    override def rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sinkXGate.gate, map.sinkYGate.gate)

    override val expectedRegisters = Seq()

    override val expectedRelationships = Seq()
}
