package com.chess.core.coordinate;

import java.util.Objects;

public class SquareCoordinate {
    private final int x;
    private final int y;

    private SquareCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static SquareCoordinate of(int x, int y) {
        return new SquareCoordinate(x, y);
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SquareCoordinate that = (SquareCoordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}