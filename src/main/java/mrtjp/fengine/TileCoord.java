package mrtjp.fengine;

import java.util.Arrays;
import java.util.stream.IntStream;

public class TileCoord {

    public final int x;
    public final int y;
    public final int z;

    public TileCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TileCoord) {
            TileCoord that = (TileCoord) obj;
            return x == that.x && y == that.y && z == that.z;
        }
        return false;
    }

    // No collisions for:
    // 8 bits x
    // 8 bits y
    // 5 bits z
    @Override
    public int hashCode() {
        int result = x;
        result = (result << 8) + y;
        result = (result << 5) + z;
        return result;
    }

    public TileCoord copy() {
        return new TileCoord(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("TileCoord(x:%d, y:%d, z:%d)", x, y, z);
    }

    //@formatter:off
    public TileCoord add(int dx, int dy, int dz) { return new TileCoord(x+dx, y+dy, z+dz); }
    public TileCoord subtract(int dx, int dy, int dz) { return new TileCoord(x-dx, y-dy, z-dz); }
    public TileCoord multiply(int i, int j, int k) { return new TileCoord(x*i, y*j, z*k); }
    public TileCoord divide(int i, int j, int k) { return new TileCoord(x/i, y/j, z/k); }

    public TileCoord add(TileCoord that) { return new TileCoord(x+that.x, y+that.y, z+that.z); }
    public TileCoord subtract(TileCoord that) { return new TileCoord(x-that.x, y-that.y, z-that.z); }
    public TileCoord multiply(TileCoord that) { return new TileCoord(x*that.x, y*that.y, z*that.z); }
    public TileCoord divide(TileCoord that) { return new TileCoord(x/that.x, y/that.y, z/that.z); }

    public TileCoord add(int d) { return new TileCoord(x+d, y+d, z+d); }
    public TileCoord subtract(int d) { return new TileCoord(x-d, y-d, z-d); }
    public TileCoord multiply(int d) { return new TileCoord(x*d, y*d, z*d); }
    public TileCoord divide(int d) { return new TileCoord(x/d, y/d, z/d); }

    public TileCoord negate() { return new TileCoord(-x, -y, -z); }

    public TileCoord max(TileCoord that) { return new TileCoord(Math.max(x, that.x), Math.max(y, that.y), Math.max(z, that.z)); }
    public TileCoord min(TileCoord that) { return new TileCoord(Math.min(x, that.x), Math.min(y, that.y), Math.min(z, that.z)); }

    public TileCoord offset(int dir) { return this.add(TileCoord.dirOffsets[dir]); }
    public TileCoord offset(int dir, int amount) { return this.add(TileCoord.dirOffsets[dir].multiply(amount)); }

    public static final TileCoord infinite = new TileCoord(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final TileCoord origin = new TileCoord(0, 0, 0);

    public static final int dirDown  = 0;
    public static final int dirUp    = 1;
    public static final int dirNorth = 2;
    public static final int dirSouth = 3;
    public static final int dirWest  = 4;
    public static final int dirEast  = 5;

    public static final int bitDown  = 0x01;
    public static final int bitUp    = 0x02;
    public static final int bitNorth = 0x04;
    public static final int bitSouth = 0x08;
    public static final int bitWest  = 0x10;
    public static final int bitEast  = 0x20;

    public static int oppositeDir(int dir) { return dir^1; }

    public static final int[] allDirs = { dirDown, dirUp, dirNorth, dirSouth, dirWest, dirEast };
    public static final int dirMaskAll = bitDown | bitUp | bitNorth | bitSouth | bitWest | bitEast;
    public static int[] maskToDirs(int mask) { return Arrays.stream(allDirs).filter(d -> (mask&1<<d) != 0).toArray(); }
    public static int dirsToMask(int[] dirs) { return Arrays.stream(dirs).reduce(0, (mask, d) -> mask|1<<d); }

    public static final int[] allPorts = IntStream.range(0, 16).toArray();
    public static final int portMaskAll = 0;
    public static int[] maskToPorts(int mask) { return Arrays.stream(allPorts).filter(d -> (mask&1<<d) != 0).toArray(); }
    public static int portsToMask(int[] ports) { return Arrays.stream(ports).reduce(0, (mask, d) -> mask|1<<d); }

    public static final TileCoord[] dirOffsets = {
            new TileCoord( 0,-1, 0),
            new TileCoord( 0, 1, 0),
            new TileCoord( 0, 0,-1),
            new TileCoord( 0, 0, 1),
            new TileCoord(-1, 0, 0),
            new TileCoord( 1, 0, 0),
    };
    //@formatter:on
}
