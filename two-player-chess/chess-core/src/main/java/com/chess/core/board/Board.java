package com.chess.core.board;

import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

public interface Board {
    Piece getPieceAt(SquareCoordinate coord);
    void setPiece(SquareCoordinate coord, Piece piece);
    void movePiece(SquareCoordinate from, SquareCoordinate to);
    boolean isWithinBounds(SquareCoordinate coord);
}