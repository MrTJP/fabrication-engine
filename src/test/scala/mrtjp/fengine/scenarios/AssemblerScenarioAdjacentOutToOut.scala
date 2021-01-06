package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord._
import mrtjp.fengine._
import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, TTestFETileMap}

/**
  * Test Case: Two output gates with adjacent outputs
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitSouth)
  *     * (Y) Output Tile (outMask = bitNorth)
  *
  * Layout (Spans 1x2x1)
  *    *Z -- 0 -- (x)
  *     |
  *     0    X
  *     |
  *     1    Y
  *     (y)
  */
class AssemblerScenarioAdjacentOutToOut extends AssemblerScenario {
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 2

        // Add the source gate at (0, 0)
        val sourceXCoord = new TileCoord(0, 0, 0)
        val sourceXGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceXCoord, sourceXGate)

        // Add the sink gate at (0, 4)
        val sourceYCoord = new TileCoord(0, 1, 0)
        val sourceYGate = new PortlessGateTileImpl("Y", inDirMask = 0, outDirMask = bitNorth)
        addTile(sourceYCoord, sourceYGate)
    }

    override def rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sourceXGate.gate, map.sourceYGate.gate)

    override val expectedRegisters = Seq(
        map.sourceXGate.registers(dirSouth),
        map.sourceYGate.registers(dirNorth)
    )

    override val expectedRelationships = Seq(
        (map.sourceXGate.gate, Seq(), Seq(map.sourceXGate.registers(dirSouth))),
        (map.sourceYGate.gate, Seq(), Seq(map.sourceYGate.registers(dirNorth)))
    )
}