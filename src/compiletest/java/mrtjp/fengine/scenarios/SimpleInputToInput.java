package mrtjp.fengine.scenarios;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.framework.api.FabricationParameterIntRange;
import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.testcases.FabricationTestScenario;
import mrtjp.fengine.testimpl.PortlessGateTileImpl;
import mrtjp.fengine.testimpl.PortlessWireTileImpl;
import mrtjp.fengine.tiles.FEBasicTileMap;

import static mrtjp.fengine.TileCoord.bitNorth;
import static mrtjp.fengine.TileCoord.bitSouth;

/**
 * Test Case: Two gates with inputs that connect to each other. No registers should be allocated since neither gate
 * drives the other.
 * <p>
 * Parameters:
 * <ul>
 * <li> yDist - Number of wire tiles separating the two gates (0 == adjacent with no wires)
 * </ul>
 * <p>
 * Contains:
 * <ul>
 * <li> (X) Input Tile (inMask = bitSouth)
 * <li> (Y) Input Tile (inMask = bitNorth)
 * <li> 0-n (w) Wire Tile (connMask = bitNorth | bitSouth)
 * </ul>
 * <p>
 * Layout Spans [ 1 x (2+xDist) x 1 ]
 * <pre>
 * *y ------ 0 -- (x)
 * |
 * 0        X
 * |
 * ...      w
 * |
 * yDist+1  Y
 * |
 * (z)
 * </pre>
 */
@FabricationScenario
public class SimpleInputToInput extends FabricationTestScenario {

    private final int zDist;

    public SimpleInputToInput(@FabricationParameterIntRange (min = 0, max = 3) int yDist) {
        this.zDist = yDist;
    }

    @Override
    public void init() {
        setDescription("Input of two gates are connected to each other");
        addParam("zDist", String.valueOf(zDist));

        FEBasicTileMap map = new FEBasicTileMap();

        int z = 0;

        // Gate A
        TileCoord gateACoord = new TileCoord(0, 0, z++);
        PortlessGateTileImpl gateA = new PortlessGateTileImpl("A", bitSouth, 0);
        map.addTile(gateACoord, gateA);

        // Wires between A <--> B
        for (int i = 0; i < zDist; i++) {
            TileCoord wireCoord = new TileCoord(0, 0, z++);
            map.addTile(wireCoord, new PortlessWireTileImpl(bitSouth | bitNorth));
        }

        // Gate B
        TileCoord gateBCoord = new TileCoord(0, 0, z++);
        PortlessGateTileImpl gateB = new PortlessGateTileImpl("B", bitNorth, 0);
        map.addTile(gateBCoord, gateB);

        // Declare expected gates and registers
        setRootMap(map);
        addGate(gateA.gate);
        addGate(gateB.gate);
    }
}
