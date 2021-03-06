package mrtjp.fengine.spec

import mrtjp.fengine.TileCoord
import mrtjp.fengine.assemble.PathFinderNode

import scala.collection.mutable

class PathFinderNodeSpec extends FEFlatSpec
{
    "A PathFinderNode" should "have unique hashes within bounds" in {
        val hashSet = mutable.HashSet[Int]()
        for (xb <- 0 until 8) for (yb <- 0 until 8) for (zb <- 0 until 5) {
            for (dir <- 0 until 6) for (port <- 0 until 16) {
                val node = PathFinderNode(new TileCoord(1<<xb, 1<<yb, 1<<zb), dir, port)
                val hashcode = node.hashCode()

                assert(!hashSet.contains(hashcode), s"Unexpected hash collision: node:${node.toString}, hash:$hashcode")
                hashSet.add(hashcode)
            }
        }
    }
}
