package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Knight extends BasePiece {
    public Knight(PlayerColor owner) {
        super(owner, "KNIGHT");
    }

    @Override
    public List<SquareCoordinate> getValidMoves(Board board, SquareCoordinate position) {
        List<SquareCoordinate> moves = new ArrayList<>();
        int[][] jumps = {
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };

        for (int[] jump : jumps) {
            SquareCoordinate target = SquareCoordinate.of(position.x() + jump[0], position.y() + jump[1]);
            if (isValidTarget(board, target)) {
                moves.add(target);
            }
        }
        return moves;
    }
}