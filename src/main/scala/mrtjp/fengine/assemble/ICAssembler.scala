package mrtjp.fengine.assemble

import mrtjp.fengine.simulate.{ICGate, ICRegister}
import mrtjp.fengine.tiles.FETileMap

trait ICAssembler
{
    def allocRegisterID():Int
    def allocRegisterID(id:Int):Int

    def allocGateID():Int
    def allocGateID(id:Int):Int

    def getRemappedRegisterID(id:Int):Int

    def addRemap(oldID:Int, newID:Int):Unit

    def addRegister(id:Int, r:ICRegister):Unit
    def addGate(id:Int, g:ICGate, drivingRegs:collection.Seq[Int], drivenRegs:collection.Seq[Int]):Unit

    def addTileMap(map:FETileMap, remaps:collection.Map[Int, Int]):Unit
    def addFlatMap(flatMap:ICFlatMap, remaps:collection.Map[Int, Int]):Unit

    def getMapIndex:Int

    def result():ICFlatMap
}

object ICAssembler
{
    def newAssembler:ICAssembler = new ICAssemblerImpl
}