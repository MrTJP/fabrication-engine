package mrtjp.fengine.api;

import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FETileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ICFlatMap {

    private final Map<Integer, ICRegister> registers;
    private final Map<Integer, ICGate> gates;
    private final Map<Integer, ArrayList<Integer>> registerDependents;
    private final Map<Integer, ArrayList<Integer>> registerDependencies;
    private final Map<Integer, ArrayList<Integer>> gateDependents;
    private final Map<Integer, ArrayList<Integer>> gateDependencies;

    public ICFlatMap(Map<Integer, ICRegister> registers, Map<Integer, ICGate> gates, Map<Integer, ArrayList<Integer>> registerDependents, Map<Integer, ArrayList<Integer>> registerDependencies, Map<Integer, ArrayList<Integer>> gateDependents, Map<Integer, ArrayList<Integer>> gateDependencies) {
        this.registers = registers;
        this.gates = gates;
        this.registerDependents = registerDependents;
        this.registerDependencies = registerDependencies;
        this.gateDependents = gateDependents;
        this.gateDependencies = gateDependencies;
    }

    public Map<Integer, ICRegister> getRegisters() { return registers; }
    public Map<Integer, ICGate> getGates() { return gates; }
    public Map<Integer, ArrayList<Integer>> getRegDependents() { return registerDependents; }    //[regID -> Seq[gateID]]
    public Map<Integer, ArrayList<Integer>> getRegDependencies() { return registerDependencies; }  //[regID -> Seq[gateID]]
    public Map<Integer, ArrayList<Integer>> getGateDependents() { return gateDependents; }   //[gateID -> Seq[regID]]
    public Map<Integer, ArrayList<Integer>> getGateDependencies() { return gateDependencies; } //[gateID -> Seq[regID]]
}
