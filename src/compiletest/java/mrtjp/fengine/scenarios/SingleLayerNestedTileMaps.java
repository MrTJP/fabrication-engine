package mrtjp.fengine.scenarios;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.testcases.FabricationTestScenario;
import mrtjp.fengine.testimpl.PortlessGateTileImpl;
import mrtjp.fengine.testimpl.PortlessIOTileTestImpl;
import mrtjp.fengine.testimpl.PortlessNestedTileMapTileTestImpl;
import mrtjp.fengine.tiles.FEBasicTileMap;
import mrtjp.fengine.tiles.PortlessNestedTileMapTile;

import java.util.Collections;
import java.util.Map;

import static mrtjp.fengine.TileCoord.*;

/**
 * Test Case: Single output gate connected to single input gate with wire
 * <p>
 * Contains:
 * <ul>
 * <li> (i) Input tile (ioDir = dirSouth)
 * <li> (o) Output tile (ioDir = dirNorth)
 * <li> (g1, g2) Gate (inMask = bitNorth, outMask = bitSouth)
 * <li> (n1, n2) Nested map tile (inMask = bitNorth, outMask = bitSouth)
 * </ul>
 * <p>
 * Top-level layout (Spans 1x5x1)
 * <pre>
 *    *y -- 0 (x)
 *     |
 *     0    i
 *     |    ↓
 *     1    g1
 *     |    ↓
 *     2   [n1]
 *     |    ↓
 *     3   [n2]
 *     |    ↓
 *     4    g2
 *     |    ↓
 *     5    o
 *     (z)
 * </pre>
 *
 * Layout of nested maps (n1, n2) (Spans 1x3x1)
 * <pre>
 *    *y -- 0 (x)
 *     |
 *     0    i
 *     |    ↓
 *     1    g1
 *     |    ↓
 *     2    o
 *     (z)
 * </pre>
 */
@FabricationScenario
public class SingleLayerNestedTileMaps extends FabricationTestScenario {

    private static class NestedMap extends FEBasicTileMap {

        // Input tile
        final TileCoord inputCoord = new TileCoord(0, 0, 0);
        final int inputRegId = 0;
        final PortlessIOTileTestImpl inputTile = new PortlessIOTileTestImpl("i", true, dirSouth, inputRegId);

        // Gate tile
        final TileCoord gateCoord = new TileCoord(0, 0, 1);
        final PortlessGateTileImpl gateTile = new PortlessGateTileImpl("g", bitNorth, bitSouth);

        //  Output tile
        final TileCoord outputCoord = new TileCoord(0, 0, 2);
        final int outputRegId = 1;
        final PortlessIOTileTestImpl outputTile = new PortlessIOTileTestImpl("o", false, dirNorth, outputRegId);

        public NestedMap() {
            NestedMap.this.addTile(inputCoord, inputTile);
            NestedMap.this.addTile(gateCoord, gateTile);
            NestedMap.this.addTile(outputCoord, outputTile);
        }
    }

    @Override
    public void init() {
        setDescription("Top level layout with 1 input and 1 output IOs, 2 gates, and 2 nested tile map gates.");

        FEBasicTileMap map = new FEBasicTileMap();

        // Input tile
        TileCoord inputCoord = new TileCoord(0, 0, 0);
        int inputRegId = 0;
        PortlessIOTileTestImpl inputTile = new PortlessIOTileTestImpl("i", true, dirSouth, inputRegId);
        map.addTile(inputCoord, inputTile);

        // Gate 1
        TileCoord g1Coord = new TileCoord(0, 0, 1);
        PortlessGateTileImpl g1Tile = new PortlessGateTileImpl("g", bitNorth, bitSouth);
        map.addTile(g1Coord, g1Tile);

        // Nested map 1
        TileCoord n1Coord = new TileCoord(0, 0, 2);
        NestedMap n1 = new NestedMap();
        Map<Integer, Integer> n1InputMap = Collections.singletonMap(dirNorth, n1.inputRegId);
        Map<Integer, Integer> n1OutputMap = Collections.singletonMap(dirSouth, n1.outputRegId);
        PortlessNestedTileMapTile n1Tile = new PortlessNestedTileMapTileTestImpl("n", bitNorth, bitSouth, n1InputMap, n1OutputMap, n1);
        map.addTile(n1Coord, n1Tile);

        // Nested map 2
        TileCoord n2Coord = new TileCoord(0, 0, 3);
        NestedMap n2 = new NestedMap();
        Map<Integer, Integer> n2InputMap = Collections.singletonMap(dirNorth, n2.inputRegId);
        Map<Integer, Integer> n2OutputMap = Collections.singletonMap(dirSouth, n2.outputRegId);
        PortlessNestedTileMapTile n2Tile = new PortlessNestedTileMapTileTestImpl("n", bitNorth, bitSouth, n2InputMap, n2OutputMap, n2);
        map.addTile(n2Coord, n2Tile);

        // Gate 2
        TileCoord g2Coord = new TileCoord(0, 0, 4);
        PortlessGateTileImpl g2Tile = new PortlessGateTileImpl("g", bitNorth, bitSouth);
        map.addTile(g2Coord, g2Tile);

        // Output tile
        TileCoord outputCoord = new TileCoord(0, 0, 5);
        int outputRegId = 1;
        PortlessIOTileTestImpl outputTile = new PortlessIOTileTestImpl("i", false, dirNorth, outputRegId);
        map.addTile(outputCoord, outputTile);

        // Declare elements
        setRootMap(map);
        addGate(g1Tile.gate);
        addGate(n1.gateTile.gate);
        addGate(n2.gateTile.gate);
        addGate(g2Tile.gate);

        addStaticRegister(inputRegId, inputTile.register);
        addStaticRegister(outputRegId, g2Tile.registers[dirSouth]);
        addRegister(g1Tile.registers[dirSouth]);
        addRegister(n1.gateTile.registers[dirSouth]);
        addRegister(n2.gateTile.registers[dirSouth]);

        // Declare relationships
        gateReadsFromRegister(g1Tile.gate, inputTile.register);
        gateWritesToRegister(g1Tile.gate, g1Tile.registers[dirSouth]);

        gateReadsFromRegister(n1.gateTile.gate, g1Tile.registers[dirSouth]);
        gateWritesToRegister(n1.gateTile.gate, n1.gateTile.registers[dirSouth]);

        gateReadsFromRegister(n2.gateTile.gate, n1.gateTile.registers[dirSouth]);
        gateWritesToRegister(n2.gateTile.gate, n2.gateTile.registers[dirSouth]);

        gateReadsFromRegister(g2Tile.gate, n2.gateTile.registers[dirSouth]);
        gateWritesToRegister(g2Tile.gate, g2Tile.registers[dirSouth]);
    }
}
