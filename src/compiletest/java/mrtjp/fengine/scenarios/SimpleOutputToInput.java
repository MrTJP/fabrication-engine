package mrtjp.fengine.scenarios;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.framework.api.FabricationParameterIntRange;
import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.testcases.FabricationTestScenario;
import mrtjp.fengine.testimpl.PortlessGateTileImpl;
import mrtjp.fengine.testimpl.PortlessWireTileImpl;
import mrtjp.fengine.tiles.FEBasicTileMap;

import static mrtjp.fengine.TileCoord.*;

/**
 * Test Case: Single output gate connected to single input gate.
 * <p>
 * Parameters:
 * <ul>
 * <li> xDist - Number of wire tiles separating the two gates (0 == adjacent with no wires)
 * </ul>
 * <p>
 * Contains:
 * <ul>
 * <li> (A) Output Tile (inMask = bitSouth)
 * <li> (B) Input Tile (inMask = bitNorth)
 * <li> 0-n (w) Wire Tile (connMask = bitNorth | bitSouth)
 * </ul>
 * <p>
 * Layout Spans [ 1 x (2+xDist) x 1 ]
 * <pre>
 * *Y ------ 0 -- (x)
 * |
 * 0        A
 * |
 * ...      w
 * |
 * zDist+1  B
 * |
 * (z)
 * </pre>
 */
@FabricationScenario
public class SimpleOutputToInput extends FabricationTestScenario {

    private final int zDist;

    public SimpleOutputToInput(@FabricationParameterIntRange (min = 0, max = 3) int zDist) {
        this.zDist = zDist;
    }

    @Override
    public void init() {
        setDescription("Single output of Gate X connects to single input of Gate Y");
        addParam("zDist", String.valueOf(zDist));

        FEBasicTileMap map = new FEBasicTileMap();

        int z = 0;

        // Gate A
        TileCoord gateACoord = new TileCoord(0, 0, z++);
        PortlessGateTileImpl gateA = new PortlessGateTileImpl("A", 0, bitSouth);
        map.addTile(gateACoord, gateA);

        // Wires between A <--> B
        PortlessWireTileImpl[] wires = new PortlessWireTileImpl[zDist];
        for (int i = 0; i < zDist; i++) {
            TileCoord wireCoord = new TileCoord(0, 0, z++);
            wires[i] = new PortlessWireTileImpl(bitSouth | bitNorth);
            map.addTile(wireCoord, wires[i]);
        }

        // Gate B
        TileCoord gateBCoord = new TileCoord(0, 0, z++);
        PortlessGateTileImpl gateB = new PortlessGateTileImpl("B", bitNorth, 0);
        map.addTile(gateBCoord, gateB);

        // Wire registers
        for (PortlessWireTileImpl wire : wires) {
            addWireTileInput(wire, gateA.registers[dirSouth]);
        }

        // Declare expected gates and registers
        setRootMap(map);

        addGate(gateA.gate);
        addGate(gateB.gate);

        addRegister(gateA.registers[dirSouth]);

        // Gate relationships
        gateWritesToRegister(gateA.gate, gateA.registers[dirSouth]);
        gateReadsFromRegister(gateB.gate, gateA.registers[dirSouth]);
    }
}
