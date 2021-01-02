package mrtjp.fengine.testimpl

import mrtjp.fengine.TileCoord._
import mrtjp.fengine.tiles.{FEPortlessNestedTileMapTile, FETile, FETileMap}

class PortlessNestedTileMapTile(label:String, override val inDirMask:Int, override val outDirMask:Int, override val inputSignalMap:Map[Int, Int], override val outputSignalMap:Map[Int, Int], override val nestedTileMap:FETileMap) extends FETile with FEPortlessNestedTileMapTile with TTestFETile
{
    override def getStringRenderRows:Seq[String] = {
        def regStr(dir:Int):String = if (inputRegisters(dir) > -1) s"${inputRegisters(dir)}"
            else if (outputRegisters(dir) > -1) s"${outputRegisters(dir)}" else " "

        val cN = if (((inDirMask|outDirMask)&bitNorth) == 0) "•••" else s" ${regStr(dirNorth)} "
        val cE = if (((inDirMask|outDirMask)&bitEast)  == 0) "•" else s"${regStr(dirEast)}"
        val cS = if (((inDirMask|outDirMask)&bitSouth) == 0) "•••" else s" ${regStr(dirSouth)} "
        val cW = if (((inDirMask|outDirMask)&bitWest)  == 0) "•" else s"${regStr(dirWest)}"

        val dN = if ((inDirMask&outDirMask&bitNorth) != 0) '↕' else if ((inDirMask&bitNorth) != 0) '↓'  else if ((outDirMask&bitNorth) != 0) '↑' else ' '
        val dE = if ((inDirMask&outDirMask&bitEast)  != 0) '↔' else if ((inDirMask&bitEast)  != 0) '←'  else if ((outDirMask&bitEast)  != 0) '→' else ' '
        val dS = if ((inDirMask&outDirMask&bitSouth) != 0) '↕' else if ((inDirMask&bitSouth) != 0) '↑'  else if ((outDirMask&bitSouth) != 0) '↓' else ' '
        val dW = if ((inDirMask&outDirMask&bitWest)  != 0) '↔' else if ((inDirMask&bitWest)  != 0) '→'  else if ((outDirMask&bitWest)  != 0) '←' else ' '

        Seq(
            s"•••$cN•••",
            s"•   $dN   •",
            s"$cW$dW  $label  $dE$cE",
            s"•   $dS   •",
            s"•••$cS•••"
        )
//        Seq(
//            s"┌──$cN──┐",
//            s"│   $dN   │",
//            s"$cW$dW  $label  $dE$cE",
//            s"│   $dS   │",
//            s"└──$cS──┘"
//        )
    }
}