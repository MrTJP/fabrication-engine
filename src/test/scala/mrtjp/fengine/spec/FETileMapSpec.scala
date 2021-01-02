package mrtjp.fengine.spec

import mrtjp.fengine.TileCoord
import mrtjp.fengine.tiles.{FETile, FETileMap}
import org.scalamock.scalatest.MockFactory

class FETileMapSpec extends FEFlatSpec with MockFactory
{
    private def testMap:FETileMap = new FETileMap {}

    "An FETileMap" should "grow as tiles are added" in {
        val map = testMap

        assert(map.getTileCount == 0, "Map not initially empty")

        map.addTile(new TileCoord(0, 0, 0), mock[FETile])
        map.addTile(new TileCoord(0, 1, 0), mock[FETile])
        map.addTile(new TileCoord(0, 2, 0), mock[FETile])

        assert(map.getTileCount == 3, "Map did not grow +3")
    }

    it should "not allow overwriting tiles" in {
        val map = testMap

        val pos = new TileCoord(0, 0, 0)
        val original = mock[FETile]
        val replacement = mock[FETile]

        logger.info(s"Adding initial tile to $pos")
        assert(map.addTile(pos, original), "Cant add first tile")

        logger.info("Attempting to overwrite original with replacement")
        assert(!map.addTile(pos, replacement), "Map succeeded on tile overwrite")

        logger.info("Checking if old tile is still there")
        val t = map.getTile(pos)
        assert(t.contains(original), "Map did not have original tile")
    }

    it should "properly fetch added tiles if present" in {
        val map = testMap

        val pos = new TileCoord(0, 0, 0)
        val wrongPos = new TileCoord(1, 1, 0)

        val t = mock[FETile]

        assert(map.getTile(pos).isEmpty, "Empty map returned a tile")
        assert(map.getTile(wrongPos).isEmpty, "Empty map returned a tile")

        logger.info("Setting and getting tile")
        map.addTile(pos, t)
        assert(map.getTile(pos).contains(t), "Map did not return expected tile")
        logger.info("Checking get for empty position ")
        assert(map.getTile(wrongPos).isEmpty, "Map returned tile from empty position")
    }

    it should "remove tiles if pressent" in {
        val map = testMap
        val pos = new TileCoord(0, 0, 0)
        val t = mock[FETile]

        assert(map.removeTile(pos).isEmpty)

        map.addTile(pos, t)
        assert(map.getTileCount == 1)
        assert(map.removeTile(pos).contains(t))
        assert(map.getTileCount == 0)
        assert(map.removeTile(pos).isEmpty)
    }
}
