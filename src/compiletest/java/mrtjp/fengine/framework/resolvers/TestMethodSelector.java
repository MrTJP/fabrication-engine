package mrtjp.fengine.framework.resolvers;

import org.junit.platform.engine.DiscoverySelector;

import java.lang.reflect.Method;

public class TestMethodSelector implements DiscoverySelector {

    public final ScenarioSelector scenarioSelector;
    public final Method testMethod;
    public final int order;

    public TestMethodSelector(ScenarioSelector scenarioSelector, Method testMethod, int order) {
        this.scenarioSelector = scenarioSelector;
        this.testMethod = testMethod;
        this.order = order;
    }
}
