package mrtjp.fengine.testcases;

import mrtjp.fengine.api.FabricationEngine;
import mrtjp.fengine.api.ICAssembler;
import mrtjp.fengine.framework.api.FabricationTest;
import mrtjp.fengine.framework.api.FabricationTestClass;
import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FabricationTestClass
public class FabricationBasicTest {

    private final FabricationTestScenario scenario;

    public FabricationBasicTest(FabricationTestScenario scenario) {
        this.scenario = scenario;
    }

    @FabricationTest (order = 0)
    public void testValidateScenario() {

        //TODO
    }

    @FabricationTest (order = 1)
    public void testPrimaryAssembly() {
        System.out.println("Initializing test scenario...");

        ICAssembler assembler = FabricationEngine.newAssembler();

        // Start assembly at the root map
        scenario.init();
        assembler.addTileMap(scenario.rootMap, Collections.emptyMap());

        // Allocate static registers ids
        for (int id : scenario.staticRegisters.keySet()) { assembler.allocRegisterID(id); }

        // Run the assembler
        scenario.rootFlatMap = assembler.result();

        System.out.println("Test case assembled");
    }

    @FabricationTest (order = 2)
    public void testCheckRegisters() {
        Set<ICRegister> expectedRegisters = scenario.expectedRegisters;
        Collection<ICRegister> actualRegisters = scenario.rootFlatMap.getRegisters().values();

        assertEquals(expectedRegisters.size(), actualRegisters.size());
        assertEquals(expectedRegisters, new HashSet<>(actualRegisters));
    }

    @FabricationTest (order = 3)
    public void testCheckStaticRegisters() {
        for (Map.Entry<Integer, ICRegister> e : scenario.staticRegisters.entrySet()) {
            int regId = e.getKey();
            ICRegister expectedReg = e.getValue();
            ICRegister actualReg = scenario.rootFlatMap.getRegisters().get(regId);

            assertEquals(expectedReg, actualReg, "Static register ID " + regId + " assigned to incorrect register");
        }
    }

    @FabricationTest (order = 4)
    public void testCheckGates() {
        Set<ICGate> expectedGates = scenario.expectedGates;
        Collection<ICGate> actualGates = scenario.rootFlatMap.getGates().values();

        assertEquals(expectedGates.size(), actualGates.size());
        assertEquals(expectedGates, new HashSet<>(actualGates));
    }

    @FabricationTest (order = 5)
    public void testGateRegisterRelationships() {

        Map<ICGate, Integer> gateIDLookup = scenario.rootFlatMap.getGates().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Map<ICRegister, Integer> registerIDLookup = scenario.rootFlatMap.getRegisters().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // Ensure 1-to-1 mapping between gate/registers and their IDs
        assertEquals(gateIDLookup.size(), scenario.rootFlatMap.getGates().size(), "Could not flip [gate -> gateID] map");
        assertEquals(registerIDLookup.size(), scenario.rootFlatMap.getRegisters().size(), "Could not flip [register -> registerID] map");

        for (ICGate gate : scenario.expectedGates) {
            Set<Integer> expectedWritesSet = scenario.gateWrites.get(gate).stream().map(registerIDLookup::get).collect(Collectors.toSet());
            Set<Integer> expectedReadsSet = scenario.gateReads.get(gate).stream().map(registerIDLookup::get).collect(Collectors.toSet());

            int gateID = gateIDLookup.get(gate);

            ArrayList<Integer> actualWrites = scenario.rootFlatMap.getGateDependents().get(gateID);
            Set<Integer> actualWritesSet = new HashSet<>(actualWrites);

            ArrayList<Integer> actualReads = scenario.rootFlatMap.getGateDependencies().get(gateID);
            Set<Integer> actualReadsSet = new HashSet<>(actualReads);

            assertEquals(expectedWritesSet, actualWritesSet, String.format("Gate %s with id %d has unexpected writes list", gate, gateID));
            assertEquals(expectedReadsSet.size(), actualReads.size(), String.format("Gate %s with id %d has unexpected reads list", gate, gateID));

            System.out.printf("Gate %s (id: %d) writes to %s and reads from %s%n", gate, gateID, actualWritesSet, actualReadsSet);
        }
    }
}