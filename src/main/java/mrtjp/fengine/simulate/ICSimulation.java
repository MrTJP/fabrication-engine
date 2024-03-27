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

    //region Register read
    public byte getRegByteVal(int r) {
        return registers[r].getByteVal();
    }

    public short getRegShortVal(int r1, int r0) {
        short i = 0;
        i |= (short) ((getRegByteVal(r1) & 0xFF) << 8);
        i |= (short) (getRegByteVal(r0) & 0xFF);
        return i;
    }

    public int getRegIntVal(int r3, int r2, int r1, int r0) {
        int i = 0;
        i |= (getRegByteVal(r3) & 0xFF) << 24;
        i |= (getRegByteVal(r2) & 0xFF) << 16;
        i |= (getRegByteVal(r1) & 0xFF) << 8;
        i |= getRegByteVal(r0) & 0xFF;
        return i;
    }

    public long getRegLongVal(int r7, int r6, int r5, int r4, int r3, int r2, int r1, int r0) {
        long i = 0;
        i |= (long) (getRegByteVal(r7) & 0xFF) << 56;
        i |= (long) (getRegByteVal(r6) & 0xFF) << 48;
        i |= (long) (getRegByteVal(r5) & 0xFF) << 40;
        i |= (long) (getRegByteVal(r4) & 0xFF) << 32;
        i |= (long) (getRegByteVal(r3) & 0xFF) << 24;
        i |= (long) (getRegByteVal(r2) & 0xFF) << 16;
        i |= (long) (getRegByteVal(r1) & 0xFF) << 8;
        i |= (long) (getRegByteVal(r0) & 0xFF);
        return i;
    }

    public short getRegShortVal(int[] regIDs, int offset) {
        short i = 0;
        i |= (short) ((getRegByteVal(regIDs[offset]) & 0xFF) << 8);
        i |= (short) (getRegByteVal(regIDs[offset + 1]) & 0xFF);
        return i;
    }

    public int getRegIntVal(int[] regIDs, int offset) {
        int i = 0;
        i |= (getRegByteVal(regIDs[offset    ]) & 0xFF) << 24;
        i |= (getRegByteVal(regIDs[offset + 1]) & 0xFF) << 16;
        i |= (getRegByteVal(regIDs[offset + 2]) & 0xFF) << 8;
        i |= (getRegByteVal(regIDs[offset + 3]) & 0xFF);
        return i;
    }

    public long getRegLongVal(int[] regIDs, int offset) {
        long i = 0;
        i |= (long) (getRegByteVal(regIDs[offset    ]) & 0xFF) << 56;
        i |= (long) (getRegByteVal(regIDs[offset + 1]) & 0xFF) << 48;
        i |= (long) (getRegByteVal(regIDs[offset + 2]) & 0xFF) << 40;
        i |= (long) (getRegByteVal(regIDs[offset + 3]) & 0xFF) << 32;
        i |= (long) (getRegByteVal(regIDs[offset + 4]) & 0xFF) << 24;
        i |= (long) (getRegByteVal(regIDs[offset + 5]) & 0xFF) << 16;
        i |= (long) (getRegByteVal(regIDs[offset + 6]) & 0xFF) << 8;
        i |= (long) (getRegByteVal(regIDs[offset + 7]) & 0xFF);
        return i;
    }
    //endregion

    //region Register write
    public void queueRegByteVal(int regID, byte newVal) {
        if (registers[regID].queueByteVal(newVal)) { changeQueue.add(regID); }
    }

    public void queueRegShortVal(int r1, int r0, short newVal) {
        queueRegByteVal(r1, (byte) (newVal >> 8));
        queueRegByteVal(r0, (byte) newVal);
    }

    public void queueRegIntVal(int r3, int r2, int r1, int r0, int newVal) {
        queueRegByteVal(r3, (byte) (newVal >> 24));
        queueRegByteVal(r2, (byte) (newVal >> 16));
        queueRegByteVal(r1, (byte) (newVal >> 8));
        queueRegByteVal(r0, (byte) newVal);
    }

    public void queueRegLongVal(int r7, int r6, int r5, int r4, int r3, int r2, int r1, int r0, long newVal) {
        queueRegByteVal(r7, (byte) (newVal >> 56));
        queueRegByteVal(r6, (byte) (newVal >> 48));
        queueRegByteVal(r5, (byte) (newVal >> 40));
        queueRegByteVal(r4, (byte) (newVal >> 32));
        queueRegByteVal(r3, (byte) (newVal >> 24));
        queueRegByteVal(r2, (byte) (newVal >> 16));
        queueRegByteVal(r1, (byte) (newVal >> 8));
        queueRegByteVal(r0, (byte) newVal);
    }

    public void queueRegShortVal(int[] regIDs, int offset, short newVal) {
        queueRegByteVal(regIDs[offset    ], (byte) (newVal >> 8));
        queueRegByteVal(regIDs[offset + 1], (byte) newVal);
    }

    public void queueRegIntVal(int[] regIDs, int offset, int newVal) {
        queueRegByteVal(regIDs[offset    ], (byte) (newVal >> 24));
        queueRegByteVal(regIDs[offset + 1], (byte) (newVal >> 16));
        queueRegByteVal(regIDs[offset + 2], (byte) (newVal >> 8));
        queueRegByteVal(regIDs[offset + 3], (byte) newVal);
    }

    public void queueRegLongVal(int[] regIDs, int offset, long newVal) {
        queueRegByteVal(regIDs[offset    ], (byte) (newVal >> 56));
        queueRegByteVal(regIDs[offset + 1], (byte) (newVal >> 48));
        queueRegByteVal(regIDs[offset + 2], (byte) (newVal >> 40));
        queueRegByteVal(regIDs[offset + 3], (byte) (newVal >> 32));
        queueRegByteVal(regIDs[offset + 4], (byte) (newVal >> 24));
        queueRegByteVal(regIDs[offset + 5], (byte) (newVal >> 16));
        queueRegByteVal(regIDs[offset + 6], (byte) (newVal >> 8));
        queueRegByteVal(regIDs[offset + 7], (byte) newVal);
    }
    //endregion

    //region Simulation control
    public boolean computeAll(ICSimulationCallback callback) {

        for (int id = 0; id < gates.length; id++) {
            runCompute(id);
        }

        propagate(callback);
        return changeQueue.isEmpty();
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
    //endregion
}
