package mrtjp.fengine.testimpl

import mrtjp.fengine.TileCoord._
import mrtjp.fengine.assemble.{ICAssembler, ICAssemblerPathFinder}
import mrtjp.fengine.simulate.StaticValueRegister
import mrtjp.fengine.tiles.FETile

class PortlessIOTileTestImpl(label:String, isGlobalInput:Boolean, ioDir:Int, ioRegID:Int) extends FETile with TTestFETile
{
    var regID:Int = -1
    val register:StaticValueRegister[Byte] = new StaticValueRegister[Byte](0)

    override def getOutputRegister(outDir:Int, outPort:Int):Option[Int] =
        if (regID > -1 && isGlobalInput && outDir == ioDir) Some(regID) else None

    override def getInputRegister(inDir:Int, inPort:Int):Option[Int] =
        if (regID > -1 && !isGlobalInput && inDir == ioDir) Some(regID) else None

    override def allocate(assembler:ICAssembler):Unit = {
//        if (!isGlobalInput)
//            regID = -1
//        else
            regID = assembler.allocRegisterID(ioRegID)
    }

    override def locate(assembler:ICAssembler, pathFinder:ICAssemblerPathFinder):Unit = {
        if (!isGlobalInput) { //Pathfinding only necessary for output IO tile, since they have an incomming register
            val pfr = pathFinder.doPathFinding { (d:Int, p:Int) => d == ioDir }
            if (pfr.inputRegisters.size > 1) {
                println(s"ERR: Unexpected multiple drivers: ${pfr.inputRegisters}")
            }
            if (pfr.inputRegisters.nonEmpty) {
                regID = pfr.inputRegisters.head
            }
            if (regID > -1)
                assembler.addRemap(regID, ioRegID) //Remap the located register
        }
    }

    override def remap(assembler:ICAssembler):Unit = {
        regID = assembler.getRemappedRegisterID(regID)
    }

    override def collect(assembler:ICAssembler):Unit = {
        if (isGlobalInput) {
            if (regID > -1 && assembler.getMapIndex == 0) //if root map
                assembler.addRegister(regID, register)
        }
    }

    override def getStringRenderRows:Seq[String] = {
        val cN = if (ioDir != dirNorth) "───" else "   "
        val cE = if (ioDir != dirEast) "│" else " "
        val cS = if (ioDir != dirSouth) "───" else "   "
        val cW = if (ioDir != dirWest) "│" else " "
        val rid = if (regID > -1) s"$regID" else " "

        val dN = if (!isGlobalInput && ioDir == dirNorth) '↓'  else if (isGlobalInput && ioDir == dirNorth) '↑' else ' '
        val dE = if (!isGlobalInput && ioDir == dirEast) '←'  else if (isGlobalInput && ioDir == dirEast) '→' else ' '
        val dS = if (!isGlobalInput && ioDir == dirSouth) '↑'  else if (isGlobalInput && ioDir == dirSouth) '↓' else ' '
        val dW = if (!isGlobalInput && ioDir == dirWest) '→'  else if (isGlobalInput && ioDir == dirWest) '←' else ' '

        Seq(
            s" ┌─$cN─┐ ",
            s" │$rid $dN  │ ",
            s" $cW$dW $label $dE$cE ",
            s" │  $dS  │ ",
            s" └─$cS─┘ "
        )

    }
}
