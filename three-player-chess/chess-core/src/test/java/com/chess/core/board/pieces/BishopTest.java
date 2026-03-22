package com.chess.core.board.pieces;

import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDiagonal;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    @Test
void bishopSlidesAlongDiagonalsUntilBlocked() {
    HexBoard board = new HexBoard();

    HexCoordinate start = HexCoordinate.of(0, 4);
    Bishop bishop = new Bishop(PlayerColor.WHITE);

    HexCoordinate friendlyPos = HexCoordinate.of(1, 3);
    Pawn friendlyPawn = new Pawn(PlayerColor.WHITE);

    HexCoordinate enemyPos = HexCoordinate.of(-1, 5);
    Pawn enemyPawn = new Pawn(PlayerColor.BLACK);

    board.setPiece(start, bishop);
    board.setPiece(friendlyPos, friendlyPawn);
    board.setPiece(enemyPos, enemyPawn);

    List<HexCoordinate> moves = bishop.getValidMoves(board, start);

    // Check all HexDiagonal directions
    for (HexDiagonal dir : HexDiagonal.values()) {
        HexCoordinate next = start.plus(dir.dq(), dir.dr());

        while (board.isWithinBounds(next)) {
            Piece piece = board.getPieceAt(next);

            if (piece == null) {
                assertTrue(moves.contains(next),
                        "Bishop should be able to slide to empty square: " + next);
            } else if (piece.getOwner() != bishop.getOwner()) {
                // Enemy piece can be captured
                assertTrue(moves.contains(next),
                        "Bishop should be able to capture enemy at: " + next);
                break; // Bishop stops after capturing
            } else {
                // Friendly piece blocks movement
                assertFalse(moves.contains(next),
                        "Bishop cannot move through friendly piece at: " + next);
                break;
            }

            // Move to next square along same diagonal
            next = next.plus(dir.dq(), dir.dr());
        }
    }
}


    @Test
    void bishopHasNoMovesWhenSurroundedByFriendlyPieces() {
        HexBoard board = new HexBoard();
        HexCoordinate start = HexCoordinate.of(0, 4);
        Bishop bishop = new Bishop(PlayerColor.WHITE);

        board.setPiece(start, bishop);

        // Block all diagonal directions with friendly pieces
        for (var dir : com.chess.core.coordinate.HexDiagonal.values()) {
            HexCoordinate block = start.plus(dir.dq(), dir.dr());
            if (board.isWithinBounds(block)) {
                board.setPiece(block, new Pawn(PlayerColor.WHITE));
            }
        }

        // Act
        List<HexCoordinate> moves = bishop.getValidMoves(board, start);

        // Assert
        assertTrue(moves.isEmpty(),
                "Bishop should have no legal moves when fully blocked by friendly pieces");
    }

    @Test
    void bishopCannotMoveOutsideBoardBounds() {
        HexBoard board = new HexBoard();

        // Place bishop near the edge
        HexCoordinate edge = HexCoordinate.of(0, 0);
        Bishop bishop = new Bishop(PlayerColor.WHITE);

        board.setPiece(edge, bishop);

        // Act
        List<HexCoordinate> moves = bishop.getValidMoves(board, edge);

        // Assert
        for (HexCoordinate move : moves) {
            assertTrue(board.isWithinBounds(move),
                    "Bishop must never generate moves outside board bounds");
        }
    }
}
