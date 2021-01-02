package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl._

/**
  * Test Case: Single output gate connected to single input gate with wire
  *
  * Contains:
  *     * (i1) Input tile (ioDir = dirSouth)
  *     * (o1) Output tile (ioDir = dirNorth)
  *     * (g1, g2) Gate (inMask = bitNorth, outMask = bitSouth)
  *     * (n1, n2) Nested map tile (inMask = bitNorth, outMask = bitSouth)
  *
  * ROOT Layout (Spans 1x5x1)
  *    *Z -- 0 (x)
  *     |
  *     0    i1
  *     |    ↓
  *     1    g1
  *     |    ↓
  *     2    n1
  *     |    ↓
  *     3    n2
  *     |    ↓
  *     4    g2
  *     |    ↓
  *     5    o1
  *     (y)
  *
  * N Layout (Spans 1x3x1)
  *    *Z -- 0 (x)
  *     |
  *     0    i1
  *     |    ↓
  *     1    g1
  *     |    ↓
  *     2    o1
  *     (y)
  */
class AssemblerScenarioAdjacentNestedMap extends AssemblerScenario
{
    private def createN = new TTestFETileMap {
        override val renderWidth:Int = 1
        override val renderHeight:Int = 3

        // Gateless input tile
        val i1Coord = new TileCoord(0, 0, 0)
        val i1RegID = 0
        val i1 = new PortlessIOTileTestImpl("i", true, dirSouth, i1RegID)
        addTile(i1Coord, i1)

        // Center gate
        val g1Coord = new TileCoord(0, 1, 0)
        val g1 = new PortlessGateTileImpl("g", inDirMask = bitNorth, outDirMask = bitSouth)
        addTile(g1Coord, g1)

        // Gateless output tile
        val o1Coord = new TileCoord(0, 2, 0)
        val o1RegID = 1
        val o1 = new PortlessIOTileTestImpl("o", false, dirNorth, o1RegID)
        addTile(o1Coord, o1)
    }

    private val n1Map = createN

    private val n2Map = createN

    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 6

        //input tile
        val inputTileCoord = new TileCoord(0, 0, 0)
        val inputTileRegID = 0
        val inputTile = new PortlessIOTileTestImpl("i", true, dirSouth, inputTileRegID)
        addTile(inputTileCoord, inputTile)

        //first gate
        val g1Coord = new TileCoord(0, 1, 0)
        val g1 = new PortlessGateTileImpl("g", inDirMask = bitNorth, outDirMask = bitSouth)
        addTile(g1Coord, g1)

        //first nested map
        val n1Coord = new TileCoord(0, 2, 0)
        val n1 = new PortlessNestedTileMapTile("n", bitNorth, bitSouth, Map((dirNorth, n1Map.i1RegID)), Map((dirSouth, n1Map.o1RegID)), n1Map)
        addTile(n1Coord, n1)

        //second nested map
        val n2Coord = new TileCoord(0, 3, 0)
        val n2 = new PortlessNestedTileMapTile("n", bitNorth, bitSouth, Map((dirNorth, n2Map.i1RegID)), Map((dirSouth, n2Map.o1RegID)), n2Map)
        addTile(n2Coord, n2)

        //second gate
        val g2Coord = new TileCoord(0, 4, 0)
        val g2 = new PortlessGateTileImpl("g", inDirMask = bitNorth, outDirMask = bitSouth)
        addTile(g2Coord, g2)

        // output tile
        val outputTileCoord = new TileCoord(0, 5, 0)
        val outputTileRegID = 1
        val outputTile = new PortlessIOTileTestImpl("o", false, dirNorth, outputTileRegID)
        addTile(outputTileCoord, outputTile)
    }


    override val rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.g1.gate, n1Map.g1.gate, n2Map.g1.gate, map.g2.gate)
    override val expectedRegisters = Seq(map.inputTile.register, map.g1.registers(dirSouth), n1Map.g1.registers(dirSouth), n2Map.g1.registers(dirSouth), map.g2.registers(dirSouth))

    override val expectedRelationships = Seq(
        (map.g1.gate, Seq(map.inputTile.register), Seq(map.g1.registers(dirSouth))),
        (n1Map.g1.gate, Seq(map.g1.registers(dirSouth)), Seq(n1Map.g1.registers(dirSouth))),
        (n2Map.g1.gate, Seq(n1Map.g1.registers(dirSouth)), Seq(n2Map.g1.registers(dirSouth))),
        (map.g2.gate, Seq(n2Map.g1.registers(dirSouth)), Seq(map.g2.registers(dirSouth)))
    )
}
