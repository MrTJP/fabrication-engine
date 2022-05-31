package mrtjp.fengine.api;

import java.util.Set;

public interface IPathFinderManifest {

    Set<Integer> getInputRegisters();
    Set<Integer> getInputRegistersIntersectingMasks(int inputDirMask, int inputPortMask, int outputDirMask, int outputPortMask);

    Set<Integer> getOutputRegisters();
    Set<Integer> getOutputRegistersIntersectingMasks(int inputDirMask, int inputPortMask, int outputDirMask, int outputPortMask);
}
