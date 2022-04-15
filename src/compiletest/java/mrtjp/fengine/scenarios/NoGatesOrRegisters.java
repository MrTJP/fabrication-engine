package mrtjp.fengine.scenarios;

import mrtjp.fengine.framework.api.FabricationScenario;
import mrtjp.fengine.testcases.FabricationTestScenario;
import mrtjp.fengine.tiles.FEBasicTileMap;

/**
 * Test Case: Map is completely empty
 * <p>
 * Contains:
 * <ul>
 * <li>Nothing
 * </ul>
 * <p>
 * Layout (Spans 1x1x1)
 * <pre>
 * *Z -- 0 -- (x)
 * |
 * 0
 * |
 * (y)
 * </pre>
 */
@FabricationScenario
public class NoGatesOrRegisters extends FabricationTestScenario {

    @Override
    public void init() {
        setDescription("Empty map containing no tiles");

        FEBasicTileMap map = new FEBasicTileMap();

        // use empty map
        setRootMap(map);
    }
}
