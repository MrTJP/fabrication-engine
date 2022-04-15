package mrtjp.fengine.testimpl;

import mrtjp.fengine.tiles.FEPortlessWireTile;

public class PortlessWireTileImpl extends FEPortlessWireTile {

    private final int connMask;

    public PortlessWireTileImpl(int connMask) {
        this.connMask = connMask;
    }

    //@formatter:off
    @Override protected int getConnMask() { return connMask; }
    //@formatter:on
}
