package mrtjp.fengine.framework.resolvers;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.util.Optional;

public class TestMethodResolver implements SelectorResolver {

    @Override
    public Resolution resolve(DiscoverySelector selector, Context context) {

        if (!(selector instanceof TestMethodSelector)) return Resolution.unresolved();
        TestMethodSelector testMethodSelector = (TestMethodSelector) selector;

        // Add the descriptor
        Optional<TestMethodDescriptor> testMethodDesc = context.addToParent(p -> Optional.of(new TestMethodDescriptor(
                p.getUniqueId().append("testMethod", testMethodSelector.testMethod.getName()),
                testMethodSelector.testMethod, testMethodSelector.order
        )));
        assert testMethodDesc.isPresent();

        // Match on this descriptor as a leaf node
        return Resolution.match(Match.exact(testMethodDesc.get()));
    }
}
