package com.chess.core.board;

import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import java.util.HashMap;
import java.util.Map;

public class SquareBoard implements Board {
    private final Map<SquareCoordinate, Piece> pieceMap;

    public SquareBoard() {
        this.pieceMap = new HashMap<>();
    }

    // Copy constructor for simulation
    public SquareBoard(SquareBoard other) {
        this.pieceMap = new HashMap<>(other.pieceMap);
    }

    @Override
    public Piece getPieceAt(SquareCoordinate coord) {
        return pieceMap.get(coord);
    }

    @Override
    public void setPiece(SquareCoordinate coord, Piece piece) {
        if (piece == null) pieceMap.remove(coord);
        else pieceMap.put(coord, piece);
    }

    @Override
    public void movePiece(SquareCoordinate from, SquareCoordinate to) {
        Piece p = pieceMap.remove(from);
        pieceMap.put(to, p);
    }

    @Override
    public boolean isWithinBounds(SquareCoordinate coord) {
        return coord.x() >= 0 && coord.x() < 8 && coord.y() >= 0 && coord.y() < 8;
    }
    
    public Map<SquareCoordinate, Piece> getAllPieces() {
        return pieceMap;
    }
}