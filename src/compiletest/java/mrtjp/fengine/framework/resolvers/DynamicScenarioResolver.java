package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.api.FabricationParameterIntArray;
import mrtjp.fengine.framework.api.FabricationParameterIntRange;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DynamicScenarioResolver implements SelectorResolver {

    @Override
    public Resolution resolve(DiscoverySelector selector, Context context) {

        if (!(selector instanceof DynamicScenarioSelector)) { return Resolution.unresolved(); }

        DynamicScenarioSelector dynamicScenarioSelector = (DynamicScenarioSelector) selector;

        // Add Dynamic Scenario Descriptor to parent
        Optional<DynamicScenarioDescriptor> scenarioDesc = context.addToParent(p -> Optional.of(new DynamicScenarioDescriptor(
                p.getUniqueId().append("dynamicScenario", dynamicScenarioSelector.scenarioClass.getName()),
                dynamicScenarioSelector.scenarioClass, dynamicScenarioSelector.scenarioConstructor
        )));
        assert scenarioDesc.isPresent();

        // Obtain array of parameters, where each parameter is an array of possible inputs
        Object[][] allParameterInputs = getParamInputs(dynamicScenarioSelector.scenarioConstructor);

        // Get combo list
        Object[][] comboList = resolveComboList(allParameterInputs);

        // Selectors
        Set<ScenarioSelector> selectorSet = IntStream.range(0, comboList.length)
                .mapToObj(i -> new ScenarioSelector(dynamicScenarioSelector.testClass, dynamicScenarioSelector.scenarioClass, dynamicScenarioSelector.scenarioConstructor, comboList[i], i))
                .collect(Collectors.toSet());

        return Resolution.match(Match.exact(scenarioDesc.get(), () -> selectorSet));
    }

    private Object[][] resolveComboList(Object[][] inputs) {

        int numCombos = Arrays.stream(inputs).map(c -> c.length).reduce(1, (acc, val) -> acc * val);
        int[] indices = new int[inputs.length];
        Arrays.fill(indices, 0);

        List<Object[]> comboList = new LinkedList<>();
        for (int c = 0; c < numCombos; c++) {

            Object[] combo = IntStream.range(0, inputs.length)
                    .mapToObj(j -> inputs[j][indices[j]])
                    .toArray();

            comboList.add(combo);

            int i = 0;
            while (i < indices.length) {
                indices[i] = (indices[i] + 1) % inputs[i].length;
                if (indices[i++] != 0) break;
            }
        }

        return comboList.toArray(new Object[comboList.size()][]);
    }

    private Object[][] getParamInputs(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        if (parameters.length == 0) { throw new RuntimeException("Dynamic Scenario Constructor " + constructor.getName() + "must have at least 1 parameter"); }

        // Create a list of
        Object[][] parameterValueList = Arrays.stream(parameters)
                .map(p -> Arrays.stream(p.getAnnotations()).flatMap(a -> {
                    if (a instanceof FabricationParameterIntArray) return Arrays.stream(((FabricationParameterIntArray) a).value()).boxed();
                    if (a instanceof FabricationParameterIntRange) return IntStream.range(((FabricationParameterIntRange) a).min(), ((FabricationParameterIntRange) a).max()+1).boxed();
                    return Stream.empty();
                }).toArray())
                .toArray(Object[][]::new);

        // Make sure all parameters have at least 1 resolution
        for (Object[] values : parameterValueList) {
            if (values.length == 0)
                throw new RuntimeException("Constructor " + constructor.getName() + " has at least 1 parameter that couldn't be resolved");
        }

        return parameterValueList;
    }
}
