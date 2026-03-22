package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Queen extends BasePiece {
    // Composition over Inheritance: Queen behaves like Rook + Bishop
    private final Rook rookDelegate;
    private final Bishop bishopDelegate;

    public Queen(PlayerColor owner) {
        super(owner, "QUEEN");
        this.rookDelegate = new Rook(owner);
        this.bishopDelegate = new Bishop(owner);
    }

    @Override
    public List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position) {
        List<SquareCoordinate> moves = new ArrayList<>();
        moves.addAll(rookDelegate.getValidMoves(board, position));
        moves.addAll(bishopDelegate.getValidMoves(board, position));
        return moves;
    }
}