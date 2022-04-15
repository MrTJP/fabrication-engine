package mrtjp.fengine.framework.test;

import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.framework.api.FabricationTest;
import mrtjp.fengine.framework.api.FabricationTestClass;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FabricationTestClass
public class ScenarioPermanenceTests {

    private final SomeTestScenario scenario;
    private final List<SomeTestScenario> scenarioList = new ArrayList<>();

    public ScenarioPermanenceTests(SomeTestScenario scenario) {
        this.scenario = scenario;
    }

    @FabricationTest
    public void testA() {
        scenarioList.add(scenario);
        scenario.i++;
    }

    @FabricationTest
    public void testB() {
        scenarioList.add(scenario);
        scenario.i++;
    }

    @FabricationTest
    public void testC() {
        scenarioList.add(scenario);
        scenario.i++;
    }

    @FabricationTest (order = 999)
    public void testScenarioPermanence() {
        assertEquals(3, scenarioList.size());
        assertEquals(3, scenario.i);
        assertTrue(scenarioList.stream().allMatch(c -> c == scenario));
    }

    @FabricationScenario
    public static class SomeTestScenario {

        public int i = 0;
    }
}
