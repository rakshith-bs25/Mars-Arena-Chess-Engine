package com.chess.core.game;

import com.chess.core.board.SquareBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StandardGameTest {

    @Test
    void testFoolsMate_CheckmateDetection() {
        // 1. Setup
        StandardGame game = new StandardGame();
        
        // Ensure game starts correctly
        assertEquals("IN_PROGRESS", game.getStatus());
        assertEquals(PlayerColor.WHITE, game.getCurrentPlayer());

        // --- THE MOVES (Fool's Mate) ---

        // 1. White: Pawn f2 -> f3
        // (x=5, y=1) to (x=5, y=2)
        boolean move1 = game.makeMove(5, 1, 5, 2);
        assertTrue(move1, "White move f2-f3 should be valid");
        assertEquals(PlayerColor.BLACK, game.getCurrentPlayer(), "Turn should switch to Black");

        // 2. Black: Pawn e7 -> e5
        // (x=4, y=6) to (x=4, y=4)
        boolean move2 = game.makeMove(4, 6, 4, 4);
        assertTrue(move2, "Black move e7-e5 should be valid");

        // 3. White: Pawn g2 -> g4
        // (x=6, y=1) to (x=6, y=3)
        boolean move3 = game.makeMove(6, 1, 6, 3);
        assertTrue(move3, "White move g2-g4 should be valid");

        // 4. Black: Queen d8 -> h4 (CHECKMATE!)
        // (x=3, y=7) to (x=7, y=3)
        boolean move4 = game.makeMove(3, 7, 7, 3);
        assertTrue(move4, "Black Queen d8-h4 should be valid");

        // --- VERIFICATION ---
        
        // 1. The status must change to CHECKMATE
        assertEquals("CHECKMATE", game.getStatus(), "Game should declare CHECKMATE");

        // 2. The Current Player should be WHITE (The loser who cannot move)
        assertEquals(PlayerColor.WHITE, game.getCurrentPlayer());
        
        System.out.println("Fool's Mate Test Passed! Engine correctly detects Checkmate.");
    }

    @Test
    void testSimpleMoveAndTurnSwitch() {
        StandardGame game = new StandardGame();
        
        // White Pawn e2 -> e4
        assertTrue(game.makeMove(4, 1, 4, 3));
        
        // Should be Black's turn
        assertEquals(PlayerColor.BLACK, game.getCurrentPlayer());
        assertEquals("IN_PROGRESS", game.getStatus());
    }
}