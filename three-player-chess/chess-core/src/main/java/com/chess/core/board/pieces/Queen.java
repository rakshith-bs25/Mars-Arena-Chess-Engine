package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.HexBoard;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDiagonal;
import com.chess.core.coordinate.HexDirection;
import com.chess.core.game.PlayerColor;
import com.chess.core.rules.HexSlidingEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Queen extends BasePiece {

    public Queen(PlayerColor owner) {
       super(owner, PieceType.QUEEN);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        if (board instanceof HexBoard) {
            List<HexCoordinate> moves = new ArrayList<>();
            HexBoard hexBoard = (HexBoard) board;

            // 1. Get Straight Moves (like a Rook)
            moves.addAll(HexSlidingEngine.calculateSlidingMoves(
                hexBoard, start, HexDirection.values(), getOwner()
            ));

            // 2. Get Diagonal Moves (like a Bishop)
            moves.addAll(HexSlidingEngine.calculateSlidingMoves(
                hexBoard, start, HexDiagonal.values(), getOwner()
            ));

            return moves;
        }
        return Collections.emptyList();
    }
}