package com.chess.core.rules;

import com.chess.core.board.SquareBoard;
import com.chess.core.board.pieces.*;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwoPlayerBoardAnalyzerTest {

    private SquareBoard board;

    @BeforeEach
    void setUp() {
        board = new SquareBoard();
        // Board starts empty because SquareBoard constructor only initializes the map, 
        // doesn't place pieces unless StandardGame does it.
        // If  SquareBoard puts default pieces, clear them here:
        // board.getAllPieces().clear(); 
    }

    @Test
    void testKingSafeOnEmptyBoard() {
        // 1. Place White King
        board.setPiece(SquareCoordinate.of(4, 4), new King(PlayerColor.WHITE));

        // 2. Verify Safe
        assertFalse(TwoPlayerBoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE), 
            "King should be safe when alone");
    }

    @Test
    void testRookCheck_DirectLine() {
        // 1. Place White King at e1 (4,0)
        SquareCoordinate kingPos = SquareCoordinate.of(4, 0);
        board.setPiece(kingPos, new King(PlayerColor.WHITE));

        board.setPiece(SquareCoordinate.of(4, 7), new Rook(PlayerColor.BLACK));

        // 3. Verify Check
        assertTrue(TwoPlayerBoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE), 
            "King should be in Check from Rook on open file");
    }

    @Test
    void testRookCheck_BlockedByFriend() {
        // 1. Place White King at e1 (4,0)
        board.setPiece(SquareCoordinate.of(4, 0), new King(PlayerColor.WHITE));

        // 2. Place Black Rook at e8 (4,7)
        board.setPiece(SquareCoordinate.of(4, 7), new Rook(PlayerColor.BLACK));

        // 3. BLOCK the check with White Pawn at e2 (4,1)
        board.setPiece(SquareCoordinate.of(4, 1), new Pawn(PlayerColor.WHITE));

        // 4. Verify SAFE (Analyzer relies on Rook.getValidMoves stopping at the pawn)
        assertFalse(TwoPlayerBoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE), 
            "King should be safe because Pawn blocks the Rook");
    }

    @Test
    void testKnightCheck_JumpsOverPieces() {
        // 1. Place White King at e1 (4,0)
        board.setPiece(SquareCoordinate.of(4, 0), new King(PlayerColor.WHITE));

        // 2. Place blocking pawn at e2 (4,1)
        board.setPiece(SquareCoordinate.of(4, 1), new Pawn(PlayerColor.WHITE));

        // 3. Place Black Knight at f3 (5,2) -> Attacks e1
        board.setPiece(SquareCoordinate.of(5, 2), new Knight(PlayerColor.BLACK));

        // 4. Verify Check (Knights ignore blockers)
        assertTrue(TwoPlayerBoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE), 
            "King should be in Check from Knight even with blockers");
    }

    @Test
    void testFindKingReturnsNullSafety() {
        // 1. Empty board (No King)
        boolean result = TwoPlayerBoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE);
        
        // code returns 'true' if king is missing ("Should not happen")
        assertTrue(result, "Should return true (or handle error) if King is missing");
    }
}