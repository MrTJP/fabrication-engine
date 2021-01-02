package mrtjp.fengine.assemble

import grizzled.slf4j.Logging
import mrtjp.fengine.simulate.{ICGate, ICRegister}
import mrtjp.fengine.tiles.FETileMap

import scala.collection.mutable.ArrayBuffer
import scala.collection.{Map, mutable}

class ICAssemblerImpl extends ICAssembler with ICFlatMap with Logging
{
    override val registers:mutable.Map[Int, ICRegister] = mutable.HashMap[Int, ICRegister]() //The registers currently in the circuit, indexed by ID
    override val gates:mutable.Map[Int, ICGate] = mutable.HashMap[Int, ICGate]() //The gates in the circuit indexed by ID
    override val regDependents:mutable.Map[Int, ArrayBuffer[Int]] = mutable.HashMap[Int, ArrayBuffer[Int]]()    //[regID -> Seq[gateID]]
    override val regDependencies:mutable.Map[Int, ArrayBuffer[Int]] = mutable.HashMap[Int, ArrayBuffer[Int]]()  //[regID -> Seq[gateID]]
    override val gateDependents:mutable.Map[Int, ArrayBuffer[Int]] = mutable.HashMap[Int, ArrayBuffer[Int]]()   //[gateID -> Seq[regID]]
    override val gateDependencies:mutable.Map[Int, ArrayBuffer[Int]] = mutable.HashMap[Int, ArrayBuffer[Int]]() //[gateID -> Seq[regID]]

    override val exploredTileMaps:mutable.ArrayBuffer[FETileMap] = ArrayBuffer[FETileMap]()
    override val exploredFlatMaps:mutable.ArrayBuffer[ICFlatMap] = ArrayBuffer[ICFlatMap]()

    private val openTileMaps = mutable.Queue[(FETileMap, Map[Int, Int])]()
    private val openFlatMaps = mutable.Queue[(ICFlatMap, Map[Int, Int])]()

    private var nextRegID = 0
    private var nextGateID = 0

    private var registerIDRemaps = mutable.Map[Int, Int]()

    override def allocRegisterID():Int = allocRegisterID(nextRegID)
    override def allocGateID():Int = allocGateID(nextGateID)

    override def allocRegisterID(id:Int):Int = {
        if (id >= nextRegID)
            nextRegID = id+1
        id
    }

    override def allocGateID(id:Int):Int = {
        if (id >= nextGateID)
            nextGateID = id+1
        id
    }

    override def getMapIndex:Int = exploredFlatMaps.length + exploredTileMaps.length

    override def getRemappedRegisterID(id:Int):Int = registerIDRemaps.getOrElse(id, id)

    override def addRemap(oldID:Int, newID:Int):Unit = {
        registerIDRemaps += (oldID -> getRemappedRegisterID(newID))
    }

    override def addRegister(id:Int, r:ICRegister):Unit = {
        allocRegisterID(id)
        registers += (id -> r)
    }

    override def addGate(id:Int, g:ICGate, drivingRegs:collection.Seq[Int], drivenRegs:collection.Seq[Int]):Unit = {
        allocGateID(id)
        gates += (id -> g)

        for (regID <- drivingRegs) {
            regDependents.getOrElseUpdate(regID, new ArrayBuffer[Int]()) += id
        }

        for (regID <- drivenRegs) {
            regDependencies.getOrElseUpdate(regID, new ArrayBuffer[Int]()) += id
        }

        gateDependents.getOrElseUpdate(id, new ArrayBuffer[Int]()) ++= drivenRegs
        gateDependencies.getOrElseUpdate(id, new ArrayBuffer[Int]()) ++= drivingRegs
    }

    override def addTileMap(tileMap:FETileMap, remaps:Map[Int, Int]):Unit = {
        openTileMaps.enqueue((tileMap, remaps))
    }

    override def addFlatMap(flatMap:ICFlatMap, remaps:Map[Int, Int]):Unit = {
        openFlatMaps.enqueue((flatMap, remaps))
    }

    private def mergeTileMap(map:FETileMap, remap:Map[Int, Int]):Unit = {
        registerIDRemaps.clear()
        registerIDRemaps ++= remap

        //Phase 1: Allocate registers and compute nodes
        logger.info("Assembly Phase 1: Allocations")
        for ((pos, tile) <- map.tileMap) {
            tile.allocate(this)
        }

        //Phase 2: Pathfinding and remap assignment
        logger.info("Assembly Phase 2: Pathfinding and remap declarations")
        for ((pos, tile) <- map.tileMap) {
            val p = pos
            val m = map
            val pathFinder = new ICAssemblerPathFinder {
                override def doPathFinding(propagationFunc:(Int, Int) => Boolean):PathFinderResult = {
                    logger.debug(s"Pathfinder running at $p")
                    val r = new TileMapPathFinder(m, p, propagationFunc)
                    var i = 0
                    while (!r.isFinished) { r.step(); i += 1 }
                    logger.debug(s"Pathfinder finished in $i steps")
                    val results = r.result()
                    results
                }
            }

            tile.locate(this, pathFinder)
        }

        //Phase 3: Remapping
        logger.info("Assembly Phase 3: Remapping")
        for ((pos, tile) <- map.tileMap) {
            tile.remap(this)
        }

        //Phase 4: Collect
        logger.info("Assembly Phase 4: Register and Gate collection")
        for ((pos, tile) <- map.tileMap) {
            tile.collect(this)
        }

        registerIDRemaps.clear()
    }

    private def mergeFlatMap(map:ICFlatMap, remaps:Map[Int, Int]):Unit = {
        val gateTransforms = mutable.HashMap[Int, Int]()
        val regTransforms = mutable.HashMap[Int, Int]()

        //Allocate new IDs for all registers in this address space besides those with explicit remaps
        for ((oldId, r) <- map.registers) {
            val newId = remaps.getOrElse(oldId, allocRegisterID()) //Use remap if available, else alloc new
            regTransforms += oldId -> newId
        }

        //Allocate new IDs for all gates in this address space
        for ((oldId, g) <- map.gates)
            gateTransforms += oldId -> allocGateID()

        //Copy in registers to remapped addresses
        for ((oldId, r) <- map.registers) {
            val newId = regTransforms(oldId)
            if (!registers.contains(newId)) //Skip registers that are mapped to pre-existing ones (i.e. IO)
                addRegister(newId, r)
        }

        //Copy in gates and remap their dep lists
        for ((oldId, g) <- map.gates) {
            val newId = gateTransforms(oldId)
            val newDriving = map.gateDependencies(oldId).map(regTransforms(_))
            val newDriven = map.gateDependents(oldId).map(regTransforms(_))
            addGate(newId, g, newDriving, newDriven)
        }
    }

    override def result():ICFlatMap = {
        while (openTileMaps.nonEmpty) {
            val (map, remaps) = openTileMaps.dequeue()
            mergeTileMap(map, remaps)
            exploredTileMaps += map
        }

        while (openFlatMaps.nonEmpty) {
            val (map, remaps) = openFlatMaps.dequeue()
            mergeFlatMap(map, remaps)
            exploredFlatMaps += map
        }

        this
    }
}