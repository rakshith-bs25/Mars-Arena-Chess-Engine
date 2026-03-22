package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class King extends BasePiece {
    public King(PlayerColor owner) {
        super(owner, "KING");
    }

    @Override
    public List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position) {
        List<SquareCoordinate> moves = new ArrayList<>();
        int[][] directions = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0}, 
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] dir : directions) {
            SquareCoordinate target = SquareCoordinate.of(position.x() + dir[0], position.y() + dir[1]);
            if (isValidTarget(board, target)) {
                moves.add(target);
            }
        }

        // Castling Logic (Geometric only - safety checks handled by Validator/Engine)
        if (!hasMoved()) {
            // Kingside (Right)
            checkCastling(board, position, 1, 7, moves);
            // Queenside (Left)
            checkCastling(board, position, -1, 0, moves);
        }

        return moves;
    }

    private void checkCastling(Board board, SquareCoordinate kingPos, int direction, int rookCol, List<SquareCoordinate> moves) {
        int y = kingPos.y();
        int x = kingPos.x();
        
        // Check Rook
        Piece rook = board.getPieceAt(SquareCoordinate.of(rookCol, y));
        if (rook == null || !rook.getType().equals("ROOK") || ((BasePiece)rook).hasMoved()) {
            return;
        }

        // Check Path Clear
        int step = (direction == 1) ? 1 : -1;
        int checkX = x + step;
        while (checkX != rookCol) {
            if (board.getPieceAt(SquareCoordinate.of(checkX, y)) != null) return;
            checkX += step;
        }

        // Add King's destination (2 steps)
        moves.add(SquareCoordinate.of(x + (step * 2), y));
    }
}