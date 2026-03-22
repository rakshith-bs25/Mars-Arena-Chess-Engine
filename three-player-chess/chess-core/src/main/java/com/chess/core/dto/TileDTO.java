package com.chess.core.dto;

import com.chess.core.game.PlayerColor;

public class TileDTO {
    private String coordinate;
    private String pieceType;
    private PlayerColor owner;

    public TileDTO(String coordinate, String pieceType, PlayerColor owner) {
        this.coordinate = coordinate;
        this.pieceType = pieceType;
        this.owner = owner;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getPieceType() {
        return pieceType;
    }

    public PlayerColor getOwner() {
        return owner;
    }
}