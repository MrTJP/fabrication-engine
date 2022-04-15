package mrtjp.fengine.framework.api;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Testable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FabricationTest
{
    /**
     * Optionally declare a method order when executing tests withing a
     * Fabrication Test Suite. Lower order number gets higher priority.
     *
     * Multiple methods declaring the same order
     * value will execute by sorting method names in alphabetical order.
     *
     * @return Ordering value
     */
    int order() default 0;
}
