package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.api.IPathFinder;
import mrtjp.fengine.api.PropagationFunction;
import mrtjp.fengine.tiles.FETileMap;

import java.util.jar.Manifest;

public class PathFinder implements IPathFinder {

    private final FETileMap map;
    private final TileCoord pos;
    private final PathFinderManifest manifest;

    public PathFinder(FETileMap map, TileCoord pos, PathFinderManifest manifest) {
        this.map = map;
        this.pos = pos;
        this.manifest = manifest;
    }

    public PathFinderResult doPathFinding(PropagationFunction propagationFunc) {
        TileMapPathFinder pf = new TileMapPathFinder(manifest, map, pos, propagationFunc);
        while (!pf.isFinished()) pf.step();
        return pf.result();
    }
}
