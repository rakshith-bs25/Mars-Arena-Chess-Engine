package com.chess.core.board;

import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import java.util.List;

public abstract class BasePiece implements Piece {
    private final PlayerColor owner;
    private final String type;
    private boolean hasMoved;

    public BasePiece(PlayerColor owner, String type) {
        this.owner = owner;
        this.type = type;
        this.hasMoved = false;
    }

    @Override
    public PlayerColor getOwner() {
        return owner;
    }

    @Override
    public String getType() {
        return type;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    // Standard helper to check if a target is valid (empty or enemy)
    protected boolean isValidTarget(Board board, SquareCoordinate target) {
        if (!board.isWithinBounds(target)) return false;
        Piece p = board.getPieceAt(target);
        return p == null || p.getOwner() != this.owner;
    }
}