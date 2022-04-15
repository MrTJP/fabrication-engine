package mrtjp.fengine.framework.resolvers;

import org.junit.platform.engine.DiscoverySelector;

import java.lang.reflect.Constructor;

public class ScenarioSelector implements DiscoverySelector {

    public final Class<?> testClass;
    public final Class<?> scenarioClass;
    public final Constructor<?> scenarioConstructor;
    public final Object[] scenarioParams;
    public final int variant;

    public ScenarioSelector(Class<?> testClass, Class<?> scenarioClass, Constructor<?> scenarioConstructor, Object[] scenarioParams, int variant) {
        this.testClass = testClass;
        this.scenarioClass = scenarioClass;
        this.scenarioConstructor = scenarioConstructor;
        this.scenarioParams = scenarioParams;
        this.variant = variant;
    }
}
