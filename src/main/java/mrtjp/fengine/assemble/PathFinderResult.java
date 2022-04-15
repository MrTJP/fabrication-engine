package mrtjp.fengine.assemble;

import java.util.List;

public class PathFinderResult {

    public final List<Integer> outputRegisters;
    public final List<Integer> inputRegisters;

    public PathFinderResult(List<Integer> outputRegisters, List<Integer> inputRegisters) {
        this.outputRegisters = outputRegisters;
        this.inputRegisters = inputRegisters;
    }
}
