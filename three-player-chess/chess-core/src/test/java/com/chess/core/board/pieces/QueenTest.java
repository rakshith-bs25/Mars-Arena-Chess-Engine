package com.chess.core.board.pieces;

import com.chess.core.board.HexBoard;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

    @Test
    void queenHasRookLikeMovesOnEmptyBoard() {
        HexBoard board = new HexBoard();
        Queen queen = new Queen(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);

        board.setPiece(start, queen);

        List<HexCoordinate> moves = queen.getValidMoves(board, start);

        // One step in straight directions
        assertTrue(moves.contains(HexCoordinate.of(0, -1))); // NORTH
        assertTrue(moves.contains(HexCoordinate.of(1, -1))); // NE
        assertTrue(moves.contains(HexCoordinate.of(1, 0)));  // SE
        assertTrue(moves.contains(HexCoordinate.of(0, 1)));  // SOUTH
        assertTrue(moves.contains(HexCoordinate.of(-1, 1))); // SW
        assertTrue(moves.contains(HexCoordinate.of(-1, 0))); // NW
    }

    @Test
    void queenHasBishopLikeMovesOnEmptyBoard() {
        HexBoard board = new HexBoard();
        Queen queen = new Queen(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);

        board.setPiece(start, queen);

        List<HexCoordinate> moves = queen.getValidMoves(board, start);

        // Diagonal-style movement (at least one deep square)
        assertTrue(moves.contains(HexCoordinate.of(2, -1)));
        assertTrue(moves.contains(HexCoordinate.of(-2, 1)));
    }

    @Test
    void queenCanCaptureEnemyButCannotMovePastIt() {
        HexBoard board = new HexBoard();
        Queen queen = new Queen(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate enemyPos = HexCoordinate.of(0, -2);

        board.setPiece(start, queen);
        board.setPiece(enemyPos, new Pawn(PlayerColor.BLACK));

        List<HexCoordinate> moves = queen.getValidMoves(board, start);

        assertTrue(moves.contains(enemyPos),
                "Queen should be able to capture enemy");

        assertFalse(moves.contains(HexCoordinate.of(0, -3)),
                "Queen must not move past captured piece");
    }

    @Test
    void queenIsBlockedByFriendlyPiece() {
        HexBoard board = new HexBoard();
        Queen queen = new Queen(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);
        HexCoordinate friendlyPos = HexCoordinate.of(1, 0);

        board.setPiece(start, queen);
        board.setPiece(friendlyPos, new Pawn(PlayerColor.WHITE));

        List<HexCoordinate> moves = queen.getValidMoves(board, start);

        assertFalse(moves.contains(friendlyPos),
                "Queen must not capture friendly piece");

        assertFalse(moves.contains(HexCoordinate.of(2, 0)),
                "Queen must be blocked by friendly piece");
    }

    @Test
    void queenMovementIsCombinationOfRookAndBishop() {
        HexBoard board = new HexBoard();
        Queen queen = new Queen(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0);

        board.setPiece(start, queen);

        List<HexCoordinate> queenMoves = queen.getValidMoves(board, start);

        Rook rook = new Rook(PlayerColor.WHITE);
        Bishop bishop = new Bishop(PlayerColor.WHITE);

        List<HexCoordinate> rookMoves = rook.getValidMoves(board, start);
        List<HexCoordinate> bishopMoves = bishop.getValidMoves(board, start);

        assertTrue(queenMoves.containsAll(rookMoves),
                "Queen must include all rook moves");

        assertTrue(queenMoves.containsAll(bishopMoves),
                "Queen must include all bishop moves");
    }
}