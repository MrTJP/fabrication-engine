package mrtjp.fengine.simulate;

import java.util.Set;

public interface ICSimulationCallback {

    void registersDidChange(Set<Integer> registers);

    void icDidThrowErrorFlag(int flag, Set<Integer> registers, Set<Integer> gates);

    void icEventComputeOverflow(Set<Integer> registers, Set<Integer> gates, int computeLimit);
}
