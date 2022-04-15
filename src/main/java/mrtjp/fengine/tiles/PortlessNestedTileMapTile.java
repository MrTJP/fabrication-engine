package mrtjp.fengine.tiles;

import mrtjp.fengine.api.ICAssembler;

import java.util.Map;

public abstract class PortlessNestedTileMapTile extends FEPortlessNestedMapTile {

    private final int inDirMask;
    private final int outDirMask;
    private final Map<Integer, Integer> inputSignalMap;
    private final Map<Integer, Integer> outputSignalMap;
    private final FETileMap nestedTileMap;

    public PortlessNestedTileMapTile(int inDirMask, int outDirMask, Map<Integer, Integer> inputSignalMap, Map<Integer, Integer> outputSignalMap, FETileMap nestedTileMap) {
        this.inDirMask = inDirMask;
        this.outDirMask = outDirMask;
        this.inputSignalMap = inputSignalMap;
        this.outputSignalMap = outputSignalMap;
        this.nestedTileMap = nestedTileMap;
    }

    @Override
    void addMapToAssembler(ICAssembler assembler, Map<Integer, Integer> remaps) {
        assembler.addTileMap(nestedTileMap, remaps);
    }

    //@formatter:off
    @Override int getInDirMask() { return inDirMask; }
    @Override int getOutDirMask() { return outDirMask; }
    @Override Map<Integer, Integer> getInputSignalMap() { return inputSignalMap; }
    @Override Map<Integer, Integer> getOutputSignalMap() { return outputSignalMap; }
    //@formatter:on
}
