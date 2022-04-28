package mrtjp.fengine.tiles;

import mrtjp.fengine.assemble.PathFinder;
import mrtjp.fengine.assemble.PathFinderResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class FEPortlessNestedMapTile implements FETile {

    private final int[] inputRegisters = new int[6];
    private final int[] outputRegisters = new int[6];

    public FEPortlessNestedMapTile() {
        Arrays.fill(inputRegisters, -1);
        Arrays.fill(outputRegisters, -1);
    }

    /**
     * Mask of outputs. Registers are allocated and created
     */
    abstract int getOutDirMask();

    /**
     * Mask of inputs. Registers are obtained via pathfinding
     */
    abstract int getInDirMask();

    /**
     * Maps [inDir -> nestedRegID] where:
     * <li> nestedRegID is a register ID from inside the nested map
     * <li> inDir is a signal's input direction to this tile
     */
    abstract Map<Integer, Integer> getInputSignalMap();

    /**
     * Maps [outDir -> nestedRegID] where:
     * <li> nestedRegID is a register ID from inside the nested map
     * <li> outDir is a signal's output direction from this tile
     */
    abstract Map<Integer, Integer> getOutputSignalMap();

    /**
     * Override point for injecting the nested element into the assembler.
     *  @param collector Receives the nested map
     * @param remaps    Remaps as determined by the signal maps above
     */
    abstract void addMapToAssembler(Collector collector, Map<Integer, Integer> remaps);

    @Override
    public Optional<Integer> getInputRegister(int inDir, int inPort) {
        int regId = inputRegisters[inDir];
        return ((getInDirMask() & 1 << inDir) != 0 && regId > -1) ? Optional.of(regId) : Optional.empty();
    }

    @Override
    public Optional<Integer> getOutputRegister(int outDir, int outPort) {
        int regId = outputRegisters[outDir];
        return ((getOutDirMask() & 1 << outDir) != 0 && regId > -1) ? Optional.of(regId) : Optional.empty();
    }

    @Override
    public void allocate(Allocator allocator) {
        for (int dir = 0; dir < 6; dir++) {
            outputRegisters[dir] = (getOutDirMask() & 1 << dir) != 0 ? allocator.allocRegisterID() : -1;
            inputRegisters[dir] = -1;
        }
    }

    @Override
    public void locate(PathFinder pathFinder) {
        for (int dir = 0; dir < 6; dir++) {
            if ((getInDirMask() & 1 << dir) != 0) {
                int finalDir = dir;
                PathFinderResult pfr = pathFinder.doPathFinding((d, p) -> d == finalDir);
                if (pfr.inputRegisters.size() > 1) {
                    System.out.println("ERR: Unexpected multiple drivers: " + pfr.inputRegisters);
                }
                if (!pfr.inputRegisters.isEmpty()) {
                    inputRegisters[dir] = pfr.inputRegisters.get(0);
                }
            }
        }
    }

    @Override
    public void consumeRemaps(RemapProvider remapProvider) {
        for (int dir = 0; dir < 6; dir++) {
            outputRegisters[dir] = remapProvider.getRemappedRegisterID(outputRegisters[dir]);
            inputRegisters[dir] = remapProvider.getRemappedRegisterID(inputRegisters[dir]);
        }
    }

    @Override
    public void collect(Collector collector) {
        Map<Integer, Integer> remaps = new HashMap<>();

        for (Map.Entry<Integer, Integer> e : getInputSignalMap().entrySet()) {
            int inDir = e.getKey();
            int outsideInputReg = inputRegisters[inDir]; // Connect this tile's input register...
            int insideInputReg = e.getValue();   // to this register from inside the nested map
            remaps.put(insideInputReg, outsideInputReg);
        }

        for (Map.Entry<Integer, Integer> e : getOutputSignalMap().entrySet()) {
            int outDir = e.getKey();
            int outsideOutputReg = outputRegisters[outDir];
            int insideOutputReg = e.getValue();
            remaps.put(insideOutputReg, outsideOutputReg);
        }

        addMapToAssembler(collector, remaps);
    }
}
