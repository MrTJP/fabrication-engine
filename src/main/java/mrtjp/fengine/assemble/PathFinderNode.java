package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PathFinderNode {

    public final TileCoord pos;
    public final int inputDir;
    public final int inputPort;

    private final List<PathFinderNode> trail = new LinkedList<>();

    public PathFinderNode(TileCoord pos, int inputDir, int inputPort) {
        this(pos, inputDir, inputPort, Collections.emptyList());
    }

    private PathFinderNode(TileCoord pos, int inputDir, int inputPort, List<PathFinderNode> trail) {
        this.pos = pos;
        this.inputDir = inputDir;
        this.inputPort = inputPort;
        this.trail.addAll(trail);
        this.trail.add(this);
    }

    public PathFinderNode getRootNode() {
        return trail.get(0);
    }

    public PathFinderNode moveTo(TileCoord pos, int inputDir, int inputPort) {
        return new PathFinderNode(pos, inputDir, inputPort, trail);
    }

    public void forEachPropagation(PropagationConsumer propConsumer) {

        for (int i = 0; i < trail.size() - 1; i++) { // Stop 1 short. Need a next node to calculate outgoing dir/port

            PathFinderNode node = trail.get(i);
            PathFinderNode nextNode = trail.get(i + 1);

            int inDir = node.inputDir;
            int outDir = TileCoord.oppositeDir(nextNode.inputDir);

            int inPort = node.inputPort;
            int outPort = nextNode.inputPort;

            propConsumer.accept(node.pos, inDir, inPort, outDir, outPort);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PathFinderNode) {
            PathFinderNode that = (PathFinderNode) obj;
            return that.pos.equals(pos) &&
                    that.inputDir == inputDir &&
                    that.inputPort == inputPort;
        }
        return false;
    }

    // No collisions for:
    // 25 bits pos.hashCode()
    // 3 bits dir
    // 4 bits port
    @Override
    public int hashCode() {
        int result = pos.hashCode();
        result = (result << 3) + inputDir;
        result = (result << 4) + inputPort;
        return result;
    }

    @Override
    public String toString() {
        return String.format("PathFinderNode(pos:%s, inDir:%d, inPort:%d", pos.toString(), inputDir, inputPort);
    }

    public interface PropagationConsumer {

        void accept(TileCoord pos, int inputDir, int inputPort, int outputDir, int outputPort);
    }
}
