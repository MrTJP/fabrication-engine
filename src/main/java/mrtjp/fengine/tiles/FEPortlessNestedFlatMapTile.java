package mrtjp.fengine.tiles;

import mrtjp.fengine.api.ICFlatMap;

import java.util.Map;

public abstract class FEPortlessNestedFlatMapTile extends FEPortlessNestedMapTile {

    private final int inDirMask;
    private final int outDirMask;
    private final Map<Integer, Integer> inputSignalMap;
    private final Map<Integer, Integer> outputSignalMap;
    private final ICFlatMap nestedFlatMap;

    public FEPortlessNestedFlatMapTile(int inDirMask, int outDirMask, Map<Integer, Integer> inputSignalMap, Map<Integer, Integer> outputSignalMap, ICFlatMap nestedFlatMap) {
        this.inDirMask = inDirMask;
        this.outDirMask = outDirMask;
        this.inputSignalMap = inputSignalMap;
        this.outputSignalMap = outputSignalMap;
        this.nestedFlatMap = nestedFlatMap;
    }

    @Override
    void addMapToAssembler(Collector collector, Map<Integer, Integer> remaps) {
        collector.addFlatMap(nestedFlatMap, remaps);
    }

    //@formatter:off
    @Override int getInDirMask() { return inDirMask; }
    @Override int getOutDirMask() { return outDirMask; }
    @Override Map<Integer, Integer> getInputSignalMap() { return inputSignalMap; }
    @Override Map<Integer, Integer> getOutputSignalMap() { return outputSignalMap; }
    //@formatter:on
}
