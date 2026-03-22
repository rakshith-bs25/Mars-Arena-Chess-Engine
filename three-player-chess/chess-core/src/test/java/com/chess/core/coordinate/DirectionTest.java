package com.chess.core.coordinate;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void allDirectionsHaveValidAxialVectors() {
        for (Direction dir : Direction.values()) {
            int dq = dir.dq;
            int dr = dir.dr;
            int ds = -dq - dr;

            assertEquals(0, dq + dr + ds,
                    "Direction " + dir + " violates cube coordinate constraint");
        }
    }

    @Test
    void allDirectionsAreUnique() {
        Set<String> vectors = new HashSet<>();

        for (Direction dir : Direction.values()) {
            String key = dir.dq + "," + dir.dr;
            assertTrue(vectors.add(key),
                    "Duplicate direction vector found: " + key);
        }
    }

    @Test
    void oppositeDirectionsCancelEachOther() {
        assertEquals(0, Direction.EAST.dq + Direction.WEST.dq);
        assertEquals(0, Direction.EAST.dr + Direction.WEST.dr);

        assertEquals(0, Direction.NORTHEAST.dq + Direction.SOUTHWEST.dq);
        assertEquals(0, Direction.NORTHEAST.dr + Direction.SOUTHWEST.dr);

        assertEquals(0, Direction.NORTHWEST.dq + Direction.SOUTHEAST.dq);
        assertEquals(0, Direction.NORTHWEST.dr + Direction.SOUTHEAST.dr);
    }
}
