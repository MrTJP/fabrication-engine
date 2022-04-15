package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.FabricationExecutionContext;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ScenarioDescriptor extends AbstractTestDescriptor implements Node<FabricationExecutionContext> {

    public final Class<?> scenarioClass;
    public final Constructor<?> scenarioConstructor;
    public final Object[] scenarioParams;
    public final int variant;

    public ScenarioDescriptor(UniqueId uniqueId, Class<?> scenarioClass, Constructor<?> scenarioConstructor, Object[] scenarioParams, int variant) {
        super(uniqueId, scenarioClass.getName() + (scenarioParams.length > 0 ? Arrays.toString(scenarioParams) : ""), ClassSource.from(scenarioClass));

        this.scenarioClass = scenarioClass;
        this.scenarioConstructor = scenarioConstructor;
        this.scenarioParams = scenarioParams;
        this.variant = variant;
    }

    @Override
    public FabricationExecutionContext prepare(FabricationExecutionContext context) {
        return context.copy().setScenario(scenarioClass, scenarioConstructor, scenarioParams);
    }

    @Override
    public FabricationExecutionContext before(FabricationExecutionContext context) throws Exception {
        context.constructTestObject();
        return context;
    }

    //@formatter:off
    @Override public Type getType() { return Type.CONTAINER; }
    @Override public ExecutionMode getExecutionMode() { return ExecutionMode.SAME_THREAD; }
    //@formatter:on
}
