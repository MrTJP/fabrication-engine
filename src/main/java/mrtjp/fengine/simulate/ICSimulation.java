package mrtjp.fengine.simulate;

import mrtjp.fengine.api.ICFlatMap;

import java.util.*;

public class ICSimulation {

    private final ICRegister[] registers;
    private final ICGate[] gates;
    private final int[][] regDependents;
    private final int[][] gateInputs;
    private final int[][] gateOutputs;

    private Queue<Integer> changeQueue = new LinkedList<>();

    private static final int[] EMPTY_INT_ARRAY = new int[0];

    public ICSimulation(
            Map<Integer, ICRegister> registerMap,
            Map<Integer, ICGate> gateMap,
            Map<Integer, ArrayList<Integer>> registerDependentsMap,
            Map<Integer, ArrayList<Integer>> gateInputsMap,
            Map<Integer, ArrayList<Integer>> gateOutputsMap
    ) {
        int regCount = registerMap.isEmpty() ? 0 : maxKey(registerMap) + 1;
        int gateCount = gateMap.isEmpty() ? 0 : maxKey(gateMap) + 1;

        registers = expandIntoArray(registerMap, new ICRegister[regCount]);
        gates = expandIntoArray(gateMap, new ICGate[gateCount]);
        regDependents = expandIntoDoubleIntArray(registerDependentsMap, regCount);
        gateInputs = expandIntoDoubleIntArray(gateInputsMap, gateCount);
        gateOutputs = expandIntoDoubleIntArray(gateOutputsMap, gateCount);
    }

    public ICSimulation(ICFlatMap map) {
        this(map.getRegisters(), map.getGates(), map.getRegDependents(), map.getGateDependencies(), map.getGateDependents());
    }

    private static int maxKey(Map<Integer, ?> map) {
        return map.keySet().stream()
                .mapToInt(c -> c)
                .max()
                .orElse(0);
    }

    private static <T> T[] expandIntoArray(Map<Integer, T> map, T[] array) {
        for (Map.Entry<Integer, T> e : map.entrySet()) {
            array[e.getKey()] = e.getValue();
        }
        return array;
    }

    private static int[][] expandIntoDoubleIntArray(Map<Integer, ArrayList<Integer>> map, int length) {
        int[][] array = new int[length][];
        Arrays.fill(array, EMPTY_INT_ARRAY);
        for (Map.Entry<Integer, ArrayList<Integer>> e : map.entrySet()) {
            array[e.getKey()] = e.getValue().isEmpty() ? EMPTY_INT_ARRAY : e.getValue().stream().mapToInt(c -> c).toArray();
        }
        return array;
    }

    private void runCompute(int gateID) {
        gates[gateID].compute(this, gateInputs[gateID], gateOutputs[gateID]);
    }

    public boolean computeAll(ICSimulationCallback callback) {

        for (int id = 0; id < gates.length; id++) {
            runCompute(id);
        }

        propagate(callback);
        return changeQueue.isEmpty();
    }

    public byte getRegByteVal(int regID) {
        return registers[regID].getByteVal();
    }

    public void queueRegByteVal(int regID, byte newVal) {
        if (registers[regID].queueByteVal(newVal)) { changeQueue.add(regID); }
    }

    public boolean propagate(ICSimulationCallback callback) {

        int[] allComputes = new int[gates.length];
        Arrays.fill(allComputes, 0);
        Set<Integer> computes = new HashSet<>();

        boolean hasOverflow = false;
        int overflowGateId = -1;

        Set<Integer> allChanges = new HashSet<>();
        Queue<Integer> changes = new LinkedList<>();

        while (!changeQueue.isEmpty() && !hasOverflow) {

            // Record new changes
            allChanges.addAll(changeQueue);

            // Copy changes to another list and reset changeQueue
            changes = changeQueue;
            changeQueue = new LinkedList<>();

            // Commit changes for all registers in previous change queue
            for (int regID : changes) { registers[regID].pushVal(this); }

            // Re-run computes for all gates that care about the changed registers
            computes.clear();
            for (int regID : changes) {
                for (int gateID : regDependents[regID]) {
                    if (!computes.contains(gateID)) {
                        runCompute(gateID);
                        computes.add(gateID);
                    }
                }
            }

            // Increment counter for all gates that were re-computed and limit max computes
            for (int i : computes) {
                if (++allComputes[i] > 32) {
                    hasOverflow = true;
                    overflowGateId = i;
                }
            }
        }

        if (hasOverflow && callback != null) callback.icEventComputeOverflow(new HashSet<>(changes), Collections.singleton(overflowGateId), 32);

        if (!allChanges.isEmpty() && callback != null) callback.registersDidChange(allChanges);

        return !allChanges.isEmpty();
    }
}
