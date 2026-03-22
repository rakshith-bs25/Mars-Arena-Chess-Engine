package com.chess.core.rules;

import com.chess.core.board.Piece;
import com.chess.core.board.SquareBoard;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.List;
import java.util.Map;

public class TwoPlayerBoardAnalyzer {

    public static boolean isKingInCheck(SquareBoard board, PlayerColor kingColor) {
        SquareCoordinate kingPos = findKing(board, kingColor);
        if (kingPos == null) return true; // Should not happen
        return isSquareAttacked(board, kingPos, kingColor);
    }

    public static boolean isSquareAttacked(SquareBoard board, SquareCoordinate target, PlayerColor defenderColor) {
        for (Map.Entry<SquareCoordinate, Piece> entry : board.getAllPieces().entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getOwner() != defenderColor) {
                // Get geometric moves (raw potential moves)
                List<SquareCoordinate> attacks = piece.getValidMoves(board, entry.getKey());
                if (attacks.contains(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static SquareCoordinate findKing(SquareBoard board, PlayerColor color) {
        for (Map.Entry<SquareCoordinate, Piece> entry : board.getAllPieces().entrySet()) {
            if (entry.getValue().getType().equals("KING") && entry.getValue().getOwner() == color) {
                return entry.getKey();
            }
        }
        return null;
    }
}