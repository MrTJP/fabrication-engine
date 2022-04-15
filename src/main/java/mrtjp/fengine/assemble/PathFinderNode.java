package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;

public class PathFinderNode {

    public final TileCoord pos;
    public final int dir;
    public final int port;
    private final PathFinderNode root;

    public PathFinderNode(TileCoord pos, int dir, int port) {
        this.pos = pos;
        this.dir = dir;
        this.port = port;
        this.root = this;
    }

    private PathFinderNode(TileCoord pos, int dir, int port, PathFinderNode root) {
        this.pos = pos;
        this.dir = dir;
        this.port = port;
        this.root = root;
    }

    public PathFinderNode getRootNode() {
        return root;
    }

    public PathFinderNode moveTo(TileCoord pos, int dir, int port) {
        return new PathFinderNode(pos, dir, port, root);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PathFinderNode) {
            PathFinderNode that = (PathFinderNode) obj;
            return that.pos.equals(pos) &&
                    that.dir == dir &&
                    that.port == port;
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
        result = (result << 3) + dir;
        result = (result << 4) + port;
        return result;
    }

    @Override
    public String toString() {
        return String.format("PathFinderNode(pos:%s, dir:%d, port:%d", pos.toString(), dir, port);
    }
}
