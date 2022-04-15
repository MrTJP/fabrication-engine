package mrtjp.fengine.framework;

import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.framework.api.FabricationTestClass;
import mrtjp.fengine.framework.resolvers.*;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;
import org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutorService;

import java.util.Optional;

public class FabricationTestCaseEngine extends HierarchicalTestEngine<FabricationExecutionContext> {

    private static final EngineDiscoveryRequestResolver<EngineDescriptor> RESOLVER =
            EngineDiscoveryRequestResolver.<EngineDescriptor>builder()
                    .addClassContainerSelectorResolver(e ->
                            e.getAnnotation(FabricationTestClass.class) != null ||
                                    e.getAnnotation(FabricationScenario.class) != null)
                    .addSelectorResolver(new TestClassResolver())
                    .addSelectorResolver(new ScenarioResolver())
                    .addSelectorResolver(new DynamicScenarioResolver())
                    .addSelectorResolver(new TestMethodResolver())
                    .addTestDescriptorVisitor(e -> new TestMethodOrderingVisitor())
                    .addTestDescriptorVisitor(e -> new DynamicScenarioOrderingVisitor())
                    .build();

    @Override
    protected FabricationExecutionContext createExecutionContext(ExecutionRequest request) {
        return new FabricationExecutionContext();
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        EngineDescriptor desc = new EngineDescriptor(uniqueId, "Fabrication Test Cases");
        RESOLVER.resolve(discoveryRequest, desc);
        return desc;
    }

    @Override
    protected HierarchicalTestExecutorService createExecutorService(ExecutionRequest request) {
        return new ForkJoinPoolHierarchicalTestExecutorService(request.getConfigurationParameters());
    }

    //@formatter:off
    @Override public String getId() { return "fabrication-testcase-engine"; }
    @Override public Optional<String> getGroupId() { return Optional.of("mrtjp.fengine.framework"); }
    @Override public Optional<String> getArtifactId() { return Optional.of("testcase-engine"); }
    //@formatter:on
}
