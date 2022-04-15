package mrtjp.fengine.api;

import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FETileMap;

import java.util.List;
import java.util.Map;

public interface ICAssembler {

    int allocRegisterID();
    int allocRegisterID(int id);

    int allocGateID();
    int allocGateID(int id);

    int getRemappedRegisterID(int id);

    void addRemap(int oldID, int newID);

    void addRegister(int id, ICRegister r);
    void addGate(int id, ICGate gate, List<Integer> drivingRegs, List<Integer> drivenRegs);

    void addTileMap(FETileMap map, Map<Integer, Integer> remaps);
    void addFlatMap(ICFlatMap flatMap, Map<Integer, Integer> remaps);

    int getMapIndex();

    ICFlatMap result();
}
