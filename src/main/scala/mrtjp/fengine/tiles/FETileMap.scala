package mrtjp.fengine.tiles

import mrtjp.fengine.TileCoord

import scala.collection.mutable

trait FETileMap
{
    val tileMap:mutable.Map[TileCoord, FETile] = mutable.HashMap[TileCoord, FETile]()

    def addTile(coord:TileCoord, t:FETile):Boolean =
        if (!tileMap.contains(coord)) {
            tileMap += (coord -> t)
            true
        } else false

    def getTile(coord:TileCoord):Option[FETile] = tileMap.get(coord)

    def removeTile(coord:TileCoord):Option[FETile] = tileMap.remove(coord)

    def getTileCount:Int = tileMap.size
}