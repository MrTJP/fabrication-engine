package mrtjp.fengine.framework.resolvers;

import org.junit.platform.engine.TestDescriptor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestMethodOrderingVisitor implements TestDescriptor.Visitor {

    @Override
    public void visit(TestDescriptor descriptor) {
        if (!(descriptor instanceof ScenarioDescriptor)) { return; }

        ScenarioDescriptor scenarioDesc = (ScenarioDescriptor) descriptor;

        Set<? extends TestDescriptor> children = scenarioDesc.getChildren();
        List<TestMethodDescriptor> sortedChildren = children.stream()
                .filter(c -> c instanceof TestMethodDescriptor)
                .map(c -> (TestMethodDescriptor) c)
                .sorted(Comparator.<TestMethodDescriptor>comparingInt(c -> c.order).thenComparing(c -> c.testMethod.getName(), Comparator.naturalOrder()))
                .collect(Collectors.toList());

        if (children.size() != sortedChildren.size()) { throw new RuntimeException("Descriptor " + scenarioDesc + " had children that were not instances of " + TestMethodDescriptor.class.getSimpleName()); }

        for (TestDescriptor c : sortedChildren) { scenarioDesc.removeChild(c); }

        for (TestDescriptor c : sortedChildren) { scenarioDesc.addChild(c); }
    }
}
