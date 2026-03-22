package com.chess.core.board.pieces;

import com.chess.core.board.HexBoard;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    @Test
    void rookSlidesInAllSixDirectionsOnEmptyBoard() {
        HexBoard board = new HexBoard();
        Rook rook = new Rook(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);

        board.setPiece(start, rook);

        List<HexCoordinate> moves = rook.getValidMoves(board, start);

        // One-step in each axial direction
        assertTrue(moves.contains(HexCoordinate.of(0, -1))); // NORTH
        assertTrue(moves.contains(HexCoordinate.of(1, -1))); // NE
        assertTrue(moves.contains(HexCoordinate.of(1, 0)));  // SE
        assertTrue(moves.contains(HexCoordinate.of(0, 1)));  // SOUTH
        assertTrue(moves.contains(HexCoordinate.of(-1, 1))); // SW
        assertTrue(moves.contains(HexCoordinate.of(-1, 0))); // NW
    }

    @Test
    void rookCanSlideMultipleSquaresUntilEdge() {
        HexBoard board = new HexBoard();
        Rook rook = new Rook(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);

        board.setPiece(start, rook);

        List<HexCoordinate> moves = rook.getValidMoves(board, start);

        assertTrue(moves.contains(HexCoordinate.of(0, -2)));
        assertTrue(moves.contains(HexCoordinate.of(0, -3)));
    }

    @Test
    void rookCanCaptureEnemyButCannotMovePastIt() {
        HexBoard board = new HexBoard();
        Rook rook = new Rook(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate enemyPos = HexCoordinate.of(0, -2);

        board.setPiece(start, rook);
        board.setPiece(enemyPos, new Pawn(PlayerColor.BLACK));

        List<HexCoordinate> moves = rook.getValidMoves(board, start);

        assertTrue(moves.contains(enemyPos),
                "Rook should be able to capture enemy");

        assertFalse(moves.contains(HexCoordinate.of(0, -3)),
                "Rook must not move past captured piece");
    }

    @Test
    void rookIsBlockedByFriendlyPiece() {
        HexBoard board = new HexBoard();
        Rook rook = new Rook(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate friendlyPos = HexCoordinate.of(1, 0);

        board.setPiece(start, rook);
        board.setPiece(friendlyPos, new Pawn(PlayerColor.WHITE));

        List<HexCoordinate> moves = rook.getValidMoves(board, start);

        assertFalse(moves.contains(friendlyPos),
                "Rook must not capture friendly piece");

        assertFalse(moves.contains(HexCoordinate.of(2, 0)),
                "Rook must not move past friendly piece");
    }

    @Test
    void rookCannotMoveOutsideBoard() {
        HexBoard board = new HexBoard();
        Rook rook = new Rook(PlayerColor.WHITE);

        // Place rook near edge
        HexCoordinate edge = HexCoordinate.of(0, -5);
        board.setPiece(edge, rook);

        List<HexCoordinate> moves = rook.getValidMoves(board, edge);

        for (HexCoordinate move : moves) {
            assertTrue(board.isWithinBounds(move),
                    "Rook move must stay within board");
        }
    }
}
