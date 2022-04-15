package mrtjp.fengine.framework.test;

import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.framework.api.FabricationTest;
import mrtjp.fengine.framework.api.FabricationTestClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FabricationTestClass
public class OrderingTests {

    private final OrderingScenario scenario;
    private final List<String> finishedTests = new ArrayList<>();

    public OrderingTests(OrderingScenario scenario) {
        this.scenario = scenario;
    }

    @FabricationTest
    public void testA() {
        finishedTests.add("A");
    }

    @FabricationTest
    public void testB() {
        finishedTests.add("B");
    }

    @FabricationTest (order = 1)
    public void testC() {
        finishedTests.add("C");
    }

    @FabricationTest (order = 1)
    public void testD() {
        finishedTests.add("D");
    }

    @FabricationTest (order = 3)
    public void testE() {
        finishedTests.add("E");
    }

    @FabricationTest (order = 2)
    public void testF() {
        finishedTests.add("F");
    }

    @FabricationTest (order = 999)
    public void testCorrectMethodExecutionOrder() {
        assertEquals(Arrays.asList("A", "B", "C", "D", "F", "E"), finishedTests);
    }

    @FabricationScenario
    public static class OrderingScenario {
    }
}
