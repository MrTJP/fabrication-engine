package mrtjp.fengine.testimpl;

import mrtjp.fengine.tiles.FETileMap;
import mrtjp.fengine.tiles.PortlessNestedTileMapTile;

import java.util.Map;

public class PortlessNestedTileMapTileTestImpl extends PortlessNestedTileMapTile {

    private final String label;

    public PortlessNestedTileMapTileTestImpl(String label, int inDirMask, int outDirMask, Map<Integer, Integer> inputSignalMap, Map<Integer, Integer> outputSignalMap, FETileMap nestedTileMap) {
        super(inDirMask, outDirMask, inputSignalMap, outputSignalMap, nestedTileMap);
        this.label = label;
    }
}
