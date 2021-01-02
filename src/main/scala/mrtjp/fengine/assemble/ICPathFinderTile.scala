package mrtjp.fengine.assemble

trait ICPathFinderTile
{
    /**
      * Used to calculate where an incoming signal can propagate to.
      *
      * @param inDir    The direction from which the propagating signal is coming from
      * @param inPort   The port for which the incoming signal is associated with
      *
      * @return Propagation test function `f(Int, Int) => Boolean` such that if the incoming signal (inDir, inPort)
      *         is allowed to propagate out to direction `outDir` on port `outPort`, then:
      *             f(outDir, outPort) == true
      *
      */
    def propagationFunc(inDir:Int, inPort:Int):(Int, Int) => Boolean = {(_, _) => false}

    /**
      * Optionally returns the ID of a driven register if one exists for the given parameters.
      *
      * @param outDir  The output direction that is being queried
      * @param outPort The output port that is being queried
      *
      * @return A register ID if this gate is driving `port` on side `outputDir`, or None
      */
    def getOutputRegister(outDir:Int, outPort:Int):Option[Int] = None

    /**
      * Optionally returns the ID of a driving register if one exists for the given parameters.
      *
      * @param inDir  The input direction that is being queried
      * @param inPort The input port that is being queried
      *
      * @return A register ID if this gate is driven by `port` on side `outputDir`, or None
      */
    def getInputRegister(inDir:Int, inPort:Int):Option[Int] = None
}