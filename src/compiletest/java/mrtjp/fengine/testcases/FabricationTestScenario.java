package mrtjp.fengine.testcases;

import mrtjp.fengine.api.ICFlatMap;
import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FEBasicTileMap;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class FabricationTestScenario {

    public String description;
    public Map<String, String> params = new LinkedHashMap<>();

    public FEBasicTileMap rootMap;
    public ICFlatMap rootFlatMap;

    public Set<ICGate> expectedGates = new HashSet<>();
    public Set<ICRegister> expectedRegisters = new HashSet<>();

    public Map<Integer, ICRegister> staticRegisters = new LinkedHashMap<>();

    public Map<ICGate, Set<ICRegister>> gateWrites = new LinkedHashMap<>();
    public Map<ICGate, Set<ICRegister>> gateReads = new LinkedHashMap<>();

    //@formatter:off
    public void setDescription(String description) { this.description = description; }
    public void addParam(String key, String value) { params.put(key, value); }

    public void setRootMap(FEBasicTileMap rootMap) { this.rootMap = rootMap; }

    public void addGate(ICGate gate) {
        expectedGates.add(gate);
        gateReads.computeIfAbsent(gate, k -> new HashSet<>());
        gateWrites.computeIfAbsent(gate, k -> new HashSet<>());
    }
    public void addRegister(ICRegister register) { expectedRegisters.add(register); }
    public void addStaticRegister(int id, ICRegister register) { staticRegisters.put(id, register); expectedRegisters.add(register); }

    public void gateWritesToRegister(ICGate gate, ICRegister register) { gateWrites.get(gate).add(register); }
    public void gateReadsFromRegister(ICGate gate, ICRegister register) { gateReads.get(gate).add(register); }
    //@formatter:on

    public abstract void init();
}
