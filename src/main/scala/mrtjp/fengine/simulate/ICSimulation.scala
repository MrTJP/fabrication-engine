package mrtjp.fengine.simulate

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ICSimulation(
    $registers:Seq[ICRegister], //Registers in the circuit, indexed by regID
    $gates:Seq[ICGate], //Gates in the circuit, indexed by gateID
    $regDependents:Map[Int, Seq[Int]] //Register deps [regID -> Seq(gateID)]
)
{
    val registers:Array[ICRegister] = $registers.toArray
    val gates:Array[ICGate] = $gates.toArray

    val regDependents:Array[Array[Int]] = {
        val b = Array.newBuilder[Array[Int]]
        for (i <- registers.indices)
            b += $regDependents.getOrElse(i, Seq.empty).toArray
        b.result()
    }

    val changeQueue:ListBuffer[Int] = new ListBuffer[Int]

    def getRegisterMap:Seq[ICRegister] = registers

    def computeAll():Boolean =
    {
        for (i <- gates)
            i.compute(this)
        propagate(None)
        changeQueue.isEmpty
    }

    def getRegVal[T](regID:Int):T = registers(regID).getVal[T]

    def queueRegVal[T](regID:Int, newVal:T):Unit =
    {
        if (registers(regID).queueVal[T](newVal))
            changeQueue += regID
    }

    def propagate(callback:Option[ICSimulationCallback]):Boolean =
    {
        val allChanges = Set.newBuilder[Int]
        var changes:List[Int] = List.empty

        val allComputes = Array.fill[Int](gates.length)(0)
        var computes = mutable.Set[Int]()

        var hasOverflow = false
        var overflowGateID = -1

        def fetch():Unit =
        {
            changes = changeQueue.result()
            changeQueue.clear()
            allChanges ++= changes
        }

        def checkOverflow():Unit =
        {
            for (i <- computes) {
                allComputes(i) += 1
                if (allComputes(i) > 32) {
                    hasOverflow = true
                    overflowGateID = i
                    return
                }
            }

            computes.clear()
        }

        fetch()

        do {
            for (regID <- changes)
                registers(regID).pushVal(this)

            for (regID <- changes) {
                for (gateID <- regDependents(regID)) {
                    if (!computes(gateID)) {
                        gates(gateID).compute(this)
                        computes += gateID
                    }
                }
            }

            fetch()
            checkOverflow()
        }
        while (changes.nonEmpty && !hasOverflow)

        if (hasOverflow)
            callback.foreach(_.icEventComputeOverflow(changes, Seq(overflowGateID), 32))

        val ch = allChanges.result()
        if (ch.nonEmpty) {
            callback.foreach(_.registersDidChange(ch))
            true
        } else
            false
    }

    override def toString:String = {
        val builder = new mutable.StringBuilder()
        builder.append("IntegratedCircuit: DUMP\n")
        builder.append("=== Registers ===\n")
        for (i <- registers.indices) {
            val reg = registers(i)
            builder.append(s"reg[$i] = ")
//            reg match {
//                case ValueRegister(r:Byte) => builder.append(s"$r {byte}")
//                case ValueRegister(r:Long) => builder.append(s"$r {long}")
//                case ValueRegister(r:Int)  => builder.append(s"$r {int}")
//                case StaticValueRegister(r:Byte) => builder.append(s"$r {byte}")
//                case _ => builder.append(s"${reg.getVal} {unknown}")
//            }
            builder.append("\n")
        }
        builder.result()
    }
}