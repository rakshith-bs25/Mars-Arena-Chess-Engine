package com.chess.twoplayer.dto;

public class TileDTO {
    private int x;
    private int y;
    private String pieceType;
    private String owner;

    public TileDTO(int x, int y, String pieceType, String owner) {
        this.x = x;
        this.y = y;
        this.pieceType = pieceType;
        this.owner = owner;
    }
    // Getters...
    public int getX() { return x; }
    public int getY() { return y; }
    public String getPieceType() { return pieceType; }
    public String getOwner() { return owner; }
}