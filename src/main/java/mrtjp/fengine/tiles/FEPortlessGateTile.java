package mrtjp.fengine.tiles;

import mrtjp.fengine.api.ICAssembler;
import mrtjp.fengine.assemble.PathFinder;
import mrtjp.fengine.assemble.PathFinderResult;
import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class FEPortlessGateTile implements FETile {

    private final int[] inputRegisters = new int[6];
    private final int[] outputRegisters = new int[6];
    private int gateId = -1;

    public FEPortlessGateTile() {
        Arrays.fill(inputRegisters, -1);
        Arrays.fill(outputRegisters, -1);
    }

    /**
     * Mask of outputs. Registers are allocated and created
     */
    protected abstract int getOutDirMask();

    /**
     * Mask of inputs. Registers are obtained via pathfinding
     */
    protected abstract int getInDirMask();

    /**
     * Creates an output register to be exposed towards `dir`
     *
     * @param dir Direction towards which the returned register should connect to
     * @return Register for the given dir
     */
    protected abstract ICRegister createRegister(int dir);

    /**
     * Create a new gate that will operate over the given inputs and outputs
     *
     * @param inputs  List of input IDs for registers that can be read
     * @param outputs List of output IDs for registers that can be written
     * @return The new gate object
     */
    protected abstract ICGate createGate(ArrayList<Integer> inputs, ArrayList<Integer> outputs);

    @Override
    public Optional<Integer> getInputRegister(int inDir, int inPort) {
        return (getInDirMask() & 1 << inDir) != 0 && inputRegisters[inDir] > -1 ?
                Optional.of(inputRegisters[inDir]) :
                Optional.empty();
    }

    @Override
    public Optional<Integer> getOutputRegister(int outDir, int outPort) {
        return (getOutDirMask() & 1 << outDir) != 0 && outputRegisters[outDir] > -1 ?
                Optional.of(outputRegisters[outDir]) :
                Optional.empty();
    }

    @Override
    public void allocate(ICAssembler assembler) {
        Arrays.fill(inputRegisters, -1);
        Arrays.fill(outputRegisters, -1);
        gateId = -1;

        for (int dir = 0; dir < 6; dir++) {
            if ((getOutDirMask() & 1 << dir) != 0) outputRegisters[dir] = assembler.allocRegisterID();
        }
        gateId = assembler.allocGateID();
    }

    @Override
    public void locate(ICAssembler assembler, PathFinder pathFinder) {
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
    public void remap(ICAssembler assembler) {
        for (int dir = 0; dir < 6; dir++) {
            outputRegisters[dir] = assembler.getRemappedRegisterID(outputRegisters[dir]);
            inputRegisters[dir] = assembler.getRemappedRegisterID(inputRegisters[dir]);
        }
    }

    @Override
    public void collect(ICAssembler assembler) {
        ArrayList<Integer> inputs = Arrays.stream(inputRegisters).filter(c -> c != -1).boxed().collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer> outputs = Arrays.stream(outputRegisters).filter(c -> c != -1).boxed().collect(Collectors.toCollection(ArrayList::new));

        for (int dir = 0; dir < 6; dir++) {
            if (outputRegisters[dir] != -1) assembler.addRegister(outputRegisters[dir], createRegister(dir));
        }
        ICGate op = createGate(inputs, outputs);
        assembler.addGate(gateId, op, inputs, outputs);
    }
}