package mrtjp.fengine.assemble;

import mrtjp.fengine.api.ICAssembler;
import mrtjp.fengine.api.ICAssemblyTile;
import mrtjp.fengine.api.ICFlatMap;
import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FETileMap;

import java.util.*;
import java.util.stream.Collectors;

public class ICAssemblerImpl implements ICAssembler, ICAssemblyTile.Allocator, ICAssemblyTile.RemapRegistry, ICAssemblyTile.RemapProvider, ICAssemblyTile.Collector {

    private final Map<Integer, ICRegister> registers = new HashMap<>();
    private final Map<Integer, ICGate> gates = new HashMap<>();

    private final Map<Integer, ArrayList<Integer>> regDependents = new HashMap<>();    //[regID -> Seq[gateID]]
    private final Map<Integer, ArrayList<Integer>> regDependencies = new HashMap<>();  //[regID -> Seq[gateID]]
    private final Map<Integer, ArrayList<Integer>> gateDependents = new HashMap<>();   //[gateID -> Seq[regID]]
    private final Map<Integer, ArrayList<Integer>> gateDependencies = new HashMap<>(); //[gateID -> Seq[regID]]

    private final List<FETileMap> exploredTileMaps = new ArrayList<>();
    private final List<ICFlatMap> exploredFlatMaps = new ArrayList<>();

    private final Queue<TileMapRemapPair> openTileMaps = new LinkedList<>();
    private final Queue<FlatMapRemapPair> openFlatMaps = new LinkedList<>();

    private final Map<Integer, Integer> registerIDRemaps = new HashMap<>();

    private int nextRegID = 0;
    private int nextGateID = 0;

    @Override
    public int allocRegisterID() {
        return nextRegID++;
    }

    @Override
    public int allocGateID() {
        return nextGateID++;
    }

    @Override
    public int allocRegisterID(int id) {
        if (id >= nextRegID) nextRegID = id + 1;
        return id;
    }

    @Override
    public int allocGateID(int id) {
        if (id >= nextGateID) nextGateID = id + 1;
        return id;
    }

    @Override
    public int getRemappedRegisterID(int id) {
        return registerIDRemaps.getOrDefault(id, id);
    }

    @Override
    public void addRemap(int oldID, int newID) {
        registerIDRemaps.put(oldID, getRemappedRegisterID(newID));
    }

    @Override
    public void addRegister(int id, ICRegister r) {

        if (registers.containsKey(id)) {
            if (getMapIndex() == 0)
                throw new IllegalArgumentException("Register ID " + id + " already exists");
            else
                return; // Duplicates are allowed in nested maps, but are ignored. They are expected to be remapped.
        }

        allocRegisterID(id);
        registers.put(id, r);
    }

    @Override
    public void addGate(int id, ICGate gate, List<Integer> drivingRegs, List<Integer> drivenRegs) {

        if (gates.containsKey(id)) throw new IllegalArgumentException("Gate ID " + id + " already exists");

        allocGateID(id);
        gates.put(id, gate);

        for (int regID : drivingRegs) {
            regDependents.putIfAbsent(regID, new ArrayList<>());
            regDependents.get(regID).add(id);
        }

        for (int regID : drivenRegs) {
            regDependencies.putIfAbsent(regID, new ArrayList<>());
            regDependencies.get(regID).add(id);
        }

        // TODO Below, id should not exist in either gateDependents nor gateDependencies.
        //      The add to existing existing list logic is unnecessary. Should be replaced
        //      with simply <list>.put(new ArrayList(<registers>));

        gateDependents.putIfAbsent(id, new ArrayList<>());
        gateDependents.get(id).addAll(drivenRegs);

        gateDependencies.putIfAbsent(id, new ArrayList<>());
        gateDependencies.get(id).addAll(drivingRegs);
    }

    @Override
    public void addTileMap(FETileMap map, Map<Integer, Integer> remaps) {
        openTileMaps.add(new TileMapRemapPair(map, remaps));
    }

    @Override
    public void addFlatMap(ICFlatMap flatMap, Map<Integer, Integer> remaps) {
        openFlatMaps.add(new FlatMapRemapPair(flatMap, remaps));
    }

    private void mergeTileMap(FETileMap map, Map<Integer, Integer> remaps) {
        System.out.println("Assembly merge tile map setup");
        registerIDRemaps.clear();
        registerIDRemaps.putAll(remaps);

        // Phase 1: Allocate registers and compute nodes
        System.out.println("Assembly Phase 1: Allocations");
        Collection<FETileMap.TileMapEntry> entries = map.getEntries();
        for (FETileMap.TileMapEntry entry : entries) {
            entry.getTile().allocate(this);
        }

        // Phase 2: Pathfinding and remap assignment
        System.out.println("Assembly Phase 2: Pathfinding and remap declarations");
        for (FETileMap.TileMapEntry entry : entries) {
            System.out.println("Pathfinding at " + entry.getCoord());
            PathFinder pathFinder = new PathFinder(map, entry.getCoord());
            entry.getTile().locate(pathFinder);
            entry.getTile().registerRemaps(this);
        }

        // Phase 3: Remapping
        System.out.println("Assembly Phase 3: Remapping");
        for (FETileMap.TileMapEntry entry : entries) {
            entry.getTile().consumeRemaps(this);
        }

        // Phase 4: Collect
        System.out.println("Assembly Phase 4: Register and Gate collection");
        for (FETileMap.TileMapEntry entry : entries) {
            entry.getTile().collect(this);
        }

        System.out.println("Assembly merge tile map cleanup");
        registerIDRemaps.clear();
    }

    private void mergeFlatMap(ICFlatMap map, Map<Integer, Integer> remaps) {
        Map<Integer, Integer> gateTransforms = new HashMap<>();
        Map<Integer, Integer> regTransforms = new HashMap<>();

        // Allocate new IDs for all registers in this address space besides those with explicit remaps
        for (Map.Entry<Integer, ICRegister> e : map.getRegisters().entrySet()) {
            int oldId = e.getKey();
            int newId = remaps.getOrDefault(oldId, allocRegisterID()); // Use remap if available, else alloc new
            regTransforms.put(oldId, newId);
        }

        // Allocate new IDs for all gates in this address space
        for (Map.Entry<Integer, ICGate> e : map.getGates().entrySet()) {
            int oldId = e.getKey();
            int newId = allocGateID(); // Gates always get assigned a new id (they are never statically assigned)
            gateTransforms.put(oldId, newId);
        }

        // Copy in registers to remapped addresses
        for (Map.Entry<Integer, ICRegister> e : map.getRegisters().entrySet()) {
            int oldId = e.getKey();
            int newId = regTransforms.get(oldId);
            if (!registers.containsKey(newId)) {
                addRegister(newId, e.getValue());
            }
        }

        // Copy in gates and remap their read/write lists
        for (Map.Entry<Integer, ICGate> e : map.getGates().entrySet()) {
            int oldId = e.getKey();
            int newId = gateTransforms.get(oldId);

            List<Integer> newDriving = map.getGateDependencies().get(oldId).stream()
                    .map(regTransforms::get)
                    .collect(Collectors.toList());
            List<Integer> newDriven = map.getGateDependents().get(oldId).stream()
                    .map(regTransforms::get)
                    .collect(Collectors.toList());

            addGate(newId, e.getValue(), newDriving, newDriven);
        }
    }

    @Override
    public ICFlatMap result() {
        while (!openTileMaps.isEmpty()) {
            TileMapRemapPair pair = openTileMaps.poll();
            mergeTileMap(pair.map, pair.remaps);
            exploredTileMaps.add(pair.map);
        }

        while (!openFlatMaps.isEmpty()) {
            FlatMapRemapPair pair = openFlatMaps.poll();
            mergeFlatMap(pair.map, pair.remaps);
            exploredFlatMaps.add(pair.map);
        }

        return new ICFlatMap(registers, gates, regDependents, regDependencies, gateDependents, gateDependencies);
    }

    private int getMapIndex() {
        return exploredFlatMaps.size() + exploredTileMaps.size();
    }

    private static class TileMapRemapPair {

        FETileMap map;
        Map<Integer, Integer> remaps;

        public TileMapRemapPair(FETileMap map, Map<Integer, Integer> remaps) {
            this.map = map;
            this.remaps = remaps;
        }
    }

    private static class FlatMapRemapPair {

        ICFlatMap map;
        Map<Integer, Integer> remaps;

        public FlatMapRemapPair(ICFlatMap map, Map<Integer, Integer> remaps) {
            this.map = map;
            this.remaps = remaps;
        }
    }
}
