package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDirection;
import com.chess.core.coordinate.HexDiagonal;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class King extends BasePiece {
    public King(PlayerColor owner) {
        super(owner, PieceType.KING);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        List<HexCoordinate> moves = new ArrayList<>();
        HexBoard hexBoard = (HexBoard) board;

        // 6 orthogonal (rook-style) neighbors
        for (HexDirection dir : HexDirection.values()) {
            HexCoordinate target = start.plus(dir.dq(), dir.dr());
            addIfLegal(hexBoard, target, moves);
        }

        // 6 diagonal (bishop-style) neighbors
        for (HexDiagonal diag : HexDiagonal.values()) {
            HexCoordinate target = start.plus(diag.dq(), diag.dr());
            addIfLegal(hexBoard, target, moves);
        }

        return moves;
    }

    private void addIfLegal(HexBoard board, HexCoordinate target, List<HexCoordinate> moves) {
        if (!board.isWithinBounds(target)) return;

        Piece occupant = board.getPieceAt(target);
        if (occupant == null || occupant.getOwner() != this.getOwner()) {
            moves.add(target);
        }
    }
}
