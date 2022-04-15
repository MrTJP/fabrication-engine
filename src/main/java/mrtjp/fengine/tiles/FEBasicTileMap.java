package mrtjp.fengine.tiles;

import mrtjp.fengine.TileCoord;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FEBasicTileMap implements FETileMap {

    protected Map<TileCoord, FETile> tileMap = new HashMap<>();

    /**
     * Adds the given tile to this map at the given coordinate. The coordinate
     * must be currently empty, or the tile will not be added.
     *
     * @param coord The coordinate to map the tile to
     * @param tile  The tile to add
     * @return True if this tile was added
     */
    public boolean addTile(TileCoord coord, FETile tile) {
        if (!tileMap.containsKey(coord)) {
            tileMap.put(coord, tile);
            return true;
        }
        return false;
    }

    /**
     * Removes a tile at the given coordinate, if one exists.
     *
     * @param coord The coordinate to remove the tile from
     * @return An optional containing the removed tile
     */
    public Optional<FETile> removeTile(TileCoord coord) {
        return Optional.ofNullable(tileMap.remove(coord));
    }

    /**
     * Returns the number of tiles currently in this map.
     */
    public int getTileCount() {
        return tileMap.size();
    }

    @Override
    public Optional<FETile> getTile(TileCoord coord) {
        return Optional.ofNullable(tileMap.get(coord));
    }

    @Override
    public Collection<TileMapEntry> getEntries() {

        return tileMap.entrySet().stream().map(e -> new TileMapEntry() {
            //@formatter:off
            @Override public TileCoord getCoord() { return e.getKey(); }
            @Override public FETile getTile() { return e.getValue(); }
            //@formatter:on
        }).collect(Collectors.toList());
    }
}
