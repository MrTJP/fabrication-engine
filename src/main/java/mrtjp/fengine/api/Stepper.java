package mrtjp.fengine.api;

public interface Stepper {

    void stepOver();

    void stepIn();

    void stepOut();

    int stepsRemaining();

    default boolean isDone() {
        return stepsRemaining() == 0;
    }
}
