package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.FabricationExecutionContext;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.engine.support.hierarchical.Node;

import java.lang.reflect.Method;

public class TestMethodDescriptor extends AbstractTestDescriptor implements Node<FabricationExecutionContext> {

    public final Method testMethod;
    public final int order;

    public TestMethodDescriptor(UniqueId uniqueId, Method testMethod, int order) {
        super(uniqueId, testMethod.getName(), MethodSource.from(testMethod));
        this.testMethod = testMethod;
        this.order = order;
    }

    @Override
    public FabricationExecutionContext prepare(FabricationExecutionContext context) {
        return context.setMethod(testMethod);
    }

    @Override
    public FabricationExecutionContext execute(FabricationExecutionContext context, DynamicTestExecutor dynamicTestExecutor) throws Exception {
        context.executeTestMethod();
        return context;
    }

    //@formatter:off
    @Override public Type getType() { return Type.TEST; }
    @Override public ExecutionMode getExecutionMode() { return ExecutionMode.SAME_THREAD; }
    //@formatter:on
}
