package com.chess.core.board.pieces;

import com.chess.core.board.HexBoard;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void knightCanJumpIgnoringInterveningPieces() {
        HexBoard board = new HexBoard();
        HexCoordinate start = HexCoordinate.of(3, 3);  // central position
        Knight knight = new Knight(PlayerColor.WHITE);

        // Place blocking pieces around the knight
        board.setPiece(start, knight);
        board.setPiece(HexCoordinate.of(3, 2), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(4, 3), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(2, 3), new Pawn(PlayerColor.BLACK));

        // Act
        List<HexCoordinate> moves = knight.getValidMoves(board, start);

        // Assert
        assertFalse(moves.isEmpty(), "Knight should have jump moves");

        for (HexCoordinate move : moves) {
            // Knight must be exactly a valid jump offset away
            int distance = new HexCoordinate(move.q() - start.q(), move.r() - start.r())
                    .distanceToCenter();
            assertTrue(distance == 2 || distance == 3,  // typical knight jump distances on hex
                    "Knight must move using a valid jump vector");
        }
    }

   @Test
void knightCanCaptureEnemyButNotFriendlyPiece() {
    HexBoard board = new HexBoard();
    HexCoordinate start = HexCoordinate.of(3, 3);
    Knight knight = new Knight(PlayerColor.WHITE);
    board.setPiece(start, knight);

    // Replicate Knight's 12 jump vectors
    int[][] jumps = {
        {  1, -3 }, {  2, -3 }, {  3, -2 }, {  3, -1 },
        {  2,  1 }, {  1,  2 }, { -1,  3 }, { -2,  3 },
        { -3,  2 }, { -3,  1 }, { -2, -1 }, { -1, -2 }
    };

    // Pick one for enemy, one for friendly
    HexCoordinate enemyPos = start.plus(jumps[0][0], jumps[0][1]);
    HexCoordinate friendlyPos = start.plus(jumps[1][0], jumps[1][1]);

    // Place pieces
    board.setPiece(enemyPos, new Pawn(PlayerColor.BLACK));
    board.setPiece(friendlyPos, new Pawn(PlayerColor.WHITE));

    // Act
    List<HexCoordinate> moves = knight.getValidMoves(board, start);

    // Assert
    assertTrue(moves.contains(enemyPos),
            "Knight should be able to capture enemy on jump square");
    assertFalse(moves.contains(friendlyPos),
            "Knight must not land on same-color piece");
}


    @Test
    void knightCannotMoveOutsideBoardBounds() {
        HexBoard board = new HexBoard();
        Knight knight = new Knight(PlayerColor.WHITE);

        // Place knight near edge
        HexCoordinate edge = HexCoordinate.of(0, 0);
        board.setPiece(edge, knight);

        List<HexCoordinate> moves = knight.getValidMoves(board, edge);

        for (HexCoordinate move : moves) {
            assertTrue(board.isWithinBounds(move),
                    "Knight must never generate moves outside board bounds");
        }
    }

    @Test
    void knightMovesCountRespectsBoardEdges() {
        HexBoard board = new HexBoard();
        Knight knight = new Knight(PlayerColor.WHITE);

        // Choose a corner so some jumps are off-board
        HexCoordinate corner = HexCoordinate.of(0, 0);
        board.setPiece(corner, knight);

        List<HexCoordinate> moves = knight.getValidMoves(board, corner);

        // Compute number of valid jump offsets dynamically
        long validMoves = moves.stream().filter(board::isWithinBounds).count();

        // Assert that all generated moves are within bounds
        assertEquals(validMoves, moves.size(), "All knight moves must be within board bounds");

        // Assert at least one jump exists (assuming board is big enough)
        assertTrue(moves.size() > 0, "Knight should have at least one valid jump from corner");
    }
}
