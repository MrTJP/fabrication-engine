package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.FabricationExecutionContext;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;

import java.lang.reflect.Constructor;

public class DynamicScenarioDescriptor extends AbstractTestDescriptor implements Node<FabricationExecutionContext> {

    public final Class<?> scenarioClass;
    public final Constructor<?> scenarioConstructor;

    public DynamicScenarioDescriptor(UniqueId uniqueId, Class<?> scenarioClass, Constructor<?> scenarioConstructor) {
        super(uniqueId, scenarioClass.getName(), ClassSource.from(scenarioClass));
        this.scenarioClass = scenarioClass;
        this.scenarioConstructor = scenarioConstructor;
    }

    //@formatter:off
    @Override public Type getType() { return Type.CONTAINER; }
    @Override public ExecutionMode getExecutionMode() { return ExecutionMode.SAME_THREAD; }
    //@formatter:on
}
