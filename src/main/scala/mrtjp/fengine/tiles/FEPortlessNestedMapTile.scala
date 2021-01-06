package mrtjp.fengine.tiles

import mrtjp.fengine.assemble.{ICAssembler, ICAssemblerPathFinder}

import scala.collection.mutable

trait FEPortlessNestedMapTile extends FETile
{
    /** Mask of outputs. Registers are allocated and created */
    val outDirMask:Int
    /** Mask of inputs. Registers are obtained via pathfinding */
    val inDirMask:Int
    /**
      * Maps [inDir -> nestedRegID] where:
      *   - nestedRegID is a register ID from inside the nested map
      *   - inDir is a signal's input direction to this tile
      */
    val inputSignalMap:Map[Int, Int]
    /**
      * Maps [outDir -> nestedRegID] where:
      *   - nestedRegID is a register ID from inside the nested map
      *   - outDir is a signal's output direction from this tile
      */
    val outputSignalMap:Map[Int, Int]

    def addMapToAssembler(assembler:ICAssembler, remaps:collection.Map[Int, Int]):Unit

    protected val inputRegisters:Array[Int] = Array.fill(6)(-1)
    protected val outputRegisters:Array[Int] = Array.fill(6)(-1)

    override def getInputRegister(inDir:Int, inPort:Int):Option[Int] =
        if ((inDirMask & 1 << inDir) != 0 && inputRegisters(inDir) > -1) Some(inputRegisters(inDir)) else None

    override def getOutputRegister(outDir:Int, outPort:Int):Option[Int] =
        if ((outDirMask & 1 << outDir) != 0 && outputRegisters(outDir) > -1) Some(outputRegisters(outDir)) else None

    override def allocate(assembler:ICAssembler):Unit = {
        inputRegisters.map(_ => -1)
        outputRegisters.map(_ => -1)

        for (dir <- 0 until 6) {
            if ((outDirMask & 1 << dir) != 0)
                outputRegisters(dir) = assembler.allocRegisterID()
        }
    }

    override def locate(assembler:ICAssembler, pathFinder:ICAssemblerPathFinder):Unit = {
        for (dir <- 0 until 6) {
            if ((inDirMask & 1 << dir) != 0) {
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
        val remaps = mutable.Map[Int, Int]()

        for ((inDir, regID) <- inputSignalMap) {
            val outsideInputReg = inputRegisters(inDir) // Connent this tile's input register...
            val insideInputReg = regID // To this register from inside the nested map

            remaps += (insideInputReg -> outsideInputReg)
        }

        for ((outDir, regID) <- outputSignalMap) {
            val outsideOutputReg = outputRegisters(outDir)
            val insideOutputReg = regID

            remaps += (insideOutputReg -> outsideOutputReg)
        }

        addMapToAssembler(assembler, remaps)
    }
}
