package com.chess.core.rules;

import com.chess.core.board.SquareBoard;
import com.chess.core.board.pieces.*;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwoPlayerMoveValidatorTest {

    private SquareBoard board;

    @BeforeEach
    void setUp() {
        board = new SquareBoard();
        // The Validator needs a King to verify "Check" conditions.
        // We place it far away at (7,7) so it doesn't interfere with basic tests.
        board.setPiece(SquareCoordinate.of(7, 7), new King(PlayerColor.WHITE));
    }

    @Test
    void testBasicLegalMove() {
        // Setup: White Pawn at e2 (4,1)
        SquareCoordinate from = SquareCoordinate.of(4, 1);
        SquareCoordinate to = SquareCoordinate.of(4, 2);
        board.setPiece(from, new Pawn(PlayerColor.WHITE));

        assertTrue(TwoPlayerMoveValidator.isMoveLegal(board, from, to, PlayerColor.WHITE),
            "Pawn moving forward 1 step should be legal");
    }

    @Test
    void testIllegalGeometricMove() {
        // Setup: White Rook at a1 (0,0) trying to move diagonally
        SquareCoordinate from = SquareCoordinate.of(0, 0);
        SquareCoordinate to = SquareCoordinate.of(1, 1);
        board.setPiece(from, new Rook(PlayerColor.WHITE));
        
        // (Note: We rely on the King at (7,7) for the check validation)

        assertFalse(TwoPlayerMoveValidator.isMoveLegal(board, from, to, PlayerColor.WHITE),
            "Rook cannot move diagonally");
    }

    @Test
    void testCannotMoveIntoCheck_KingSuicide() {
        // White King at e1 (4,0), Black Rook at e8 (4,7)
        SquareCoordinate kingPos = SquareCoordinate.of(4, 0);
        
        // Overwrite the default King at (7,7) with our test King at (4,0)
        board.setPiece(SquareCoordinate.of(7, 7), null); 
        board.setPiece(kingPos, new King(PlayerColor.WHITE));
        
        board.setPiece(SquareCoordinate.of(4, 7), new Rook(PlayerColor.BLACK));

        // Attempt: Move King to e2 (4,1) -> DEATH (Still on e-file)
        SquareCoordinate suicideMove = SquareCoordinate.of(4, 1);
        
        assertFalse(TwoPlayerMoveValidator.isMoveLegal(board, kingPos, suicideMove, PlayerColor.WHITE),
            "King cannot move to e2 because the file is controlled by the Rook");
    }

    @Test
    void testPinnedPieceCannotMove() {
        // --- THE PIN SCENARIO ---
        
        // Remove default King
        board.setPiece(SquareCoordinate.of(7, 7), null);

        // White King at e1 (4,0)
        // White Rook at e2 (4,1) -> PINNED!
        // Black Queen at e8 (4,7) -> Attacking down the e-file
        
        SquareCoordinate kingPos = SquareCoordinate.of(4, 0);
        SquareCoordinate pinnedRookPos = SquareCoordinate.of(4, 1);
        
        board.setPiece(kingPos, new King(PlayerColor.WHITE));
        board.setPiece(pinnedRookPos, new Rook(PlayerColor.WHITE));
        board.setPiece(SquareCoordinate.of(4, 7), new Queen(PlayerColor.BLACK));

        // 1. Try to move the Rook to the side (d2)
        SquareCoordinate sideMove = SquareCoordinate.of(3, 1);
        
        assertFalse(TwoPlayerMoveValidator.isMoveLegal(board, pinnedRookPos, sideMove, PlayerColor.WHITE),
            "Pinned Rook should NOT be allowed to move sideways and expose King");

        // 2. Try to move the Rook forward (e3)
        SquareCoordinate forwardMove = SquareCoordinate.of(4, 2);
        assertTrue(TwoPlayerMoveValidator.isMoveLegal(board, pinnedRookPos, forwardMove, PlayerColor.WHITE),
            "Pinned Rook CAN move along the line of attack");
    }

    @Test
    void testHasAnyLegalMoves_Stalemate() {
        // Remove default King
        board.setPiece(SquareCoordinate.of(7, 7), null);

        board.setPiece(SquareCoordinate.of(7, 7), new King(PlayerColor.WHITE));
        board.setPiece(SquareCoordinate.of(6, 5), new Queen(PlayerColor.BLACK));
        board.setPiece(SquareCoordinate.of(5, 6), new King(PlayerColor.BLACK));
        assertFalse(TwoPlayerBoardAnalyzer.isKingInCheck(board, PlayerColor.WHITE));
        boolean hasMoves = TwoPlayerMoveValidator.hasAnyLegalMoves(board, PlayerColor.WHITE);
        
        assertFalse(hasMoves, "White should have NO legal moves (Stalemate)");
    }
}