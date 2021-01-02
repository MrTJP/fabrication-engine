package mrtjp.fengine.scenarios

import grizzled.slf4j.Logging
import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.testimpl.TTestFETileMap

class AssemblerScenarioTestFixture(val scenario:AssemblerScenario) extends Logging
{
    logger.info("Initializing test scenario..")
    val assembler:ICAssembler = ICAssembler.newAssembler
    assembler.addTileMap(scenario.rootMap, Map.empty)
    assembler.allocRegisterID(5) //reserve a few

    val flatMap:ICFlatMap = assembler.result()

    logger.info("Test scenario created:")
//    scenario.rootMap.printMap()

    flatMap.exploredTileMaps.collect {
        case testMap:TTestFETileMap => testMap.printMap()
    }
}
