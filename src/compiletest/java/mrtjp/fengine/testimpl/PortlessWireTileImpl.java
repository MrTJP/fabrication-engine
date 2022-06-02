package mrtjp.fengine.testimpl;

import mrtjp.fengine.api.IPathFinder;
import mrtjp.fengine.api.IPathFinderManifest;
import mrtjp.fengine.tiles.FEPortlessWireTile;

import java.util.HashSet;
import java.util.Set;

public class PortlessWireTileImpl extends FEPortlessWireTile {

    private final int connMask;

    public final Set<Integer> inputRegisters = new HashSet<>();

    public PortlessWireTileImpl(int connMask) {
        this.connMask = connMask;
    }

    @Override
    public void locate(IPathFinder pathFinder) {
        inputRegisters.clear();
    }

    @Override
    public void searchManifest(IPathFinderManifest manifest) {
        inputRegisters.addAll(manifest.getOutputRegisters()); // Found output registers feed into this wire as inputs
    }

    //@formatter:off
    @Override protected int getConnMask() { return connMask; }
    //@formatter:on
}
