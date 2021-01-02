package mrtjp.fengine.tiles

import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}

trait FEPortlessNestedFlatMapTile extends FEPortlessNestedMapTile
{
    /** The nested map */
    val nestedFlatMap:ICFlatMap

    override def addMapToAssembler(assembler:ICAssembler, remaps:collection.Map[Int, Int]):Unit = {
        assembler.addFlatMap(nestedFlatMap, remaps)
    }
}
