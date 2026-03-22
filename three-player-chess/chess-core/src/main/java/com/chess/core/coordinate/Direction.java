package com.chess.core.coordinate;

public enum Direction {

    EAST(1, 0),
    NORTHEAST(1, -1),
    NORTHWEST(0, -1),
    WEST(-1, 0),
    SOUTHWEST(-1, 1),
    SOUTHEAST(0, 1);

    public final int dq;
    public final int dr;

    Direction(int dq, int dr) {
        this.dq = dq;
        this.dr = dr;
    }
}
