package mrtjp.fengine.framework.resolvers;

import org.junit.platform.engine.DiscoverySelector;

import java.lang.reflect.Constructor;

public class DynamicScenarioSelector implements DiscoverySelector {

    public final Class<?> testClass;
    public final Class<?> scenarioClass;
    public final Constructor<?> scenarioConstructor;

    public DynamicScenarioSelector(Class<?> testClass, Class<?> scenarioClass, Constructor<?> scenarioConstructor) {
        this.testClass = testClass;
        this.scenarioClass = scenarioClass;
        this.scenarioConstructor = scenarioConstructor;
    }
}
