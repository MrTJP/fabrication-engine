package mrtjp.fengine.testimpl;

import mrtjp.fengine.api.IPathFinder;
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
        return isGlobalInput && outDir == ioDir ? Optional.of(regId) : Optional.empty();
    }

    @Override
    public Optional<Integer> getInputRegister(int inDir, int inPort) {
        return !isGlobalInput && inDir == ioDir ? Optional.of(regId) : Optional.empty();
    }

    @Override
    public void allocate(Allocator allocator) {
        if (isGlobalInput) {
            regId = allocator.allocRegisterID(ioRegId);
        } else {
            regId = -1; // located with pathfinder, then remapped to ioRegId
        }
    }

    @Override
    public void locate(IPathFinder pathFinder) {
        if (!isGlobalInput) { //Pathfinding only necessary for output IO tile, since they have an incoming register
            PathFinderResult pfr = pathFinder.doPathFinding((d, p) -> d == ioDir);
            if (pfr.outputRegisters.size() > 1) {
                System.out.println("ERR: Unexpected multiple drivers: " + pfr.outputRegisters);
            }
            if (!pfr.outputRegisters.isEmpty()) {
                regId = pfr.outputRegisters.get(0);
            }
        }
    }

    @Override
    public void registerRemaps(RemapRegistry remapRegistry) {
        if (!isGlobalInput) {
            // TODO test case for when Input IO gate is connected directly to output IO gate
            //      (Remap request should be ignored)
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
            collector.addRegister(regId, register); // Auto-dropped if this is not the top-level map
        }
    }
}
