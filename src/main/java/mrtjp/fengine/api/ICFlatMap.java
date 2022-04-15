package mrtjp.fengine.api;

import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FETileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ICFlatMap {

    Map<Integer, ICRegister> getRegisters();

    Map<Integer, ICGate> getGates();

    Map<Integer, ArrayList<Integer>> getRegDependents();    //[regID -> Seq[gateID]]

    Map<Integer, ArrayList<Integer>> getRegDependencies();  //[regID -> Seq[gateID]]

    Map<Integer, ArrayList<Integer>> getGateDependents();   //[gateID -> Seq[regID]]

    Map<Integer, ArrayList<Integer>> getGateDependencies(); //[gateID -> Seq[regID]]

    List<FETileMap> getExploredTileMaps();

    List<ICFlatMap> getExploredFlatMaps();
}
