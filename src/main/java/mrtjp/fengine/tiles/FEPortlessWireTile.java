package mrtjp.fengine.tiles;

import mrtjp.fengine.api.PropagationFunction;

public abstract class FEPortlessWireTile implements FETile {

    /**
     * Mask of allowed directions
     */
    protected abstract int getConnMask();

    @Override
    public PropagationFunction propagationFunc(int inDir, int inPort) {
        return ((outDir, outPort) -> (getConnMask() & 1 << inDir) != 0 && (getConnMask() & 1 << outDir) != 0);
    }
}
