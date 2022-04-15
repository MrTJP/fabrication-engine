package mrtjp.fengine.framework.test;

import mrtjp.fengine.framework.api.*;

@FabricationTestClass
public class DynamicScenarioTests {

    private final DynamicScenario scenario;

    public DynamicScenarioTests(DynamicScenario scenario) {
        this.scenario = scenario;
    }

    @FabricationTest
    public void testA() {
        System.out.println("param1: " + scenario.param1 + ", param2: " + scenario.param2);
    }

    @FabricationScenario
    public static class DynamicScenario {

        public final int param1;
        public final int param2;

        //@formatter:off
        public DynamicScenario(
                @FabricationParameterIntArray ({ 0, 1, 2 })      int param1,
                @FabricationParameterIntRange (min = 4, max = 6) int param2) {
            this.param1 = param1;
            this.param2 = param2;
        }
        //@formatter:on
    }
}
