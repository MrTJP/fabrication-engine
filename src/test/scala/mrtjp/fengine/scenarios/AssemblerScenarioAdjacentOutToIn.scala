package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord._
import mrtjp.fengine._
import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, TTestFETileMap}

/**
  * Test Case: Single output gate connected to single input gate directly with no wire
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitSouth)
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
class AssemblerScenarioAdjacentOutToIn extends AssemblerScenario {
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 2

        // Add the source gate at (0, 0)
        val sourceCoord = new TileCoord(0, 0, 0)
        val sourceGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceCoord, sourceGate)

        // Add the sink gate at (0, 4)
        val sinkCoord = new TileCoord(0, 1, 0)
        val sinkGate = new PortlessGateTileImpl("Y", inDirMask = bitNorth, outDirMask = 0)
        addTile(sinkCoord, sinkGate)
    }

    override def rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sourceGate.gate, map.sinkGate.gate)

    override val expectedRegisters = Seq(map.sourceGate.registers(dirSouth))

    override val expectedRelationships = Seq(
        (map.sourceGate.gate, Seq(), Seq(map.sourceGate.registers(dirSouth))),
        (map.sinkGate.gate, Seq(map.sourceGate.registers(dirSouth)), Seq())
    )
}