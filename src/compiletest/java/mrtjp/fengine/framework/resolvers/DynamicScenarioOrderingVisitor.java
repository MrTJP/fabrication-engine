package mrtjp.fengine.framework.resolvers;

import org.junit.platform.engine.TestDescriptor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicScenarioOrderingVisitor implements TestDescriptor.Visitor {

    @Override
    public void visit(TestDescriptor descriptor) {

        if (!(descriptor instanceof DynamicScenarioDescriptor)) { return; }

        DynamicScenarioDescriptor dynamicScenarioDescriptor = (DynamicScenarioDescriptor) descriptor;

        Set<? extends TestDescriptor> children = dynamicScenarioDescriptor.getChildren();
        List<ScenarioDescriptor> sortedChildren = children.stream()
                .filter(c -> c instanceof ScenarioDescriptor)
                .map(c -> (ScenarioDescriptor) c)
                .sorted(Comparator.comparingInt(c -> c.variant))
                .collect(Collectors.toList());

        if (children.size() != sortedChildren.size()) { throw new RuntimeException("Descriptor " + descriptor + " had children that were not instances of " + ScenarioDescriptor.class.getSimpleName()); }

        for (TestDescriptor c : sortedChildren) { dynamicScenarioDescriptor.removeChild(c); }

        for (TestDescriptor c : sortedChildren) { dynamicScenarioDescriptor.addChild(c); }
    }
}
