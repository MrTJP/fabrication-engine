package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.api.PropagationFunction;
import mrtjp.fengine.tiles.FETileMap;

public class PathFinder {

    private final FETileMap map;
    private final TileCoord pos;

    public PathFinder(FETileMap map, TileCoord pos) {
        this.map = map;
        this.pos = pos;
    }

    public PathFinderResult doPathFinding(PropagationFunction propagationFunc) {
        TileMapPathFinder pf = new TileMapPathFinder(map, pos, propagationFunc);
        while (!pf.isFinished()) pf.step();
        return pf.result();
    }
}
