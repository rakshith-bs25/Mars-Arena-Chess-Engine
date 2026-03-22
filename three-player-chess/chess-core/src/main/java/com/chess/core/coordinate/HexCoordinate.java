package com.chess.core.coordinate;

import java.util.Objects;

public final class HexCoordinate {
    private final int q;
    private final int r;

    public HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    // Factory method for cleaner creation
    public static HexCoordinate of(int q, int r) {
        return new HexCoordinate(q, r);
    }

    public int q() {
        return q;
    }

    public int r() {
        return r;
    }

    /**
     * Converts Axial (q, r) to Cube (q, r, s) coordinate 's' component.
     * Constraint: q + r + s = 0
     */
    public int s() {
        return -q - r;
    }

    // Calculates neighbors based on a direction vector.
    public HexCoordinate plus(int dq, int dr) {
        return new HexCoordinate(this.q + dq, this.r + dr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HexCoordinate that = (HexCoordinate) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    public int distanceToCenter() {
        return (Math.abs(q) + Math.abs(q + r) + Math.abs(r)) / 2;
    }

    public double getAngle() {
        double x = 1.5 * q;
        double y = (Math.sqrt(3) / 2.0 * q) + (Math.sqrt(3) * r);
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return "Hex(" + q + ", " + r + ")";
    }
}