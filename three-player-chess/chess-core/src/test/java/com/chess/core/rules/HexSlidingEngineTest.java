package com.chess.core.rules;

import com.chess.core.board.HexBoard;
import com.chess.core.board.pieces.Rook;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDirection;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HexSlidingEngineTest {

    private HexBoard board;

    @BeforeEach
    void setup() {
        board = new HexBoard();
    }

    @Test
    void testSliding_EmptyBoard() {
        // Arrange
        HexCoordinate start = HexCoordinate.of(0, 0);
        
        // Act: Ask for moves North
        List<HexCoordinate> moves = HexSlidingEngine.calculateSlidingMoves(
                board,
                start,
                new HexDirection[]{HexDirection.NORTH},
                PlayerColor.WHITE
        );

        // Assert: On an empty board starting at 0,0, moving North goes to (0, -1), (0, -2), etc.
        assertTrue(moves.size() > 0, "Should find moves on empty board");
        assertTrue(moves.contains(HexCoordinate.of(0, -1)));
    }

    @Test
    void testSliding_BlockedByFriend() {
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate friendPos = HexCoordinate.of(0, -1); // Square directly North

        // Place a friendly Rook directly in the way
        board.setPiece(friendPos, new Rook(PlayerColor.WHITE)); 

        // Act
        List<HexCoordinate> moves = HexSlidingEngine.calculateSlidingMoves(
                board,
                start,
                new HexDirection[]{HexDirection.NORTH},
                PlayerColor.WHITE
        );

        // Assert: Should be blocked immediately (0 moves)
        assertEquals(0, moves.size());
    }

    @Test
    void testSliding_CaptureEnemy() {
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate enemyPos = HexCoordinate.of(0, -1); 

        // Place an ENEMY Rook directly in the way
        board.setPiece(enemyPos, new Rook(PlayerColor.BLACK)); 

        // Act
        List<HexCoordinate> moves = HexSlidingEngine.calculateSlidingMoves(
                board,
                start,
                new HexDirection[]{HexDirection.NORTH},
                PlayerColor.WHITE
        );

        // Assert: Should include the capture, then stop
        assertTrue(moves.contains(enemyPos));
        assertEquals(1, moves.size()); // Only 1 move allowed (the capture)
    }
}