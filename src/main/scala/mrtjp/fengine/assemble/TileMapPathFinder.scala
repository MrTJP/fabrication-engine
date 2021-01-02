package mrtjp.fengine.assemble

import mrtjp.fengine.TileCoord
import mrtjp.fengine.tiles.{FETile, FETileMap}

import scala.collection.mutable

class TileMapPathFinder(map:FETileMap, coord:TileCoord, propFunc:(Int, Int) => Boolean)
{
    private val open = mutable.Queue[PathFinderNode]()
    private val openSet = mutable.HashSet[PathFinderNode]()
    private val closedSet = mutable.HashSet[PathFinderNode]()

    val portToOutputRegisters:mutable.HashMap[Int, mutable.HashSet[Int]] = mutable.HashMap[Int, mutable.HashSet[Int]]()
    val portToInputRegisters:mutable.HashMap[Int, mutable.HashSet[Int]] = mutable.HashMap[Int, mutable.HashSet[Int]]()

    openInitial(coord, propFunc)

    private def openInitial(coord:TileCoord, propFunc:(Int, Int) => Boolean):Unit =
    {
        openNext(coord, propFunc, PathFinderNode.apply)
    }
    private def openFrom(prev:PathFinderNode, coord:TileCoord, propFunc:(Int, Int) => Boolean):Unit =
    {
        openNext(coord, propFunc, prev.moveTo)
    }
    private def openNext(coord:TileCoord, propFunc:(Int, Int) => Boolean, nodeFactory:(TileCoord, Int, Int) => PathFinderNode):Unit =
    {
        for (s <- 0 until 6) for (p <- 0 until 16) if (propFunc(s, p)) {
            val move = nodeFactory(coord, s, p)
            if (!openSet.contains(move) && !closedSet.contains(move)) {
                open += move
                openSet += move
            }
        }
    }

    private def collect(root:PathFinderNode, tile:FETile, coord:TileCoord, dir:Int, port:Int):Unit =
    {
        val initialPort = root.port // Initial port at start of pathfinding which lead to discovery of this new tile

        tile.getOutputRegister(dir, port) match {
            case Some(outputRegID) => // Pathfinding lead to tile that has output register
                val inputRegisters = portToInputRegisters.getOrElseUpdate(initialPort, new mutable.HashSet[Int]())
                inputRegisters += outputRegID // Another tile's output is root tile's input
            case None =>
        }
        tile.getInputRegister(dir, port) match {
            case Some(inputRegID) =>
                val outputRegisters = portToOutputRegisters.getOrElseUpdate(initialPort, new mutable.HashSet[Int]())
                outputRegisters += inputRegID // Another tile's input is root tile's output
            case None =>
        }
    }

    private def iterate():Unit =
    {
        if (open.isEmpty) return

        val next = open.dequeue()
        openSet.remove(next)

        val newPos = next.pos.offset(next.dir)
        map.getTile(newPos) match {
            case Some(tile) =>
                val fromDir = TileCoord.oppositeDir(next.dir)
                val pfunc = tile.propagationFunc(fromDir, next.port)
                collect(next.rootNode, tile, newPos, fromDir, next.port)
                openFrom(next.rootNode, newPos, pfunc)

                closedSet += next
            case None =>
        }
    }

    def step():Unit =
    {
        iterate()
    }

    def isFinished:Boolean = open.isEmpty

    def result():PathFinderResult = new PathFinderResult(
        portToOutputRegisters.map(kv => (kv._1, kv._2.toSet)).toMap,
        portToInputRegisters.map(kv => (kv._1, kv._2.toSet)).toMap)
}
