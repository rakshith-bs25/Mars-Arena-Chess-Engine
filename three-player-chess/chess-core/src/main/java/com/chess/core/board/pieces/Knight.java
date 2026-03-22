package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Knight extends BasePiece {

    // 12 distinct jump vectors in axial coordinates (q, r)
    private static final int[][] JUMPS = {
        // Around the ring clockwise
        {  1, -3 },
        {  2, -3 },
        {  3, -2 },
        {  3, -1 },
        {  2,  1 },
        {  1,  2 },
        { -1,  3 },
        { -2,  3 },
        { -3,  2 },
        { -3,  1 },
        { -2, -1 },
        { -1, -2 }
    };

    public Knight(PlayerColor owner) {
        super(owner, PieceType.KNIGHT);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        List<HexCoordinate> moves = new ArrayList<>();
        HexBoard hexBoard = (HexBoard) board;

        for (int[] v : JUMPS) {
            HexCoordinate target = start.plus(v[0], v[1]);

            // 1) Stay on the 3-player board
            if (!hexBoard.isWithinBounds(target)) continue;

            // 2) Can't land on own piece
            Piece piece = hexBoard.getPieceAt(target);
            if (piece == null || piece.getOwner() != this.getOwner()) {
                moves.add(target);
            }
        }

        return moves;
    }
}
