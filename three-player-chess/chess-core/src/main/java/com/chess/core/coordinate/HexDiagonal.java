package com.chess.core.coordinate;

public enum HexDiagonal implements DirectionOffset{
    D1(1, -2),
    D2(2, -1),
    D3(1, 1),
    D4(-1, 2),
    D5(-2, 1),
    D6(-1, -1);

    private final int dq;
    private final int dr;

    HexDiagonal(int dq, int dr) {
        this.dq = dq;
        this.dr = dr;
    }

    public int dq() { return dq; }
    public int dr() { return dr; }
}