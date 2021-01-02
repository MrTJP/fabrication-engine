package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, TTestFETileMap}

/**
  * Test Case: Single source gate with no adjacent tiles
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitNorth|bitSouth|bitEast|bitWest)
  *
  * Layout (Spans 1x1x1)
  *    *Z -- 0 -- (x)
  *     |
  *     0    X
  *     |
  *     (y)
  */
class AssemblerScenarioSecludedSource extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 1

        // Add the driving tile at (0, 0)
        val sourceXCoord = new TileCoord(0, 0, 0)
        val sourceXGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitNorth|bitSouth|bitEast|bitWest)
        addTile(sourceXCoord, sourceXGate)
    }

    override val rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sourceXGate.gate)

    override val expectedRegisters = Seq(
        map.sourceXGate.registers(dirNorth),
        map.sourceXGate.registers(dirEast),
        map.sourceXGate.registers(dirSouth),
        map.sourceXGate.registers(dirWest),
    )

    override val expectedRelationships = Seq()
}
