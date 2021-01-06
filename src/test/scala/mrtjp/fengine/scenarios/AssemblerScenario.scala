package mrtjp.fengine.scenarios

import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.simulate.{ICGate, ICRegister}
import mrtjp.fengine.testimpl.TTestFETileMap

/**
  * A testable assembly scenario consisting of an FETileMap and some expectations
  * on what the assembled flat map should look like if passed through an ICAssembler
  */
trait AssemblerScenario {
    // Tile Map for this scenario
    def rootMap:TTestFETileMap

    // Expected gate and register relationships in flat map
    def expectedGates:Seq[ICGate]
    def expectedRegisters:Seq[ICRegister]
    def expectedRelationships:Seq[(ICGate, Seq[ICRegister], Seq[ICRegister])]

    // Tests will call this to create a flat map upon which the above will be verified
    def assembleFlatMap:ICFlatMap = {
        val assembler:ICAssembler = ICAssembler.newAssembler
        assembler.addTileMap(rootMap, Map.empty)
        assembler.result()
    }
}
