package mrtjp.fengine.testimpl;

import mrtjp.fengine.api.FabricationEngine;
import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.simulate.NoOpGate;
import mrtjp.fengine.tiles.FETile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FabricationEngineTestImpl extends FabricationEngine {

    public static final FabricationEngineTestImpl INSTANCE = new FabricationEngineTestImpl();

    @Override
    public Map<Class<? extends FETile>, String> getTileSerializationMap() {
        Map<Class<? extends FETile>, String> map = new HashMap<>();

        map.put(PortlessGateTileImpl.class, PortlessGateTileImpl.class.getSimpleName());
        map.put(PortlessIOTileTestImpl.class, PortlessIOTileTestImpl.class.getSimpleName());
        map.put(PortlessNestedTileMapTileTestImpl.class, PortlessNestedTileMapTileTestImpl.class.getSimpleName());
        map.put(PortlessWireTileImpl.class, PortlessWireTileImpl.class.getSimpleName());
        map.put(PortPassthroughWireTileImpl.class, PortPassthroughWireTileImpl.class.getSimpleName());

        return map;
    }

    @Override
    public Map<Class<? extends ICGate>, String> getGateSerializationMap() {
        Map<Class<? extends ICGate>, String> map = new HashMap<>();

        map.put(NoOpGate.class, NoOpGate.class.getSimpleName());

        return map;
    }

    @Override
    public Map<Class<? extends ICRegister>, String> getRegisterSerializationMap() {
        return Collections.emptyMap();
    }
}
