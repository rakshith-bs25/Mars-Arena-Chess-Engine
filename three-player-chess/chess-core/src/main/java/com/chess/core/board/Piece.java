package com.chess.core.board;

import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import java.util.List;

public interface Piece {
    PlayerColor getOwner();

    String getType();

    PieceType getPieceType();

    List<HexCoordinate> getValidMoves(Board board, HexCoordinate position);
}