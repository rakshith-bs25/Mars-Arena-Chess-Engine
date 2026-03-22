package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends BasePiece {
    private final int direction; // 1 for White (Up), -1 for Black (Down)

    public Pawn(PlayerColor owner) {
        super(owner, "PAWN");
        this.direction = (owner == PlayerColor.WHITE) ? 1 : -1;
    }

    @Override
    public List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position) {
        List<SquareCoordinate> moves = new ArrayList<>();
        int x = position.x();
        int y = position.y();

        // 1. Move Forward 1
        SquareCoordinate oneStep = SquareCoordinate.of(x, y + direction);
        if (board.isWithinBounds(oneStep) && board.getPieceAt(oneStep) == null) {
            moves.add(oneStep);

            // 2. Move Forward 2 (if not moved and path clear)
            if (!hasMoved()) {
                SquareCoordinate twoStep = SquareCoordinate.of(x, y + (direction * 2));
                if (board.isWithinBounds(twoStep) && board.getPieceAt(twoStep) == null) {
                    moves.add(twoStep);
                }
            }
        }

        // 3. Capture Diagonals
        int[][] captures = {{1, direction}, {-1, direction}};
        for (int[] cap : captures) {
            SquareCoordinate target = SquareCoordinate.of(x + cap[0], y + cap[1]);
            if (board.isWithinBounds(target)) {
                Piece targetPiece = board.getPieceAt(target);
                if (targetPiece != null && targetPiece.getOwner() != this.getOwner()) {
                    moves.add(target);
                }
            }
        }

        return moves;
    }
}