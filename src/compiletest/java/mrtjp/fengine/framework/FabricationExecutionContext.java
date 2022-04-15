package mrtjp.fengine.framework;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FabricationExecutionContext implements EngineExecutionContext {

    // Test class
    private Class<?> testClass;

    // Scenario
    private Class<?> scenarioClass;
    private Constructor<?> scenarioConstructor;
    private Object[] scenarioParams;

    // Test method
    private Method testMethod;

    // Instantiated test objects
    private Object scenarioObj;
    private Object testObj;

    /* Context prep methods */

    public FabricationExecutionContext setTestClass(Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }

    public FabricationExecutionContext setScenario(Class<?> scenarioClass, Constructor<?> scenarioConstructor, Object[] scenarioParams) {
        this.scenarioClass = scenarioClass;
        this.scenarioConstructor = scenarioConstructor;
        this.scenarioParams = scenarioParams;
        return this;
    }

    public FabricationExecutionContext setMethod(Method testMethod) {
        this.testMethod = testMethod;
        return this;
    }

    /* Context execution */

    public void constructTestObject() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        scenarioObj = scenarioConstructor.newInstance(scenarioParams);
        testObj = testClass.getConstructors()[0].newInstance(scenarioObj);
    }

    public void executeTestMethod() throws InvocationTargetException, IllegalAccessException {
        testMethod.invoke(testObj);
    }

    public FabricationExecutionContext copy() {
        return new FabricationExecutionContext()
                .setTestClass(testClass)
                .setScenario(scenarioClass, scenarioConstructor, scenarioParams)
                .setMethod(testMethod);
    }

    @Override
    public String toString() {
        return "FabricationExecutionContext" + "(" + super.toString() + ")[" +
                "testClass = " + (testClass == null ? "null" : testClass.getName()) + ", " +
                "scenarioClass = " + (scenarioClass == null ? "null" : scenarioClass.getName()) + ", " +
                "testMethod = " + (testMethod == null ? "null" : testMethod.getName()) + ", " +
                "testObj = " + (testObj == null ? "null" : testObj.toString()) + "]";
    }
}
