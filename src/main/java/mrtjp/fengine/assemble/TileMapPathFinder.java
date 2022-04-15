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

    private final FETileMap map;

    public TileMapPathFinder(FETileMap map, TileCoord start, PropagationFunction startPropFunc) {
        this.map = map;
        openInitial(start, startPropFunc);
    }

    private void openInitial(TileCoord coord, PropagationFunction propFunc) {
        openNext(coord, propFunc, PathFinderNode::new);
    }

    private void openFrom(PathFinderNode prev, TileCoord coord, PropagationFunction propFunc) {
        openNext(coord, propFunc, prev::moveTo);
    }

    private void openNext(TileCoord coord, PropagationFunction propFunc, PathFinderNodeFactory nodeFactory) {
        for (int s = 0; s < 6; s++) {
            for (int p = 0; p < 16; p++) {
                if (propFunc.canPropagate(s, p)) {
                    PathFinderNode move = nodeFactory.newNode(coord, s, p);
                    if (!openSet.contains(move) && !closedSet.contains(move)) {
                        open.add(move);
                        openSet.add(move);
                    }
                }
            }
        }
    }

    private void collect(PathFinderNode root, FETile tile, TileCoord coord, int dir, int port) {
        final int initialPort = root.port; // Initial port at start of pathfinding which lead to discovery of this new tile

        tile.getOutputRegister(dir, port).ifPresent(outputRegID -> {
            portToInputRegisters.computeIfAbsent(initialPort, i -> new HashSet<>());
            portToInputRegisters.get(initialPort).add(outputRegID);
        });

        tile.getInputRegister(dir, port).ifPresent(inputRegID -> {
            portToOutputRegisters.computeIfAbsent(initialPort, i -> new HashSet<>());
            portToOutputRegisters.get(initialPort).add(inputRegID);
        });
    }

    public void step() {
        if (open.isEmpty()) return;

        PathFinderNode next = open.poll();
        openSet.remove(next);

        TileCoord newPos = next.pos.offset(next.dir);
        map.getTile(newPos).ifPresent(tile -> {
            int fromDir = TileCoord.oppositeDir(next.dir);
            PropagationFunction pfunc = tile.propagationFunc(fromDir, next.port);
            collect(next.getRootNode(), tile, newPos, fromDir, next.port);
            openFrom(next.getRootNode(), newPos, pfunc);

            closedSet.add(next);
        });
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

    private interface PathFinderNodeFactory {

        PathFinderNode newNode(TileCoord coord, int dir, int port);
    }
}
