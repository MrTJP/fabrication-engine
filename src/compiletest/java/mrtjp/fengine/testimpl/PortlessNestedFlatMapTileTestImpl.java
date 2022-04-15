package mrtjp.fengine.testimpl;

import mrtjp.fengine.api.ICFlatMap;
import mrtjp.fengine.tiles.FEPortlessNestedFlatMapTile;

import java.util.Map;

public class PortlessNestedFlatMapTileTestImpl extends FEPortlessNestedFlatMapTile {

    private final String label;

    public PortlessNestedFlatMapTileTestImpl(String label, int inDirMask, int outDirMask, Map<Integer, Integer> inputSignalMap, Map<Integer, Integer> outputSignalMap, ICFlatMap nestedFlatMap) {
        super(inDirMask, outDirMask, inputSignalMap, outputSignalMap, nestedFlatMap);
        this.label = label;
    }
}
