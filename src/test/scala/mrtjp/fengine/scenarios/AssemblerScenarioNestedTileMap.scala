package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl._

/**
  * Test Case: Single output gate connected to single input gate with wire
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitSouth)
  *     * (Y) Input Tile (inMask = bitNorth)
  *     * (w) Wire (connMask = bitNorth|bitSouth, inPortFilterMask = 0xFFFF, outPortMixMask = 0xFFFF)
  *           - Vertical column of wire connecting gate X south output to gate Y north input
  *
  * ROOT Layout (Spans 1x5x1)
  *    *Z -- 0 -- 1 (x)
  *     |
  *     0    X
  *     |
  *     1    w
  *     |
  *     2    N
  *     |
  *     3    w
  *     |
  *     4    Y
  *     (y)
  *
  * N Layout (Spans 1x5x1)
  *    *Z -- 0 -- 1 (x)
  *     |
  *     0    I
  *     |
  *     1    w
  *     |
  *     2    Z
  *     |
  *     3    w
  *     |
  *     4    O
  *     (y)
  */
class AssemblerScenarioNestedTileMap(n_precompile:Boolean) extends AssemblerScenario
{
    private val submap = new TTestFETileMap {
        override val renderWidth:Int = 1
        override val renderHeight:Int = 5

        // Gateless input tile
        val inputTileCoord = new TileCoord(0, 0, 0)
        val inputTileRegID = 0
        val inputTile = new PortlessIOTileTestImpl("I", true, dirSouth, inputTileRegID)
        addTile(inputTileCoord, inputTile)

        // Center gate
        val gateTileCoord = new TileCoord(0, 2, 0)
        val gate = new PortlessGateTileImpl("Z", inDirMask = bitNorth, outDirMask = bitSouth)
        addTile(gateTileCoord, gate)

        // Gateless output tile
        val outputTileCoord = new TileCoord(0, 4, 0)
        val outputTileRegID = 1
        val outputTile = new PortlessIOTileTestImpl("O", false, dirNorth, outputTileRegID)
        addTile(outputTileCoord, outputTile)

        // Add vertical column of wires connecting all 3 gates
        for (y <- Seq(1, 3))
            addTile(new TileCoord(0, y, 0), new PortlessWireTileImpl(bitNorth|bitSouth))

    }

    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 5

        // Add driving tile at (0, 0)
        val sourceTileCoord = new TileCoord(0, 0, 0)
        val sourceGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceTileCoord, sourceGate)

        val nestedTileCoord = new TileCoord(0, 2, 0)
        val nestedTile = new PortlessNestedMapTileTestImpl("N", bitNorth, bitSouth, Map((dirNorth, submap.inputTileRegID)), Map((dirSouth, submap.outputTileRegID)), submap)
        nestedTile.usePrecompiledFlatMap = n_precompile
        addTile(nestedTileCoord, nestedTile)

        // Add driven tile at (0, 4)
        val sinkTileCoord = new TileCoord(0, 4, 0)
        val sinkGate = new PortlessGateTileImpl("Y", inDirMask = bitNorth, outDirMask = 0)
        addTile(sinkTileCoord, sinkGate)

        // Add vertical column of wires (0,1) -> (0, 3)
        for (y <- Seq(1, 3))
            addTile(new TileCoord(0, y, 0), new PortlessWireTileImpl(bitNorth|bitSouth))
    }


    override val rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sourceGate.gate, map.sinkGate.gate, submap.gate.gate)
    override val expectedRegisters = Seq(map.sourceGate.registers(dirSouth), submap.gate.registers(dirSouth))

    override val expectedRelationships = Seq(
        (map.sourceGate.gate, Seq(), Seq(map.sourceGate.registers(dirSouth))),
        (submap.gate.gate, Seq(map.sourceGate.registers(dirSouth)), Seq(submap.gate.registers(dirSouth))),
        (map.sinkGate.gate, Seq(submap.gate.registers(dirSouth)), Seq())
    )
}

object AssemblerScenarioNestedTileMap
{
    def comboIDs:Seq[Int] = 0 until 2

    def createCombo(cID:Int):AssemblerScenarioNestedTileMap =
        new AssemblerScenarioNestedTileMap((cID&1) != 0)
}
