package mrtjp.fengine.scenarios

import mrtjp.fengine.TileCoord
import mrtjp.fengine.TileCoord._
import mrtjp.fengine.testimpl.{PortlessGateTileImpl, PortlessWireTileImpl, TTestFETileMap}

/**
  * Test Case: Single output gate connected to single output gate with single wire
  *
  * Contains:
  *     * (X) Output Tile (outMask = bitSouth)
  *     * (Y) Output Tile (outMask = bitNorth)
  *     * (w) Wire (connMask = bitNorth|bitSouth)
  *
  * Layout (Spans 1x3x1)
  *    *Z -- 0 -- 1 (x)
  *     |
  *     0    X
  *     |
  *     1    w
  *     |
  *     2    Y
  *     (y)
  */
class AssemblerScenarioOneWireOutToOut extends AssemblerScenario
{
    private val map = new TTestFETileMap {
        override val renderWidth = 1
        override val renderHeight = 3

        // Add the driving tile at (0, 0)
        val sourceXCoord = new TileCoord(0, 0, 0)
        val sourceXGate = new PortlessGateTileImpl("X", inDirMask = 0, outDirMask = bitSouth)
        addTile(sourceXCoord, sourceXGate)

        // Add the driven tile at (0, 4)
        val sourceYCoord = new TileCoord(0, 2, 0)
        val sourceYGate = new PortlessGateTileImpl("Y", inDirMask = 0, outDirMask = bitNorth)
        addTile(sourceYCoord, sourceYGate)

        // Add wire at (0,1)
        addTile(new TileCoord(0, 1, 0), new PortlessWireTileImpl(bitNorth|bitSouth))
    }

    override def rootMap:TTestFETileMap = map

    override val expectedGates = Seq(map.sourceXGate.gate, map.sourceYGate.gate)

    override val expectedRegisters = Seq(
        map.sourceXGate.registers(dirSouth),
        map.sourceYGate.registers(dirNorth)
    )

    override val expectedRelationships = Seq(
        (map.sourceXGate.gate, Seq(), Seq(map.sourceXGate.registers(dirSouth))),
        (map.sourceYGate.gate, Seq(), Seq(map.sourceYGate.registers(dirNorth)))
    )
}
