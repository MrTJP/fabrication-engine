package mrtjp.fengine.testimpl

import grizzled.slf4j.Logging
import mrtjp.fengine.TileCoord
import mrtjp.fengine.tiles.FETileMap

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Base trait for all FETileMap objects used for testing
  */
trait TTestFETileMap extends FETileMap with Logging {

    val renderWidth:Int
    val renderHeight:Int

    def printMap():Unit = {
        for (y <- 0 until renderHeight) {

            val sbList = new ArrayBuffer[mutable.StringBuilder]()
            for (x <- 0 until renderWidth) {

                val tileRender = getTile(TileCoord(x, y, 0)) match {
                    case Some(tile:TTestFETile) => tile.getStringRenderRows
                    case Some(_) => unknownTile
                    case None => noTile
                }

                while (sbList.size < tileRender.size)
                    sbList += new mutable.StringBuilder()
                for (r <- tileRender.indices) {
                    sbList(r) ++= tileRender.apply(r)
                }
            }

            for (sb <- sbList) logger.info(sb.result())
        }
    }

    val unknownTile:Seq[String] = Seq(
        "╭───────╮",
        "│ ----- │",
        "│ -???- │",
        "│ ----- │",
        "╰───────╯"
    )

    val noTile:Seq[String] = Seq(
        "╭       ╮",
        "         ",
        "         ",
        "         ",
        "╰       ╯"
    )
}

object TTestFETileMap {
    //Reserved registers
    val REG_IN_BASE = 0
    def REG_IN(dir:Int, port:Int):Int = REG_IN_BASE+dir*16+port

    val REG_OUT_BASE = 96
    def REG_OUT(dir:Int, port:Int):Int = REG_OUT_BASE+dir*16+port

    val REG_SYSTIME = 192

    val REG_ZERO = 193
    val REG_ONE = 194
}
