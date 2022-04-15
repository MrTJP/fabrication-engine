package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.api.FabricationTest;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ScenarioResolver implements SelectorResolver {

    @Override
    public Resolution resolve(DiscoverySelector selector, Context context) {

        if (!(selector instanceof ScenarioSelector)) {
            return Resolution.unresolved();
        }

        ScenarioSelector scenarioSelector = (ScenarioSelector) selector;

        // Add Scenario Descriptor to parent
        Optional<ScenarioDescriptor> scenarioDesc = context.addToParent(p -> Optional.of(new ScenarioDescriptor(
                p.getUniqueId().append("scenario", scenarioSelector.scenarioClass.getName()).append("variant", "" + scenarioSelector.variant),
                scenarioSelector.scenarioClass, scenarioSelector.scenarioConstructor, scenarioSelector.scenarioParams, scenarioSelector.variant
        )));
        assert scenarioDesc.isPresent();

        // Collect all annotated test methods in test class
        List<Method> testMethods = Arrays.stream(scenarioSelector.testClass.getMethods())
                .filter(c -> c.getAnnotation(FabricationTest.class) != null)
                .collect(Collectors.toList());

        if (testMethods.isEmpty()) {
            throw new RuntimeException("Annotated test class " + scenarioSelector.testClass.getName() + " must have at least 1 method annotated with @" + FabricationTest.class.getSimpleName());
        }

        for (Method m : testMethods) {
            if (m.getParameterCount() != 0)
                throw new RuntimeException("Annotated test method " + m.getName() + " in class " + scenarioSelector.testClass.getName() + " cannot have parameters");
        }

        Set<TestMethodSelector> methodSelectors = testMethods.stream()
                .map(c -> new TestMethodSelector(scenarioSelector, c, c.getAnnotation(FabricationTest.class).order()))
                .collect(Collectors.toSet());

        return Resolution.match(Match.exact(scenarioDesc.get(), () -> methodSelectors));
    }
}
