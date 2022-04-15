package mrtjp.fengine.testimpl;

import mrtjp.fengine.tiles.FEPortPassthroughWireTile;

public class PortPassthroughWireTileImpl extends FEPortPassthroughWireTile {

    private final int connMask;
    private final int portMask;

    public PortPassthroughWireTileImpl(int connMask, int portMask) {
        this.connMask = connMask;
        this.portMask = portMask;
    }

    //@formatter:off
    @Override protected int getConnMask() { return connMask; }
    @Override protected int getPortMask() { return portMask; }
    //@formatter:on
}
