package mrtjp.fengine.scenarios

import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.testimpl.TTestFETileMap

/**
  * Test Case: Map is completely empty
  *
  * Contains:
  *     * Nothing
  *
  * Layout (Spans 1x1x1)
  *    *Z -- 0 -- (x)
  *     |
  *     0
  *     |
  *     (y)
  */
class AssemblerScenarioEmptyMap extends AssemblerScenario
{
    val map:TTestFETileMap = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 1
    }

    override val rootMap:TTestFETileMap = map

    override val expectedGates = Seq()

    override val expectedRegisters = Seq()

    override val expectedRelationships = Seq()
}
