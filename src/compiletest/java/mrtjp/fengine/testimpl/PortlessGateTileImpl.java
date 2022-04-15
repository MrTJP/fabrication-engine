package mrtjp.fengine.testimpl;

import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.simulate.NoOpGate;
import mrtjp.fengine.simulate.StaticByteRegister;
import mrtjp.fengine.tiles.FEPortlessGateTile;

import java.util.ArrayList;

public class PortlessGateTileImpl extends FEPortlessGateTile {

    public final ICGate gate = new NoOpGate();
    public final ICRegister[] registers = new ICRegister[6];

    private final String label;
    private final int inDirMask;
    private final int outDirMask;

    public PortlessGateTileImpl(String label, int inDirMask, int outDirMask) {
        this.label = label;
        this.inDirMask = inDirMask;
        this.outDirMask = outDirMask;

        for (int dir = 0; dir < 6; dir++) {
            registers[dir] = new StaticByteRegister((byte) 0);
        }
    }

    //@formatter:off
    @Override protected int getOutDirMask() { return outDirMask; }
    @Override protected int getInDirMask() { return inDirMask; }
    @Override protected ICRegister createRegister(int dir) { return registers[dir]; }
    @Override protected ICGate createGate(ArrayList<Integer> inputs, ArrayList<Integer> outputs) { return gate; }
    //@formatter:on
}
