package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.Collections;
import java.util.List;

public class BasicPiece extends BasePiece {
    public BasicPiece(PlayerColor owner) {
        super(owner, "BASIC");
    }

    @Override
    public List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position) {
        return Collections.emptyList();
    }
}