package mrtjp.fengine.framework.resolvers;

import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.framework.api.FabricationTestClass;
import org.junit.platform.commons.util.ClassFilter;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestClassResolver implements SelectorResolver {

    @Override
    public Resolution resolve(ClassSelector selector, Context context) {

        Class<?> testClass = selector.getJavaClass();

        // Check annotation
        Annotation testClassAnnotation = testClass.getAnnotation(FabricationTestClass.class);
        if (testClassAnnotation == null) {
            return Resolution.unresolved();
        }

        // Make sure constructor can accept a scenario object
        Constructor<?>[] constructors = testClass.getConstructors();
        if (constructors.length != 1 || constructors[0].getParameterCount() != 1 ) {
            throw new RuntimeException("Class annotated with @FabricationTestSuite must have single constructor with single parameter");
        }

        // Add descriptor to parent
        Optional<TestClassDescriptor> testClassDesc = context.addToParent(p -> Optional.of(new TestClassDescriptor(
                p.getUniqueId().append("class", testClass.getName()),
                testClass
        )));
        assert testClassDesc.isPresent();

        // Find all compatible scenario classes
        Constructor<?> constructor = constructors[0];
        Class<?> constructorParamType = constructor.getParameterTypes()[0];
        URI classpathRootURI = null;
        try {
            URL classpathRootURL = testClass.getResource("/");
            if (classpathRootURL != null)
                classpathRootURI = classpathRootURL.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (classpathRootURI == null) {
            throw new RuntimeException("Error getting classpath root of class " + testClass.getName());
        }

        List<Class<?>> scenarioClasses = ReflectionUtils.findAllClassesInClasspathRoot(classpathRootURI, ClassFilter.of(c ->
                    c.getAnnotation(FabricationScenario.class) != null && constructorParamType.isAssignableFrom(c)));

        // Verify all found scenario classes are valid
        for (Class<?> c : scenarioClasses) {
            if (c.getConstructors().length != 1)
                throw new RuntimeException("Scenario class " + c.getName() + " must have 1 constructor");
        }

        // Dynamic scenarios need further resolution. Non-dynamic scenarios can skip DynamicScenarioResolver
        Set<DiscoverySelector> scenarioSelectors = scenarioClasses.stream()
                .map(c -> (DiscoverySelector) (c.getConstructors()[0].getParameterCount() == 0 ?
                        new ScenarioSelector(testClass, c, c.getConstructors()[0], new Object[0], 0) :
                        new DynamicScenarioSelector(testClass, c, c.getConstructors()[0])))
                .collect(Collectors.toSet());

        return Resolution.match(Match.exact(testClassDesc.get(), () -> scenarioSelectors));
    }
}
