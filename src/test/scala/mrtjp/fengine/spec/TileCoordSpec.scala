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

    "A TileCoord" should "compare correctly" in {
        val coordA = TileCoord(1, 1, 1)
        val coordB = TileCoord(1, 1, 1)
        val coordC = TileCoord(2, 1, 1)
        val coordD = TileCoord(1, 2, 1)
        val coordE = TileCoord(1, 1, 2)
        val coordACopy = coordA.copy

        // Different instances can equal
        assert(coordA == coordB)

        // Copies should equal value but not ref
        assert(coordA == coordACopy)
        assert(!(coordA eq coordACopy))

        // Different coords should not equal
        assert(coordA != coordC)
        assert(coordA != coordD)
        assert(coordA != coordE)

        // TileCoord cannot equal another object type
        assert(coordA != new Object)
    }

    "A TileCoord" should "respond correctly to math ops" in {

        // Addition
        assert(TileCoord(1, 2, 3) + 1 == TileCoord(2, 3, 4))
        assert(TileCoord(1, 2, 3) + (1, 2, 3) == TileCoord(2, 4, 6))
        assert(TileCoord(1, 2, 3) + TileCoord(1, 2, 3) == TileCoord(2, 4, 6))

        // Subtraction
        assert(TileCoord(1, 2, 3) - 1 == TileCoord(0, 1, 2))
        assert(TileCoord(1, 2, 3) - (1, 2, 3) == TileCoord(0, 0, 0))
        assert(TileCoord(1, 2, 3) - TileCoord(1, 2, 3) == TileCoord(0, 0, 0))

        // Multiplication
        assert(TileCoord(1, 2, 3) * 2 == TileCoord(2, 4, 6))
        assert(TileCoord(1, 2, 3) * (1, 2, 3) == TileCoord(1, 4, 9))
        assert(TileCoord(1, 2, 3) * TileCoord(1, 2, 3) == TileCoord(1, 4, 9))

        // Divide
        assert(TileCoord(2, 4, 6) / 2 == TileCoord(1, 2, 3))
        assert(TileCoord(2, 4, 6) / (1, 2, 2) == TileCoord(2, 2, 3))
        assert(TileCoord(2, 4, 6) / TileCoord(1, 2, 2) == TileCoord(2, 2, 3))

        // Negate
        assert(-TileCoord(1, 2, 3) == TileCoord(-1, -2, -3))

        // Min
        assert(TileCoord(1, 20, 3).min(TileCoord(10, 2, 30)) == TileCoord(1, 2, 3))

        // Max
        assert(TileCoord(1, 20, 3).max(TileCoord(10, 2, 30)) == TileCoord(10, 20, 30))
    }

    "A TileCoord" should "have produce valid direction masks and lists" in {
        for (mask <- 0 to TileCoord.dirMaskAll) {
            val listFromMask = TileCoord.maskToDirs(mask)
            val maskFromList = TileCoord.dirsToMask(listFromMask)
            assert(mask == maskFromList)
        }
    }

    "A TileCoord" should "produce valid port masks and lists" in {
        for (mask <- 0 to TileCoord.portMaskAll) {
            val listFromMask = TileCoord.maskToPorts(mask)
            val maskFromList = TileCoord.portsToMask(listFromMask)
            assert(mask == maskFromList)
        }
    }

}
