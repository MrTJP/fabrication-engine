package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.FabricationExecutionContext;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;

public class TestClassDescriptor extends AbstractTestDescriptor implements Node<FabricationExecutionContext> {

    private final Class<?> testClass;

    public TestClassDescriptor(UniqueId uniqueId, Class<?> testClass) {
        super(uniqueId, testClass.getName(), ClassSource.from(testClass));
        this.testClass = testClass;
    }

    @Override
    public FabricationExecutionContext prepare(FabricationExecutionContext context) {
        return context.copy().setTestClass(testClass);
    }

    //@formatter:off
    @Override public Type getType() { return Type.CONTAINER; }
    @Override public ExecutionMode getExecutionMode() { return ExecutionMode.SAME_THREAD; }
    //@formatter:on
}
