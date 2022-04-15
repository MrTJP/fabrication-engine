package mrtjp.fengine;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Testable
public class TileCoordTest {

    @Test
    public void testHashCollisions() {

        HashSet<Integer> hashSet = new HashSet<>();
        int i = 0;
        for (int x = 0; x < 256; x++)
            for (int y = 0; y < 256; y++)
                for (int z = 0; z < 32; z++) {
                    TileCoord coord = new TileCoord(x, y, z);
                    int hash = coord.hashCode();

                    assertFalse(hashSet.contains(hash));
                    hashSet.add(hash);
                    i++;
                }

        System.out.printf("Verified hash uniqueness for %d TileCoord objects%n", i);
    }
}
