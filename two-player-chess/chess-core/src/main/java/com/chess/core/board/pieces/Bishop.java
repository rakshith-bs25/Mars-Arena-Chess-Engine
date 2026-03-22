package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends BasePiece {
    public Bishop(PlayerColor owner) {
        super(owner, "BISHOP");
    }

    @Override
    public List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position) {
        List<SquareCoordinate> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // Diagonals

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = position.x() + dx;
            int y = position.y() + dy;

            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                SquareCoordinate target = SquareCoordinate.of(x, y);
                Piece targetPiece = board.getPieceAt(target);

                if (targetPiece == null) {
                    moves.add(target);
                } else {
                    if (targetPiece.getOwner() != this.getOwner()) {
                        moves.add(target);
                    }
                    break;
                }
                x += dx;
                y += dy;
            }
        }
        return moves;
    }
}