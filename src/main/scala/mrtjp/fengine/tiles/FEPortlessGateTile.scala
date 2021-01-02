package mrtjp.fengine.tiles

import mrtjp.fengine.assemble.{ICAssembler, ICAssemblerPathFinder}
import mrtjp.fengine.simulate.{ICGate, ICRegister}

/** A directionally connected gate that ignores ports
  *
  *   - Recieves from masked sides from any port
  *   - Drives to masked sides on all ports
  *   - Declares a no-op gate in schematic
  */
trait FEPortlessGateTile extends FETile
{
    /** Mask of outputs. Registers are allocated and created */
    val outDirMask:Int

    /** Mask of inputs. Registers are obtained via pathfinding */
    val inDirMask:Int

    protected val inputRegisters:Array[Int] = Array.fill(6)(-1)
    protected val outputRegisters:Array[Int] = Array.fill(6)(-1)
    protected var gateID:Int = -1

    override def getInputRegister(inDir:Int, inPort:Int):Option[Int] =
        if ((inDirMask&1<<inDir) != 0 && inputRegisters(inDir) > -1) Some(inputRegisters(inDir)) else None

    override def getOutputRegister(outDir:Int, outPort:Int):Option[Int] =
        if ((outDirMask&1<<outDir) != 0 && outputRegisters(outDir) > -1) Some(outputRegisters(outDir)) else None

    override def allocate(assembler:ICAssembler):Unit =
    {
        inputRegisters.map(_ => -1)
        outputRegisters.map(_ => -1)
        gateID = -1

        for (dir <- 0 until 6) {
            if ((outDirMask&1<<dir) != 0)
                outputRegisters(dir) = assembler.allocRegisterID()
        }
        gateID = assembler.allocGateID()
    }

    override def locate(assembler:ICAssembler, pathFinder:ICAssemblerPathFinder):Unit = {
        for (dir <- 0 until 6) {
            if ((inDirMask&1<<dir) != 0) {
                val pfr = pathFinder.doPathFinding { (d:Int, p:Int) => d == dir }
                if (pfr.inputRegisters.size > 1) {
                    println(s"ERR: Unexpected multiple drivers: ${pfr.inputRegisters}")
                }
                if (pfr.inputRegisters.nonEmpty) {
                    inputRegisters(dir) = pfr.inputRegisters.head
                }
            }
        }
    }

    override def remap(assembler:ICAssembler):Unit = {
        outputRegisters.mapInPlace(assembler.getRemappedRegisterID)
        inputRegisters.mapInPlace(assembler.getRemappedRegisterID)
    }

    override def collect(assembler:ICAssembler):Unit = {
        val inputs = inputRegisters.filter(_ != -1).toSeq
        val outputs = outputRegisters.filter(_ != -1).toSeq

        for (dir <- 0 until 6) if (outputRegisters(dir) != -1) {
            assembler.addRegister(outputRegisters(dir), createRegister(dir))
        }
        val op = createGate(inputs, outputs)
        assembler.addGate(gateID, op, inputs, outputs)
    }

    def createRegister(dir:Int):ICRegister
    def createGate(inputs:Seq[Int], outputs:Seq[Int]):ICGate
}