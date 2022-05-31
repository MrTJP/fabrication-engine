package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.api.PropagationFunction;
import mrtjp.fengine.tiles.FETile;
import mrtjp.fengine.tiles.FETileMap;

import java.util.*;
import java.util.stream.Collectors;

public class TileMapPathFinder { //TODO implement Stepper

    private final Queue<PathFinderNode> open = new LinkedList<>();
    private final HashSet<PathFinderNode> openSet = new HashSet<>();
    private final HashSet<PathFinderNode> closedSet = new HashSet<>();

    private final HashMap<Integer, HashSet<Integer>> portToOutputRegisters = new HashMap<>();
    private final HashMap<Integer, HashSet<Integer>> portToInputRegisters = new HashMap<>();

    private final PathFinderManifest manifest;
    private final FETileMap map;

    public TileMapPathFinder(PathFinderManifest manifest, FETileMap map, TileCoord start, PropagationFunction startPropFunc) {
        this.manifest = manifest;
        this.map = map;
        openInitial(start, startPropFunc);
    }

    private void openInitial(TileCoord coord, PropagationFunction propFunc) {

        traverse(propFunc, (s, p) -> {
            TileCoord nextPos = coord.offset(s);
            int inputDir = TileCoord.oppositeDir(s);
            PathFinderNode opened = new PathFinderNode(nextPos, inputDir, p);
            if (!openSet.contains(opened) && !closedSet.contains(opened)) {
                open.add(opened);
                openSet.add(opened);
            }
        });
    }

    private void openNext(PathFinderNode prev) {

        FETile tile = map.getTile(prev.pos).orElse(null);
        if (tile == null) return;

        collect(prev, tile);

        PropagationFunction propFunc = tile.propagationFunc(prev.inputDir, prev.inputPort);
        traverse(propFunc, (s, p) -> {
            TileCoord nextPos = prev.pos.offset(s);
            int inputDir = TileCoord.oppositeDir(s);
            PathFinderNode opened = prev.moveTo(nextPos, inputDir, p);
            if (!openSet.contains(opened) && !closedSet.contains(opened)) {
                open.add(opened);
                openSet.add(opened);
            }
        });
    }

    private void collect(PathFinderNode prev, FETile tile) {

        int initialPort = prev.getRootNode().inputPort;
        int dir = prev.inputDir;
        int port = prev.inputPort;

        tile.getOutputRegister(dir, port).ifPresent(outputRegID -> {
            portToInputRegisters.computeIfAbsent(initialPort, i -> new HashSet<>()).add(outputRegID);
            manifest.addInputPropagationTrail(outputRegID, prev);
        });

        tile.getInputRegister(dir, port).ifPresent(inputRegID -> {
            portToOutputRegisters.computeIfAbsent(initialPort, i -> new HashSet<>()).add(inputRegID);
            manifest.addOutputPropagationTrail(inputRegID, prev);
        });
    }

    private void traverse(PropagationFunction propFunc, TraverseFunc traverseFunc) {
        for (int s = 0; s < 6; s++) {
            for (int p = 0; p < 16; p++) {
                if (propFunc.canPropagate(s, p)) traverseFunc.traverse(s, p);
            }
        }
    }

    public void step() {
        if (open.isEmpty()) return;

        // Get next node on queue
        PathFinderNode next = open.poll();
        openSet.remove(next);

        // Operate on it and queue additional nodes
        openNext(next);

        // Close this node
        closedSet.add(next);
    }

    public boolean isFinished() {
        return open.isEmpty();
    }

    public PathFinderResult result() {

        List<Integer> outputs = portToOutputRegisters.values().stream()
                .flatMap(Set::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        List<Integer> inputs = portToInputRegisters.values().stream()
                .flatMap(Set::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new PathFinderResult(outputs, inputs);
    }

    private interface TraverseFunc {

        void traverse(int dir, int port);
    }
}
