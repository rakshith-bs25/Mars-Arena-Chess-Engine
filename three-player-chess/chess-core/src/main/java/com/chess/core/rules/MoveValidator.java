package com.chess.core.rules;

import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import java.util.List;

public class MoveValidator {

    public static boolean isMoveLegal(HexBoard board, HexCoordinate from, HexCoordinate to, PlayerColor player) {
        // 1. Check if piece exists and belongs to player
        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getOwner() != player)
            return false;

        // 2. Check geometry
        if (!piece.getValidMoves(board, from).contains(to)) {
            return false;
        }
        HexBoard simulationBoard = new HexBoard(board);
        simulationBoard.movePiece(from, to);

        if (BoardAnalyzer.isKingInCheck(simulationBoard, player)) {
            return false;
        }

        return true;
    }

    public static boolean isCheckMate(HexBoard board, PlayerColor player) {
        // 1. If King is NOT in check, it cannot be checkmate
        if (!BoardAnalyzer.isKingInCheck(board, player)) {
            return false;
        }

        // 2. If King IS in check, but they have a valid move, it's not checkmate
        return !hasAnyValidMoves(board, player);
    }

    public static boolean isStalemate(HexBoard board, PlayerColor player) {
        // 1. If King IS in check, it's not stalemate (it might be checkmate)
        if (BoardAnalyzer.isKingInCheck(board, player)) {
            return false;
        }

        // 2. If they have no legal moves left, it is Stalemate
        return !hasAnyValidMoves(board, player);
    }

    public static boolean hasAnyValidMoves(HexBoard board, PlayerColor player) {
        java.util.Map<HexCoordinate, Piece> allPieces = board.getAllPieces();

        for (java.util.Map.Entry<HexCoordinate, Piece> entry : allPieces.entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getOwner() != player)
                continue;

            HexCoordinate start = entry.getKey();

            List<HexCoordinate> candidates = piece.getValidMoves(board, start);

            for (HexCoordinate dest : candidates) {
                if (isMoveLegal(board, start, dest, player)) {
                    return true;
                }
            }
        }
        return false;
    }

}