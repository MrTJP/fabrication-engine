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
    public void allocate(Allocator allocator) {
        regId = allocator.allocRegisterID(ioRegId);
    }

    @Override
    public void locate(PathFinder pathFinder) {
        if (!isGlobalInput) { //Pathfinding only necessary for output IO tile, since they have an incoming register
            PathFinderResult pfr = pathFinder.doPathFinding((d, p) -> d == ioDir);
            if (pfr.inputRegisters.size() > 1) {
                System.out.println("ERR: Unexpected multiple drivers: " + pfr.inputRegisters);
            }
            if (!pfr.inputRegisters.isEmpty()) {
                regId = pfr.inputRegisters.get(0);
            }
        }
    }

    @Override
    public void registerRemaps(RemapRegistry remapRegistry) {
        if (regId > -1) {
            remapRegistry.addRemap(regId, ioRegId); // Remap the actual found regID to the expected, statically-assigned ID
        }
    }

    @Override
    public void consumeRemaps(RemapProvider remapProvider) {
        regId = remapProvider.getRemappedRegisterID(regId);
    }

    @Override
    public void collect(Collector collector) {
        if (isGlobalInput) {
            if (regId > -1) {
                collector.addRegister(regId, register); // Auto-dropped if this is not the top-level map
            }
        }
    }
}
