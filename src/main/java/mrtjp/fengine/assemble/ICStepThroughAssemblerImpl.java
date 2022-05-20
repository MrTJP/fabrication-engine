package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.api.ICAssemblyTile;
import mrtjp.fengine.api.ICFlatMap;
import mrtjp.fengine.api.ICStepThroughAssembler;
import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FETileMap;

import java.util.*;
import java.util.stream.Collectors;

import static mrtjp.fengine.api.ICStepThroughAssembler.AssemblerStepType.*;

public class ICStepThroughAssemblerImpl implements ICStepThroughAssembler {

    private final StepTree<AssemblerStepType, AssemblerStepResult> tree = new StepTree<>(new StepTreeEventReceiver());

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

    private ICStepThroughAssembler.EventReceiver eventReceiver = null;

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

        tree.addStep(MERGE_TILE_MAP_PRE, () -> {
            System.out.println("Assembly merge tile map setup");
            registerIDRemaps.clear();
            registerIDRemaps.putAll(remaps);
            return new AssemblerStepResult(); // TODO: Return something useful
        });

        Collection<FETileMap.TileMapEntry> entries = map.getEntries();

        // Phase 1: Allocate registers and compute nodes
        tree.addStep(MERGE_TILE_MAP_PHASE1, () -> {
            System.out.println("Assembly Phase 1: Allocations");
            for (FETileMap.TileMapEntry entry : entries) {
                tree.addStep(PHASE1_ALLOC, () -> {
                    CachedAllocator allocator = new CachedAllocator();
                    entry.getTile().allocate(allocator);
                    return new AssemblerStepResult(entry.getCoord(), allocator.registerIds, allocator.gateIds);
                });
            }
            return new AssemblerStepResult(); //TODO: Return something useful
        });

        // Phase 2: Pathfinding and remap assignment
        tree.addStep(MERGE_TILE_MAP_PHASE2, () -> {
            System.out.println("Assembly Phase 2: Pathfinding and remap declarations");
            for (FETileMap.TileMapEntry entry : entries) {
                tree.addStep(PHASE2_PATHFIND, () -> {
                    System.out.println("Pathfinding at " + entry.getCoord());
                    PathFinder pathFinder = new PathFinder(map, entry.getCoord());
                    entry.getTile().locate(pathFinder);

                    return new AssemblerStepResult(entry.getCoord()); //TODO: add pathfinding result
                });

                tree.addStep(PHASE2_REGISTER_REMAPS, () -> {
                    Map<Integer, Integer> registeredRemaps = new HashMap<>();
                    entry.getTile().registerRemaps((a, b) -> {
                        registeredRemaps.put(a, b);
                        this.addRemap(a, b);
                    });
                    return new AssemblerStepResult(entry.getCoord(), registeredRemaps); // TODO: Add located registers to result
                });
            }

            return new AssemblerStepResult(); //TODO: Return something useful
        });

        // Phase 3: Remapping
        tree.addStep(MERGE_TILE_MAP_PHASE3, () -> {
            System.out.println("Assembly Phase 3: Remapping");
            for (FETileMap.TileMapEntry entry : entries) {
                tree.addStep(PHASE3_CONSUME_REMAPS, () -> {
                    Map<Integer, Integer> consumedRemaps = new HashMap<>();
                    entry.getTile().consumeRemaps(a -> {
                        int b = ICStepThroughAssemblerImpl.this.getRemappedRegisterID(a);
                        consumedRemaps.put(a, b);
                        return b;
                    });
                    return new AssemblerStepResult(entry.getCoord(), consumedRemaps); // TODO: Add remaps to result
                });
            }
            return new AssemblerStepResult(); //TODO: Return something useful
        });

        // Phase 4: Collect
        tree.addStep(MERGE_TILE_MAP_PHASE4, () -> {
            System.out.println("Assembly Phase 4: Register and Gate collection");
            for (FETileMap.TileMapEntry entry : entries) {

                tree.addStep(PHASE4_COLLECT, () -> {
                    CachedCollector collector = new CachedCollector();
                    entry.getTile().collect(collector);
                    return new AssemblerStepResult(entry.getCoord(), collector.registerIds, collector.gateIds); // TODO: Return something useful
                });
            }

            return new AssemblerStepResult(); //TODO: Return something useful
        });

        tree.addStep(MERGE_TILE_MAP_POST, () -> {
            System.out.println("Assembly merge tile map cleanup");
            registerIDRemaps.clear();
            exploredTileMaps.add(map);
            return new AssemblerStepResult(); //TODO: Return something useful
        });
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

        exploredFlatMaps.add(map);
    }

    private AssemblerStepResult checkOpenTileMapTask() {
        // Merge next tile map
        if (!openTileMaps.isEmpty()) {
            TileMapRemapPair pair = openTileMaps.poll();
            tree.addStep(MERGE_TILE_MAP, () -> {
                mergeTileMap(pair.map, pair.remaps);
                return new AssemblerStepResult(); // TODO: Return something useful
            });

            // Re-queue this task to merge additional tile maps
            tree.addStep(CHECK_OPEN_TILE_MAPS, this::checkOpenTileMapTask);
        } else {
            // No more tile maps to merge, move to flat maps
            tree.addStep(CHECK_OPEN_FLAT_MAPS, this::checkOpenFlatMapTask);
        }

        return new AssemblerStepResult(); // TODO: Return something useful
    }

    private AssemblerStepResult checkOpenFlatMapTask() {
        // Merge next flat map
        if (!openFlatMaps.isEmpty()) {
            FlatMapRemapPair pair = openFlatMaps.poll();
            tree.addStep(MERGE_FLAT_MAP, () -> {
                mergeFlatMap(pair.map, pair.remaps);
                return new AssemblerStepResult(); // TODO: Return something useful
            });

            // Re-queue this task if more flat maps remain
            tree.addStep(CHECK_OPEN_FLAT_MAPS, this::checkOpenFlatMapTask);
        }

        return new AssemblerStepResult(); // TODO: Return something useful
    }

    @Override
    public ICFlatMap result() {
        tree.addStep(CHECK_OPEN_TILE_MAPS, this::checkOpenTileMapTask);

        while (tree.stepsRemaining() > 0) tree.stepOver();

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

    private class StepTreeEventReceiver implements StepTree.StepTreeEventReceiver<AssemblerStepType, ICStepThroughAssemblerImpl.AssemblerStepResult> {

        @Override
        public void onStepExecuted(List<Integer> treePath, AssemblerStepType stepType, AssemblerStepResult stepResult) {
            stepResult.setStepType(stepType);
            stepResult.setTreePath(treePath);
            if (eventReceiver != null) eventReceiver.onStepExecuted(stepResult);
        }

        @Override
        public void onStepAdded(List<Integer> treePath, AssemblerStepType stepType) {
            if (eventReceiver != null) eventReceiver.onStepAdded(new AssemblerStepDescriptor(stepType, treePath));
        }
    }

    private class CachedAllocator implements ICAssemblyTile.Allocator {

        public final List<Integer> registerIds = new LinkedList<>();
        public final List<Integer> gateIds = new LinkedList<>();

        @Override
        public int allocRegisterID() {
            int i = ICStepThroughAssemblerImpl.this.allocRegisterID();
            registerIds.add(i);
            return i;
        }

        @Override
        public int allocRegisterID(int id) {
            int i = ICStepThroughAssemblerImpl.this.allocRegisterID(id);
            registerIds.add(i);
            return i;
        }

        @Override
        public int allocGateID() {
            int i = ICStepThroughAssemblerImpl.this.allocGateID();
            gateIds.add(i);
            return i;
        }

        @Override
        public int allocGateID(int id) {
            int i = ICStepThroughAssemblerImpl.this.allocGateID(id);
            gateIds.add(i);
            return i;
        }
    }

    private class CachedCollector implements ICAssemblyTile.Collector {

        public final List<Integer> registerIds = new LinkedList<>();
        public final List<Integer> gateIds = new LinkedList<>();

        public int addedTileMapCount = 0;
        public int addedFlatMapCount = 0;

        @Override
        public void addRegister(int id, ICRegister r) {
            ICStepThroughAssemblerImpl.this.addRegister(id, r);
            registerIds.add(id);
        }

        @Override
        public void addGate(int id, ICGate gate, List<Integer> drivingRegs, List<Integer> drivenRegs) {
            ICStepThroughAssemblerImpl.this.addGate(id, gate, drivingRegs, drivenRegs);
            gateIds.add(id);
        }

        @Override
        public void addTileMap(FETileMap map, Map<Integer, Integer> remaps) {
            ICStepThroughAssemblerImpl.this.addTileMap(map, remaps);
            addedTileMapCount++;
        }

        @Override
        public void addFlatMap(ICFlatMap flatMap, Map<Integer, Integer> remaps) {
            ICStepThroughAssemblerImpl.this.addFlatMap(flatMap, remaps);
            addedFlatMapCount++;
        }
    }

    private static class AssemblerStepDescriptor implements ICStepThroughAssembler.AssemblerStepDescriptor {

        private final AssemblerStepType step;
        private final List<Integer> treePath;

        public AssemblerStepDescriptor(AssemblerStepType step, List<Integer> treePath) {
            this.step = step;
            this.treePath = treePath;
        }

        //@formatter:off
        @Override public AssemblerStepType getStepType() { return step; }
        @Override public List<Integer> getTreePath() { return treePath; }
        //@formatter:on
    }

    private static class AssemblerStepResult implements ICStepThroughAssembler.AssemblerStepResult {

        private AssemblerStepType step;
        private List<Integer> treePath;

        private final List<TileCoord> tileCoords;
        private final List<Integer> registerIds;
        private final List<Integer> gateIds;
        private final Map<Integer, Integer> registerRemaps;

        public AssemblerStepResult(List<TileCoord> tileCoords, List<Integer> registerIds, List<Integer> gateIds, Map<Integer, Integer> registerRemaps) {
            this.tileCoords = tileCoords;
            this.registerIds = registerIds;
            this.gateIds = gateIds;
            this.registerRemaps = registerRemaps;
        }

        public AssemblerStepResult() {
            this(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        }

        public AssemblerStepResult(TileCoord tileCoord) {
            this(Collections.singletonList(tileCoord), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        }

        public AssemblerStepResult(TileCoord tileCoord, List<Integer> registerIds, List<Integer> gateIds) {
            this(Collections.singletonList(tileCoord), registerIds, gateIds, Collections.emptyMap());
        }

        public AssemblerStepResult(TileCoord tileCoord, Map<Integer, Integer> registerRemaps) {
            this(Collections.singletonList(tileCoord), Collections.emptyList(), Collections.emptyList(), registerRemaps);
        }

        //@formatter:off
        public void setStepType(AssemblerStepType step) { this.step = step; }
        public void setTreePath(List<Integer> treePath) { this.treePath = treePath; }
        @Override public AssemblerStepType getStepType() { return step; }
        @Override public List<Integer> getTreePath() { return treePath; }
        @Override public List<TileCoord> getTileCoords() { return tileCoords; }
        @Override public List<Integer> getRegisterIds() { return registerIds; }
        @Override public List<Integer> getGateIds() { return gateIds; }
        @Override public Map<Integer, Integer> getRemappedRegisterIds() { return registerRemaps; }
        //@formatter:on
    }

    //@formatter:off
    @Override public void setEventReceiver(EventReceiver receiver) { eventReceiver = receiver; }
    @Override public void stepOver() { tree.stepOver(); }
    @Override public void stepIn() { tree.stepIn(); }
    @Override public void stepOut() { tree.stepOut(); }
    @Override public int stepsRemaining() { return tree.stepsRemaining(); }
    //@formatter:on
}
