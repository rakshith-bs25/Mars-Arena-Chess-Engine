package com.chess.core.board.pieces;

import com.chess.core.board.HexBoard;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDiagonal;
import com.chess.core.coordinate.HexDirection;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

    @Test
void kingMovesOneStepInAllDirections() {
    HexBoard board = new HexBoard();
    HexCoordinate start = HexCoordinate.of(0, 4);
    King king = new King(PlayerColor.WHITE);

    board.setPiece(start, king);
    List<HexCoordinate> moves = king.getValidMoves(board, start);

    assertFalse(moves.isEmpty(), "King should have legal moves");

    // Generate expected neighbors
    List<HexCoordinate> expectedNeighbors = new ArrayList<>();
    for (HexDirection dir : HexDirection.values()) {
        expectedNeighbors.add(start.plus(dir.dq(), dir.dr()));
    }
    for (HexDiagonal diag : HexDiagonal.values()) {
        expectedNeighbors.add(start.plus(diag.dq(), diag.dr()));
    }

    assertEquals(expectedNeighbors.size(), moves.size(), "King should have correct number of moves");
    assertTrue(moves.containsAll(expectedNeighbors), "All valid neighbors must be present");
}


    @Test
    void kingCanCaptureEnemyButNotFriendlyPiece() {
        HexBoard board = new HexBoard();
        HexCoordinate start = HexCoordinate.of(0, 4);
        King king = new King(PlayerColor.WHITE);

        HexCoordinate enemyPos = HexCoordinate.of(1, 4);
        HexCoordinate friendlyPos = HexCoordinate.of(-1, 4);

        board.setPiece(start, king);
        board.setPiece(enemyPos, new Pawn(PlayerColor.BLACK));
        board.setPiece(friendlyPos, new Pawn(PlayerColor.WHITE));

        List<HexCoordinate> moves = king.getValidMoves(board, start);

        assertTrue(moves.contains(enemyPos), "King should be able to capture enemy piece");
        assertFalse(moves.contains(friendlyPos), "King must not capture same-color piece");
    }

    @Test
    void kingCannotMoveOutsideBoardBounds() {
        HexBoard board = new HexBoard();
        HexCoordinate edge = HexCoordinate.of(0, 0);
        King king = new King(PlayerColor.WHITE);

        board.setPiece(edge, king);
        List<HexCoordinate> moves = king.getValidMoves(board, edge);

        for (HexCoordinate move : moves) {
            assertTrue(board.isWithinBounds(move), "King must never generate moves outside board bounds");
        }
    }

    @Test
    void kingDoesNotJumpOverPieces() {
        HexBoard board = new HexBoard();
        HexCoordinate start = HexCoordinate.of(0, 4);
        King king = new King(PlayerColor.WHITE);

        HexCoordinate adjacent = HexCoordinate.of(0, 3);
        HexCoordinate beyond = HexCoordinate.of(0, 2);

        board.setPiece(start, king);
        board.setPiece(adjacent, new Pawn(PlayerColor.BLACK));

        List<HexCoordinate> moves = king.getValidMoves(board, start);

        // King may capture adjacent enemy
        assertTrue(moves.contains(adjacent), "King may capture adjacent enemy");

        // King cannot jump over the enemy piece
        assertFalse(moves.contains(beyond), "King must not move beyond one square");
    }
}
