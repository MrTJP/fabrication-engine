package mrtjp.fengine.tiles

import mrtjp.fengine.assemble.ICAssembler

/**
  * Directionally connected tile that passes signals to nested FETileMap. The held tilemap is
  * presented to the ICAssembler along with a map that is used to connect this tile's signals in
  * this tile's map to registers in the nested map.
  */
trait FEPortlessNestedTileMapTile extends FETile with FEPortlessNestedMapTile
{
    /** The nested map */
    val nestedTileMap:FETileMap

    override def addMapToAssembler(assembler:ICAssembler, remaps:collection.Map[Int, Int]):Unit = {
        assembler.addTileMap(nestedTileMap, remaps)
    }
}
