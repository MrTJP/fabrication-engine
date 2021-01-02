package mrtjp.fengine.simulate

trait ICSimulationCallback
{
    def registersDidChange(registers:Set[Int]):Unit

    def icDidThrowErrorFlag(flag:Int, registers:Seq[Int], gates:Seq[Int]):Unit

    def icEventComputeOverflow(registers:Seq[Int], gates:Seq[Int], computeLimit:Int):Unit
}
