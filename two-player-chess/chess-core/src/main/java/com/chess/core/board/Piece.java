package com.chess.core.board;

import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import java.util.List;

public interface Piece {
    PlayerColor getOwner();
    String getType();
    List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position);
}