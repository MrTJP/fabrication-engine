package mrtjp.fengine.scenarios

import mrtjp.fengine.simulate.{ICGate, ICRegister}
import mrtjp.fengine.testimpl.TTestFETileMap

/**
  * A testable assembly scenario consisting of an FETileMap and some expectations
  * on what the assembled flat map should look like if passed through an ICAssembler
  */
abstract class AssemblerScenario {
    val rootMap:TTestFETileMap

    val expectedGates:Seq[ICGate]
    val expectedRegisters:Seq[ICRegister]

    val expectedRelationships:Seq[(ICGate, Seq[ICRegister], Seq[ICRegister])]

//    def init():Unit
}
