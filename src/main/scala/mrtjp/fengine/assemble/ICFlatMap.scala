package mrtjp.fengine.assemble

import mrtjp.fengine.simulate.{ICGate, ICRegister}
import mrtjp.fengine.tiles.FETileMap

import scala.collection.Map
import scala.collection.Seq
import scala.collection.mutable.ArrayBuffer

trait ICFlatMap
{
    val registers:Map[Int, ICRegister] //The registers currently in the circuit, indexed by ID
    val gates:Map[Int, ICGate] //The gates in the circuit indexed by ID

    val regDependents:Map[Int, ArrayBuffer[Int]]    //[regID -> Seq[gateID]]
    val regDependencies:Map[Int, ArrayBuffer[Int]]  //[regID -> Seq[gateID]]
    val gateDependents:Map[Int, ArrayBuffer[Int]]   //[gateID -> Seq[regID]]
    val gateDependencies:Map[Int, ArrayBuffer[Int]] //[gateID -> Seq[regID]]

    val exploredTileMaps:Seq[FETileMap]
    val exploredFlatMaps:Seq[ICFlatMap]
}