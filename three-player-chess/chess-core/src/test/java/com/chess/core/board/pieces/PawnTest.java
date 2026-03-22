package com.chess.core.board.pieces;

import com.chess.core.board.HexBoard;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.Test;
import com.chess.core.board.Piece;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void pawnCanMoveForwardIfSquareIsEmpty() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 4);

        board.setPiece(start, pawn);

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);

        // Forward squares depend on pawn color
        List<HexCoordinate> forwardSquares = List.of(
                start.plus(0, -1), // straight forward
                start.plus(1, -1)  // diagonal-forward-right (if empty for movement)
        );

        boolean hasForward = forwardSquares.stream().anyMatch(moves::contains);
        assertTrue(hasForward, "Pawn should be able to move forward to an empty square");
    }

    @Test
    void pawnCannotMoveForwardIfBlocked() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 4);

        board.setPiece(start, pawn);

        // Block forward squares
        board.setPiece(start.plus(0, -1), new Pawn(PlayerColor.BLACK));
        board.setPiece(start.plus(1, -1), new Pawn(PlayerColor.BLACK));

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);
        assertTrue(moves.isEmpty(), "Pawn must not move forward if blocked");
    }

    @Test
    void pawnCanDoubleMoveIfNotMovedAndPathIsClear() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 4);

        board.setPiece(start, pawn);

        // Double forward depends on pawn color: two hexes forward
        List<HexCoordinate> doubleSquares = List.of(
                start.plus(0, -2), // two steps forward
                start.plus(2, -2)  // diagonal double (if allowed)
        );

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);
        boolean hasDouble = doubleSquares.stream().anyMatch(moves::contains);

        assertTrue(hasDouble, "Pawn should be able to double move from initial position");
    }

    @Test
    void pawnCannotDoubleMoveAfterItHasMoved() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 4);

        board.setPiece(start, pawn);
        pawn.setHasMoved(true); // simulate already moved

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);

        List<HexCoordinate> doubleSquares = List.of(
                start.plus(0, -2),
                start.plus(2, -2)
        );

        boolean hasDouble = doubleSquares.stream().anyMatch(moves::contains);
        assertFalse(hasDouble, "Pawn must not double move after it has moved");
    }

    @Test
    void pawnCanCaptureEnemyOnDiagonal() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 4);

        // Correct forward-left and forward-right captures
        HexCoordinate enemy1 = start.plus(-1, -1);
        HexCoordinate enemy2 = start.plus(1, -1);

        board.setPiece(start, pawn);
        board.setPiece(enemy1, new Pawn(PlayerColor.BLACK));
        board.setPiece(enemy2, new Pawn(PlayerColor.BLACK));

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);

        boolean canCapture = moves.contains(enemy1) || moves.contains(enemy2);
        assertTrue(canCapture, "Pawn should be able to capture enemy diagonally");
    }

    @Test
    void pawnCannotCaptureFriendlyPiece() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 4);

        HexCoordinate friendly = start.plus(1, -1); // diagonal-forward-right

        board.setPiece(start, pawn);
        board.setPiece(friendly, new Pawn(PlayerColor.WHITE));

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);

        assertFalse(moves.contains(friendly),
                "Pawn must not capture same-color piece");
    }

    @Test
    void pawnMovesDependOnColor() {
        HexBoard board = new HexBoard();

        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK);
        Pawn redPawn = new Pawn(PlayerColor.RED);

        HexCoordinate whitePos = HexCoordinate.of(0, 4);
        HexCoordinate blackPos = HexCoordinate.of(3, 0);
        HexCoordinate redPos = HexCoordinate.of(-3, 0);

        board.setPiece(whitePos, whitePawn);
        board.setPiece(blackPos, blackPawn);
        board.setPiece(redPos, redPawn);

        List<HexCoordinate> whiteMoves = whitePawn.getValidMoves(board, whitePos);
        List<HexCoordinate> blackMoves = blackPawn.getValidMoves(board, blackPos);
        List<HexCoordinate> redMoves = redPawn.getValidMoves(board, redPos);

        assertFalse(whiteMoves.isEmpty(), "White pawn should have moves");
        assertFalse(blackMoves.isEmpty(), "Black pawn should have moves");
        assertFalse(redMoves.isEmpty(), "Red pawn should have moves");

        // Movement sets must differ for different colors
        assertNotEquals(whiteMoves, blackMoves, "Pawn movement must differ by color");
        assertNotEquals(whiteMoves, redMoves, "Pawn movement must differ by color");
        assertNotEquals(blackMoves, redMoves, "Pawn movement must differ by color");
    }

    @Test
    void pawnMovesAreWithinBoardBounds() {
        HexBoard board = new HexBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        HexCoordinate start = HexCoordinate.of(0, 0); // corner

        board.setPiece(start, pawn);

        List<HexCoordinate> moves = pawn.getValidMoves(board, start);

        // Ensure all generated moves are valid
        for (HexCoordinate move : moves) {
            assertTrue(board.isWithinBounds(move), "Pawn must not move outside board bounds");
        }
    }


}


