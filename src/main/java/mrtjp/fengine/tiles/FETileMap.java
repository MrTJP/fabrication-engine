package mrtjp.fengine.tiles;

import mrtjp.fengine.TileCoord;

import java.util.Collection;
import java.util.Optional;

/**
 * A container of tiles.
 */
public interface FETileMap {

    /**
     * Optionally returns the tile at the given coordinate, if one exists.
     *
     * @param coord The coordinate to query
     * @return An optional containing the tile if present, or Optional.empty() otherwise
     */
    Optional<FETile> getTile(TileCoord coord);

    /**
     * Returns an iterable collection containing all coord/tile pairs added to this map.
     * @return
     */
    Collection<TileMapEntry> getEntries();

    interface TileMapEntry {
        TileCoord getCoord();
        FETile getTile();
    }
}
