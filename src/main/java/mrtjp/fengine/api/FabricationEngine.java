package mrtjp.fengine.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import mrtjp.fengine.assemble.ICAssemblerImpl;
import mrtjp.fengine.assemble.ICStepThroughAssemblerImpl;
import mrtjp.fengine.serialize.RuntimeTypeAdapterFactory;
import mrtjp.fengine.simulate.*;
import mrtjp.fengine.tiles.FETile;

import java.util.Collections;
import java.util.Map;

public abstract class FabricationEngine {

    private final Gson serializer;

    public FabricationEngine() {
        this.serializer = createSerializer();
    }

    public ICAssembler newAssembler() {
        return new ICAssemblerImpl();
    }

    public ICStepThroughAssembler newStepThroughAssembler() {
        return new ICStepThroughAssemblerImpl();
    }

    private Gson createSerializer() {

        RuntimeTypeAdapterFactory<FETile> tileAdapterFactory = RuntimeTypeAdapterFactory.of(FETile.class);
        for (Map.Entry<Class<? extends FETile>, String> entry : getTileSerializationMap().entrySet()) {
            tileAdapterFactory.registerSubtype(entry.getKey(), entry.getValue());
        }

        RuntimeTypeAdapterFactory<ICGate> gateAdapterFactory = RuntimeTypeAdapterFactory.of(ICGate.class);
        for (Map.Entry<Class<? extends ICGate>, String> entry : getGateSerializationMap().entrySet()) {
            gateAdapterFactory.registerSubtype(entry.getKey(), entry.getValue());
        }

        RuntimeTypeAdapterFactory<ICRegister> registerAdapterFactory = RuntimeTypeAdapterFactory.of(ICRegister.class);
        registerAdapterFactory.registerSubtype(ByteRegister.class);
        registerAdapterFactory.registerSubtype(StaticByteRegister.class);
        for (Map.Entry<Class<? extends ICRegister>, String> entry : getRegisterSerializationMap().entrySet()) {
            registerAdapterFactory.registerSubtype(entry.getKey(), entry.getValue());
        }

        return new GsonBuilder()
                .registerTypeAdapterFactory(tileAdapterFactory)
                .registerTypeAdapterFactory(gateAdapterFactory)
                .registerTypeAdapterFactory(registerAdapterFactory)
                .enableComplexMapKeySerialization()
                .create();
    }

    public String serializeFlatMap(ICFlatMap flatMap) {
        return serializer.toJson(flatMap, ICFlatMap.class);
    }

    public ICFlatMap deserializeFlatMap(String flatMap) {
        try {
            return serializer.fromJson(flatMap, ICFlatMap.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to deserialize ICFlatMap: " + e.getMessage());
            return null;
        }
    }

    public String serializeSimulation(ICSimulation simulation) {
        return serializer.toJson(simulation, ICSimulation.class);
    }

    public ICSimulation deserializeSimulation(String simulation) {
        try {
            return serializer.fromJson(simulation, ICSimulation.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to deserialize ICSimulation: " + e.getMessage());
            return null;
        }
    }

    public Map<Class<? extends FETile>, String> getTileSerializationMap() {
        return Collections.emptyMap();
    }

    public Map<Class<? extends ICGate>, String> getGateSerializationMap() {
        return Collections.emptyMap();
    }

    public Map<Class<? extends ICRegister>, String> getRegisterSerializationMap() {
        return Collections.emptyMap();
    }
}
