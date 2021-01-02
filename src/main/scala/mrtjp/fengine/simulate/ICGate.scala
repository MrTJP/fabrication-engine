package mrtjp.fengine.simulate

/** A single compute instance in an IC simulation
  *
  * This gate or function will:
  *   - Read values from its dependency registers
  *   - Perform some calculation to obtain output value
  *   - Queue new output value to its output register
  *
  * Should be instantiated during assembly, when concrete values for dependents and dependencies are known.
  * Calculation should ONLY be using registers declared as dependents/dependencies when added to the assembler.
  */
trait ICGate
{
    /**
      * Performs the calculation as a function of dependency registers, and queue results to dependent registers.
      *
      * Suppose this gate performs the following calculation:
      * {{{
      *     outA = f(inA, inB)
      * }}}
      *
      * This gate has declared 2 dependency registers (`inA` and `inB`) and 1 dependent register (`outA`). At this point,
      * the register IDs for all of these should be known (`inAID`, `inBID`, and `outAID`).
      *
      * Given a simulation object `ic` that holds all of these registers, the compute is performed by:
      * {{{
      *     //Obtain the current values:
      *     val inA = ic.getRegVal(inAID)
      *     val inB = ic.getRegVal(inBID)
      *
      *     // Perform the calculation
      *     val outA = f(inA, inB)
      *
      *     // Queue the result
      *     ic.queueRegVal(outAID, outA)
      * }}}
      *
      * @param ic The simulation environment that is requesting the compute.
      */
    def compute(ic:ICSimulation):Unit

    def compute(inputs:Array[AnyVal], outputs:Array[AnyVal]):AnyVal = 0
}
