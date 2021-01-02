package mrtjp.fengine.tiles

/** A directionally connected wire that ignores ports
  *
  *   - Signal can enter from masked direction from any port
  *   - Signal can exit to masked direction to all ports
  */
trait FEPortlessWireTile extends FETile
{
    /** Mask of allowed directions */
    val connMask:Int

    override def propagationFunc(inDir:Int, inPort:Int):(Int, Int) => Boolean = { (outdir:Int, outPort:Int) =>
        (connMask&1<<inDir) != 0 && (connMask&1<<outdir) != 0
    }
}
