package mrtjp.fengine.testimpl

import mrtjp.fengine.TileCoord._
import mrtjp.fengine.simulate.{ICGate, ICRegister, NoOpGate, StaticValueRegister}
import mrtjp.fengine.tiles.{FEPortlessGateTile, FETile}

class PortlessGateTileImpl(label:String, override val inDirMask:Int, override val outDirMask:Int) extends FETile with FEPortlessGateTile with TTestFETile
{
    val gate = new NoOpGate
    val registers:Array[StaticValueRegister[Byte]] = Array.fill(6)(new StaticValueRegister[Byte](0))

    override def getStringRenderRows:Seq[String] = {
        def regStr(dir:Int):String = if (inputRegisters(dir) > -1) s"${inputRegisters(dir)}"
                                        else if (outputRegisters(dir) > -1) s"${outputRegisters(dir)}" else " "

        val cN = if (((inDirMask|outDirMask)&bitNorth) == 0) "───" else s" ${regStr(dirNorth)} "
        val cE = if (((inDirMask|outDirMask)&bitEast)  == 0) "│" else s"${regStr(dirEast)}"
        val cS = if (((inDirMask|outDirMask)&bitSouth) == 0) "───" else s" ${regStr(dirSouth)} "
        val cW = if (((inDirMask|outDirMask)&bitWest)  == 0) "│" else s"${regStr(dirWest)}"

        val dN = if ((inDirMask&outDirMask&bitNorth) != 0) '↕' else if ((inDirMask&bitNorth) != 0) '↓'  else if ((outDirMask&bitNorth) != 0) '↑' else ' '
        val dE = if ((inDirMask&outDirMask&bitEast)  != 0) '↔' else if ((inDirMask&bitEast)  != 0) '←'  else if ((outDirMask&bitEast)  != 0) '→' else ' '
        val dS = if ((inDirMask&outDirMask&bitSouth) != 0) '↕' else if ((inDirMask&bitSouth) != 0) '↑'  else if ((outDirMask&bitSouth) != 0) '↓' else ' '
        val dW = if ((inDirMask&outDirMask&bitWest)  != 0) '↔' else if ((inDirMask&bitWest)  != 0) '→'  else if ((outDirMask&bitWest)  != 0) '←' else ' '

        Seq(
            s"┌──$cN──┐",
            s"│   $dN   │",
            s"$cW$dW  $label  $dE$cE",
            s"│   $dS   │",
            s"└──$cS──┘"
        )
    }

    override def createRegister(dir:Int):ICRegister = registers(dir)
    override def createGate(inputs:Seq[Int], outputs:Seq[Int]):ICGate = gate
}
