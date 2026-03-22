package com.chess.core.rules;

import com.chess.core.board.HexBoard;
import com.chess.core.board.pieces.King;
import com.chess.core.board.pieces.Rook;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoveValidatorTest {

    @Test
    void cannotCaptureEnemyKingDirectly() {
        HexBoard board = new HexBoard();
        
        // 1. Setup: Place a White Rook and a Black King in the same line
        HexCoordinate rookPos = HexCoordinate.of(0, 0);
        HexCoordinate kingPos = HexCoordinate.of(0, 3); // 3 steps away, straight line
        
        board.setPiece(rookPos, new Rook(PlayerColor.WHITE));
        board.setPiece(kingPos, new King(PlayerColor.BLACK));
        
        // 2. Action: Try to move Rook onto the King (Capture)
        boolean isLegal = MoveValidator.isMoveLegal(board, rookPos, kingPos, PlayerColor.WHITE);
        
        // 3. Assert: This should be FALSE. You cannot "eat" the King.
        assertFalse(isLegal, "BUG REPRODUCED: The engine allowed capturing the King!");
    }
}