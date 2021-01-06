package mrtjp.fengine.spec

import grizzled.slf4j.Logging
import mrtjp.fengine.assemble.ICFlatMap
import mrtjp.fengine.scenarios._
import mrtjp.fengine.testimpl.TTestFETileMap
import org.scalatest.flatspec.AnyFlatSpec

class ICAssemblerSpec extends AnyFlatSpec with Logging
{
    def validFlatMapAssembler(scenarioFactory: => AssemblerScenario):Unit =
    {
        var scenario:AssemblerScenario = null
        var flatMap:ICFlatMap = null

        it should "assemble without errors" in {
            logger.info("Creating scenario...")
            scenario = scenarioFactory
            logger.info("Compiling flatmap")
            flatMap = scenario.assembleFlatMap
            logger.info("Flat map completed assembling the following maps:")
            flatMap.exploredTileMaps.zipWithIndex.collect {
                case (testMap:TTestFETileMap, i) =>
                    logger.info(s"### Map $i ###")
                    testMap.printMap()
            }
        }

        it should "map correct number of registers" in {

            val expectedRegCount = scenario.expectedRegisters.size
            val actualRegCount = flatMap.registers.size
            logger.info(s"Expected reg count: $expectedRegCount. Actual reg count: $actualRegCount")
            assert(actualRegCount == expectedRegCount, s"Mapped $actualRegCount registers, expected $expectedRegCount")
        }

        it should "map all expected registers" in {

            for (icReg <- scenario.expectedRegisters) {
                val matches = flatMap.registers.count(_._2 eq icReg)
                assert(matches == 1, s"Found $matches matches for reg $icReg. Expected exactly 1.")
            }

            logger.info("all expected registers mapped")
        }

        it should "map correct number of gates" in {

            val expectedGateCount = scenario.expectedGates.size
            val actualGateCount = flatMap.gates.size
            logger.info(s"Expected gate count: $expectedGateCount. Actual gate count: $actualGateCount")
            assert(actualGateCount == expectedGateCount, s"Mapped $actualGateCount gates, expected $expectedGateCount")
        }

        it should "map all expected gates" in {

            for (icGate <- scenario.expectedGates) {
                val matches = flatMap.gates.count(_._2 eq icGate)
                assert(matches == 1, s"Found $matches matches for gate $icGate. Expected exactly 1.")
            }

            logger.info("all expected gates mapped")
        }

        it should "map correct relationships between registers and gates" in {

            for ((g, dependencies, dependents) <- scenario.expectedRelationships) {

                logger.info(s"Checking relationships for gate $g")
                val gID = flatMap.gates.find(_._2 eq g).map(_._1).getOrElse(fail(s"Failed to find id for gate $g"))
                logger.info(s"Gate $g was mapped to gateID $gID")

                logger.info(s"Mapping related register ids for gateID $gID")
                val expectedDependencyIDs = dependencies.map { r =>
                    flatMap.registers.find(_._2 eq r).map(_._1).getOrElse(fail(s"Failed to find id for dependency reg $r"))
                }.sorted
                val expectedDependentIDs = dependents.map { r =>
                    flatMap.registers.find(_._2 eq r).map(_._1).getOrElse(fail(s"Failed to find id for dependency reg $r"))
                }.sorted

                logger.info(s"Checking relationships for gateID $gID")
                val actualDependencyIDs = flatMap.gateDependencies(gID).sorted
                val actualDependentIDs = flatMap.gateDependents(gID).sorted
                assert(actualDependencyIDs == expectedDependencyIDs, s"Mapped dependency relationships for gateID $gID did not match expected")
                assert(actualDependentIDs == expectedDependentIDs, s"Mapped dependent relationships for gateID $gID did not match expected")
            }

            logger.info("all expected relationships mapped")
        }
    }

    "In empty map scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioEmptyMap)

    "In secluded source scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioSecludedSource)
    "In secluded sink scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioSecludedSink)

    "In adjacent in-to-in scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioAdjacentInToIn)
    "In adjacent out-to-out scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioAdjacentOutToOut)
    "In adjacent out-to-in scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioAdjacentOutToIn)

    "In long wire out-to-in scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioLongWireOutToIn)

    "In one wire out-to-out scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioOneWireOutToOut)
    "In one wire out-to-in scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioOneWireOutToIn)

    "In multi-source one-sink scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioMultiSourceOneSink)
    "In one-source multi-sink scenario, an ICAssembler" should behave like validFlatMapAssembler(new AssemblerScenarioOneSourceMultiSink)

    for (id <- AssemblerScenarioNestedTileMap.comboIDs)
        s"In simple nested map scenario (combo $id), an ICAssembler" should behave like validFlatMapAssembler(AssemblerScenarioNestedTileMap.createCombo(id))

    for (id <- AssemblerScenarioAdjacentNestedMap.comboIDs) {
        s"In adjacent nested maps scenario (combo $id), an ICAssembler" should behave like validFlatMapAssembler(AssemblerScenarioAdjacentNestedMap.createCombo(id))
    }
}