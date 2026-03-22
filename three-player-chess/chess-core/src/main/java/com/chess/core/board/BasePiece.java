package com.chess.core.board;

import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import java.util.List;

public abstract class BasePiece implements Piece {
    private final PlayerColor owner;
    private final PieceType type;
    private boolean hasMoved = false;

    protected BasePiece(PlayerColor owner, PieceType type) {
        this.owner = owner;
        this.type = type;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public PlayerColor getOwner() {
        return owner;
    }

    @Override
    public String getType() {
        return type.name();
    }
    
    public PieceType getPieceType() {
        return type;
    }

    // Concrete pieces must implement this
    @Override
    public abstract List<HexCoordinate> getValidMoves(Board board, HexCoordinate position);

    @Override
    public String toString() {
        return owner + "_" + type;
    }
}