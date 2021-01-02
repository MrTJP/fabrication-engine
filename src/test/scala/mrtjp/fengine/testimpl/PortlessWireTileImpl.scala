package mrtjp.fengine.testimpl

import mrtjp.fengine.TileCoord._
import mrtjp.fengine.tiles.{FEPortlessWireTile, FETile}

class PortlessWireTileImpl(override val connMask:Int) extends FETile with FEPortlessWireTile with TTestFETile
{
    override def getStringRenderRows:Seq[String] = {
        val cN = if ((connMask&bitNorth) != 0) "║" else " "
        val cE = if ((connMask&bitEast) != 0) "═" else " "
        val cS = if ((connMask&bitSouth) != 0) "║" else " "
        val cW = if ((connMask&bitWest) != 0) "═" else " "
        val cC = "╳╨╥║╡╝╗╣╞╚╔╠═╩╦╬".apply(connMask>>2)

        Seq(
            s"╭   $cN   ╮",
            s"    $cN    ",
            s"$cW$cW$cW$cW$cC$cE$cE$cE$cE",
            s"    $cS    ",
            s"╰   $cS   ╯"
        )
    }
}
