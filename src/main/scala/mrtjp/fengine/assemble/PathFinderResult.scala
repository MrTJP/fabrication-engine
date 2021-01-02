package mrtjp.fengine.assemble

class PathFinderResult(
    val portToOutputRegisters:Map[Int, Set[Int]], // Map of starting port to all output register IDs found
    val portToInputRegisters:Map[Int, Set[Int]] // Map of starting port to all input register IDs found
) {
    lazy val outputRegisters:Set[Int] = portToOutputRegisters.flatMap(_._2).toSet // Set of all output registers found
    lazy val inputRegisters:Set[Int] = portToInputRegisters.flatMap(_._2).toSet // Set of all input registers found
}