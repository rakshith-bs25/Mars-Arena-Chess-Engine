package com.chess.core.rules;

import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.board.pieces.King;
import com.chess.core.board.pieces.Queen;
import com.chess.core.board.pieces.Pawn;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardAnalyzerTest {

    private HexBoard board;

    @BeforeEach
    void setup() {
        board = new HexBoard();
    }

    @Test
    void kingNotInCheckOnEmptyBoard() {
        // Place a White King somewhere
        HexCoordinate kingPos = HexCoordinate.of(0, 0);
        board.setPiece(kingPos, new King(PlayerColor.WHITE));

        boolean inCheck = BoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE);
        assertFalse(inCheck, "King should not be in check if no enemies threaten it");
    }

    @Test
    void kingInCheckByDirectLinePiece() {
        HexCoordinate kingPos = HexCoordinate.of(0, 0);
        board.setPiece(kingPos, new King(PlayerColor.WHITE));

        // Place an enemy Queen that can attack the king
        HexCoordinate attackerPos = HexCoordinate.of(0, 3);
        board.setPiece(attackerPos, new Queen(PlayerColor.RED));

        boolean inCheck = BoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE);
        assertTrue(inCheck, "King should be in check by enemy Queen in line");
    }

    @Test
    void kingNotInCheckWhenBlockedByOwnPiece() {
        HexCoordinate kingPos = HexCoordinate.of(0, 0);
        board.setPiece(kingPos, new King(PlayerColor.WHITE));

        // Enemy Queen further away
        HexCoordinate attackerPos = HexCoordinate.of(0, 3);
        board.setPiece(attackerPos, new Queen(PlayerColor.RED));

        // Block the attack with a friendly pawn
        HexCoordinate blockerPos = HexCoordinate.of(0, 1);
        board.setPiece(blockerPos, new Pawn(PlayerColor.WHITE));

        boolean inCheck = BoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE);
        // Depending on  Queen movement logic, this may or may not block. 
        // Assuming getValidMoves correctly stops at blockers:
        assertFalse(inCheck, "King should not be in check if a friendly piece blocks the attack");
    }

    @Test
    void squareAttackedReturnsTrueForThreatenedSquare() {
        HexCoordinate target = HexCoordinate.of(2, 2);
        board.setPiece(HexCoordinate.of(0, 0), new Queen(PlayerColor.RED));

        boolean attacked = BoardAnalyzer.isSquareAttacked(board, target, PlayerColor.WHITE);
        assertTrue(attacked, "Square should be attacked by enemy Queen");
    }

    @Test
    void squareAttackedReturnsFalseForSafeSquare() {
        HexCoordinate target = HexCoordinate.of(2, 2);
        board.setPiece(HexCoordinate.of(0, 0), new Queen(PlayerColor.WHITE));

        boolean attacked = BoardAnalyzer.isSquareAttacked(board, target, PlayerColor.WHITE);
        assertFalse(attacked, "Square should not be attacked by friendly piece");
    }

    @Test
    void missingKingCountsAsInCheck() {
        // No king placed
        boolean inCheck = BoardAnalyzer.isKingInCheck(board, PlayerColor.BLACK);
        assertTrue(inCheck, "Missing king should be treated as effectively defeated (in check)");
    }
}
