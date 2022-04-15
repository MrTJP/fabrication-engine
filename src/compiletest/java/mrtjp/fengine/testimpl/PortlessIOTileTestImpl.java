package mrtjp.fengine.testimpl;

import mrtjp.fengine.api.ICAssembler;
import mrtjp.fengine.assemble.PathFinder;
import mrtjp.fengine.assemble.PathFinderResult;
import mrtjp.fengine.simulate.StaticByteRegister;
import mrtjp.fengine.tiles.FETile;

import java.util.Optional;

public class PortlessIOTileTestImpl implements FETile {

    private int regId = -1;
    public final StaticByteRegister register = new StaticByteRegister((byte) 0);

    private final String label;
    private final boolean isGlobalInput;
    private final int ioDir;
    private final int ioRegId;

    public PortlessIOTileTestImpl(String label, boolean isGlobalInput, int ioDir, int ioRegId) {
        this.label = label;
        this.isGlobalInput = isGlobalInput;
        this.ioDir = ioDir;
        this.ioRegId = ioRegId;
    }

    @Override
    public Optional<Integer> getOutputRegister(int outDir, int outPort) {
        return regId > -1 && isGlobalInput && outDir == ioDir ? Optional.of(regId) : Optional.empty();
    }

    @Override
    public Optional<Integer> getInputRegister(int inDir, int inPort) {
        return regId > -1 && !isGlobalInput && inDir == ioDir ? Optional.of(regId) : Optional.empty();
    }

    @Override
    public void allocate(ICAssembler assembler) {
        regId = assembler.allocRegisterID(ioRegId);
    }

    @Override
    public void locate(ICAssembler assembler, PathFinder pathFinder) {
        if (!isGlobalInput) { //Pathfinding only necessary for output IO tile, since they have an incoming register
            PathFinderResult pfr = pathFinder.doPathFinding((d, p) -> d == ioDir);
            if (pfr.inputRegisters.size() > 1) {
                System.out.println("ERR: Unexpected multiple drivers: " + pfr.inputRegisters);
            }
            if (!pfr.inputRegisters.isEmpty()) {
                regId = pfr.inputRegisters.get(0);
            }
            if (regId > -1) {
                assembler.addRemap(regId, ioRegId); // Remap the actual found regID to the expected, statically-assigned ID
            }
        }
    }

    @Override
    public void remap(ICAssembler assembler) {
        regId = assembler.getRemappedRegisterID(regId);
    }

    @Override
    public void collect(ICAssembler assembler) {
        if (isGlobalInput) {
            // TODO This logic prevents IO tiles in nested maps to add a redundant register,
            //      since it is mapped to one provided by one-level-up map that is connecting
            //      into this one. This should be handled by the assembler. Basically, if regID
            //      already has a register, then don't add it again. Also make sure they
            //      are compatible or something.
            if (regId > -1 && assembler.getMapIndex() == 0) { //Only run this on top-level maps
                assembler.addRegister(regId, register);
            }
        }
    }
}
