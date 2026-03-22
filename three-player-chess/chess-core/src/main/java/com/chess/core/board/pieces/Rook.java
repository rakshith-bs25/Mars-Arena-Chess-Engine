package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDirection;
import com.chess.core.game.PlayerColor;
import com.chess.core.rules.HexSlidingEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rook extends BasePiece {

    public Rook(PlayerColor owner) {
        super(owner, PieceType.ROOK);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        if (board instanceof HexBoard) {
            return HexSlidingEngine.calculateSlidingMoves(
                    (HexBoard) board, // Cast to HexBoard
                    start,
                    HexDirection.values(), // Rooks use Directions
                    this.getOwner());
        }
        return Collections.emptyList();
    }
}
