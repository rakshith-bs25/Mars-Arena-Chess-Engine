package com.chess.core.rules;

import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.List;
import java.util.Map;

public class BoardAnalyzer {

    public static boolean isKingInCheck(HexBoard board, PlayerColor kingColor) {
        // 1. Find the King
        HexCoordinate kingPos = null;
        for (Map.Entry<HexCoordinate, Piece> entry : board.getAllPieces().entrySet()) {
            if (entry.getValue().getOwner() == kingColor && entry.getValue().getPieceType() == PieceType.KING) {
                kingPos = entry.getKey();
                break;
            }
        }

        if (kingPos == null)
            return true;

        // 2. Check if any enemy piece attacks this square
        return isSquareAttacked(board, kingPos, kingColor);
    }

    public static boolean isSquareAttacked(HexBoard board, HexCoordinate target, PlayerColor defender) {
        for (Map.Entry<HexCoordinate, Piece> entry : board.getAllPieces().entrySet()) {
            Piece attacker = entry.getValue();
            HexCoordinate attackerPos = entry.getKey();

            if (attacker.getOwner() != defender) {
                List<HexCoordinate> attacks = attacker.getValidMoves(board, attackerPos);

                if (attacks.contains(target)) {
                    return true;
                }
            }
        }
        return false;
    }
}