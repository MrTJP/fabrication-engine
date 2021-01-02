package mrtjp.fengine.tiles

/** A directionally connected wire that forwards propagation to the same port
  *
  *   - Signal can enter from masked direction from msked port
  *   - Signal can exit to masked direction to the same port it entered from
  */
trait FEPortPassthroughWireTile extends FETile
{
    /** Mask of allowed directions */
    val connMask:Int

    /** Mask of allowed ports */
    val portMask:Int

    override def propagationFunc(inDir:Int, inPort:Int):(Int, Int) => Boolean = { (outdir:Int, outPort:Int) =>
        (portMask&1<<inPort) != 0 && inPort == outPort &&
                (connMask&1<<inDir) != 0 && (connMask&1<<outdir) != 0
    }
}
