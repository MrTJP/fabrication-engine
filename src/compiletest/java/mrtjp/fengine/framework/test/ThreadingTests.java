package mrtjp.fengine.framework.test;

import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.framework.api.FabricationTest;
import mrtjp.fengine.framework.api.FabricationTestClass;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@FabricationTestClass
public class ThreadingTests {

    public final ThreadingTestScenario scenario;
    private final List<String> threadNames = new ArrayList<>();

    public ThreadingTests(ThreadingTestScenario scenario) {
        this.scenario = scenario;
    }

    @FabricationTest
    public void testA() {
        threadNames.add(Thread.currentThread().getName());
    }

    @FabricationTest
    public void testB() {
        threadNames.add(Thread.currentThread().getName());
    }

    @FabricationTest
    public void testC() {
        threadNames.add(Thread.currentThread().getName());
    }

    @FabricationTest (order = 999)
    public void testSameThreadExecutedAllMethods() {
        String currentThreadName = Thread.currentThread().getName();
        assertTrue(threadNames.stream().allMatch(currentThreadName::equals));
    }

    @FabricationScenario
    public static class ThreadingTestScenario {
        // Dummy
    }
}
