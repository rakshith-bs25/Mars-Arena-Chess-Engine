package com.chess.core.board;

import com.chess.core.game.PlayerColor;
import java.util.List;

public interface Board {
    Piece getPiece(String coordinateId);

    List<Piece> getPieces(PlayerColor playerColor);
}