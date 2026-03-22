package com.chess.core.coordinate;

public enum HexDirection implements DirectionOffset {
    // Vectors for Axial Coordinates
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    SOUTH_EAST(1, 0),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    NORTH_WEST(-1, 0);

    private final int dq;
    private final int dr;

    HexDirection(int dq, int dr) {
        this.dq = dq;
        this.dr = dr;
    }

    public int dq() {
        return dq;
    }

    public int dr() {
        return dr;
    }
}