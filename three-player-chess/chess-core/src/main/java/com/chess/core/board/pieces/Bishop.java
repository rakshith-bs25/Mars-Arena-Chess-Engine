package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.HexBoard;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDiagonal;
import com.chess.core.game.PlayerColor;
import com.chess.core.rules.HexSlidingEngine;

import java.util.Collections;
import java.util.List;

public class Bishop extends BasePiece {
    public Bishop(PlayerColor owner) {
        super(owner, PieceType.BISHOP);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        if (board instanceof HexBoard) {
            return HexSlidingEngine.calculateSlidingMoves(
                (HexBoard) board, 
                start, 
                HexDiagonal.values(), // Bishops use Diagonals
                this.getOwner()
            );
        }
        return Collections.emptyList();
    }
}
