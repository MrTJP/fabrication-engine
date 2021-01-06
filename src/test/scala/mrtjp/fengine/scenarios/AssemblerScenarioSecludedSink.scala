package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, TTestFETileMap}

/**
  * Test Case: Single sink gate with no adjacent sources
  *
  * Contains:
  *     * (X) Input Tile (inMask = bitNorth|bitSouth|bitEast|bitWest)
  *
  * Layout (Spans 1x1x1)
  *    *Z -- 0 -- (x)
  *     |
  *     0    X
  *     |
  *     (y)
  */
class AssemblerScenarioSecludedSink extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 1

        // Add the driving tile at (0, 0)
        val sinkXCoord = new TileCoord(0, 0, 0)
        val sinkXGate = new PortlessGateTileImpl("X", outDirMask = 0, inDirMask = bitNorth|bitSouth|bitEast|bitWest)
        addTile(sinkXCoord, sinkXGate)
    }

    override val rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sinkXGate.gate)

    override val expectedRegisters = Seq()

    override val expectedRelationships = Seq()
}
