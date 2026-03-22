package com.chess.core.coordinate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HexCoordinateTest {

    @Test
    void testEqualityAndHashCode() {
        HexCoordinate a = HexCoordinate.of(1, -2);
        HexCoordinate b = HexCoordinate.of(1, -2);
        HexCoordinate c = HexCoordinate.of(0, 0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void testCubeConstraint() {
        HexCoordinate coord = HexCoordinate.of(2, -5);
        assertEquals(0, coord.q() + coord.r() + coord.s());
    }

    @Test
    void testDirectionAddition() {
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate neighbor = start.plus(Direction.NORTHWEST.dq, Direction.NORTHWEST.dr);

        assertEquals(0, neighbor.q());
        assertEquals(-1, neighbor.r());
        assertEquals(0, start.q());
        assertEquals(0, start.r());
    }

    @Test
    void testDistanceToCenter() {
        assertEquals(0, HexCoordinate.of(0, 0).distanceToCenter());
        assertEquals(1, HexCoordinate.of(1, -1).distanceToCenter());
        assertEquals(2, HexCoordinate.of(2, -2).distanceToCenter());
        assertEquals(3, HexCoordinate.of(0, 3).distanceToCenter());
    }

    @Test
    void testAngleSanity() {
        HexCoordinate east = HexCoordinate.of(2, 0);
        HexCoordinate west = HexCoordinate.of(-2, 0);

        double eastAngle = east.getAngle();
        double westAngle = west.getAngle();

        // use tolerance since conversion is slightly slanted
        double tolerance = 0.6;
        assertTrue(eastAngle > -Math.PI / 2 && eastAngle < Math.PI / 2, "East ≈ 0");
        assertTrue(Math.abs(Math.abs(westAngle) - Math.PI) < tolerance, "West ≈ ±PI");
    }
}
