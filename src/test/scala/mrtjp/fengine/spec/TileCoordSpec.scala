package mrtjp.fengine.spec

import mrtjp.fengine.TileCoord

import scala.collection.mutable

class TileCoordSpec extends FEFlatSpec
{
    "A TileCoord" should "have unique hashes within bounds" in {
        val hashSet = mutable.HashSet[Int]()
        var i = 0
        for (x <- 0 until 256) for (y <- 0 until 256) for (z <- 0 until 32) {
            val coord = new TileCoord(x, y, z)
            val hash = coord.hashCode()

            assert(!hashSet.contains(hash), s"Unexpected hash collision: coord:${coord.toString}, hash:$hash")
            hashSet += hash
            i += 1
        }
        logger.info(s"Verified hash uniqueness for $i TileCoords")
    }
}
