package mrtjp.fengine.tiles;

import mrtjp.fengine.api.PropagationFunction;

/**
 * A directionally connected wire that forwards propagation to the same port
 * <p>
 * - Signal can enter from masked direction from msked port
 * - Signal can exit to masked direction to the same port it entered from
 */
public abstract class FEPortPassthroughWireTile implements FETile {

    /**
     * Mask of allowed directions
     */
    protected abstract int getConnMask();

    /**
     * Mask of allowed ports
     */
    protected abstract int getPortMask();

    @Override
    public PropagationFunction propagationFunc(int inDir, int inPort) {
        return (outDir, outPort) ->
                (getPortMask() & 1 << inPort) != 0 && inPort == outPort &&
                        (getConnMask() & 1 << inDir) != 0 && (getConnMask() & 1 << outDir) != 0;
    }
}
