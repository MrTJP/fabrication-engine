package mrtjp.fengine.api;

public interface PropagationFunction {

    boolean canPropagate(int outDir, int outPort);
}
