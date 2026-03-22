package com.chess.core.rules;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Piece;
import com.chess.core.board.SquareBoard;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;

public class TwoPlayerMoveValidator {

    public static boolean isMoveLegal(SquareBoard board, SquareCoordinate from, SquareCoordinate to, PlayerColor player) {
        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getOwner() != player) return false;

        // 1. Geometric Check
        if (!piece.getValidMoves(board, from).contains(to)) {
            return false;
        }

        // 2. Simulation (Prevent Self-Check)
        SquareBoard simBoard = new SquareBoard(board);
        simBoard.movePiece(from, to);
        
        if (TwoPlayerBoardAnalyzer.isKingInCheck(simBoard, player)) {
            return false;
        }

        return true;
    }

    // Helper to check if a player has ANY valid moves (for Checkmate/Stalemate detection)
    public static boolean hasAnyLegalMoves(SquareBoard board, PlayerColor player) {
        for (var entry : board.getAllPieces().entrySet()) {
            if (entry.getValue().getOwner() == player) {
                SquareCoordinate from = entry.getKey();
                for (SquareCoordinate to : entry.getValue().getValidMoves(board, from)) {
                    if (isMoveLegal(board, from, to, player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}