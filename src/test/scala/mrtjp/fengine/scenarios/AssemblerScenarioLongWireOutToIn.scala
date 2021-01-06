package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.assemble.{ICAssembler, ICFlatMap}
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, PortlessWireTileImpl, TTestFETileMap}

/**
  * Test Case: Single output gate connected to single input gate with wire
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitSouth)
  *     * (Y) Input Tile (inMask = bitNorth)
  *     * (w) Wire (connMask = bitNorth|bitSouth, inPortFilterMask = 0xFFFF, outPortMixMask = 0xFFFF)
  *           - Vertical column of wire connecting gate X south output to gate Y north input
  *
  * Layout (Spans 1x5x1)
  *    *Z -- 0 -- 1 (x)
  *     |
  *     0    X
  *     |
  *     1    w
  *     |
  *     2    w
  *     |
  *     3    w
  *     |
  *     4    Y
  *     (y)
  */
class AssemblerScenarioLongWireOutToIn extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 5

        // Add driving tile at (0, 0)
        val sourceTileCoord = new TileCoord(0, 0, 0)
        val sourceGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceTileCoord, sourceGate)

        // Add driven tile at (0, 4)
        val sinkTileCoord = new TileCoord(0, 4, 0)
        val sinkGate = new PortlessGateTileImpl("Y", inDirMask = bitNorth, outDirMask = 0)
        addTile(sinkTileCoord, sinkGate)

        // Add vertical column of wires (0,1) -> (0, 3)
        for (y <- sourceTileCoord.y+1 to sinkTileCoord.y-1)
            addTile(new TileCoord(0, y, 0), new PortlessWireTileImpl(bitNorth|bitSouth))
    }

    override def rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sourceGate.gate, map.sinkGate.gate)

    override val expectedRegisters = Seq(map.sourceGate.registers(dirSouth))

    override val expectedRelationships = Seq(
        (map.sourceGate.gate, Seq(), Seq(map.sourceGate.registers(dirSouth))),
        (map.sinkGate.gate, Seq(map.sourceGate.registers(dirSouth)), Seq())
    )
}
