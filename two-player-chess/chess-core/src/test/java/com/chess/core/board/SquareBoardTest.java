package com.chess.core.board;

import com.chess.core.board.pieces.Pawn;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareBoardTest {

    private SquareBoard board;

    @BeforeEach
    void setUp() {
        board = new SquareBoard();
    }

    @Test
    void testSetAndGetPiece() {
        SquareCoordinate coord = SquareCoordinate.of(3, 3);
        Pawn pawn = new Pawn(PlayerColor.WHITE);

        board.setPiece(coord, pawn);

        assertEquals(pawn, board.getPieceAt(coord), "Should retrieve the exact piece we placed");
    }

    @Test
    void testRemovePieceUsingSetNull() {
        SquareCoordinate coord = SquareCoordinate.of(3, 3);
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        board.setPiece(coord, pawn);

       
        board.setPiece(coord, null);

        assertNull(board.getPieceAt(coord), "Piece should be removed from board");
    }

    @Test
    void testMovePiece() {
        SquareCoordinate from = SquareCoordinate.of(0, 0);
        SquareCoordinate to = SquareCoordinate.of(0, 1);
        Pawn pawn = new Pawn(PlayerColor.WHITE);

        board.setPiece(from, pawn);
        board.movePiece(from, to);

        assertNull(board.getPieceAt(from), "Old square should be empty");
        assertEquals(pawn, board.getPieceAt(to), "Piece should be at new square");
    }

    @Test
    void testBoundaryChecks() {
        // Valid Cases
        assertTrue(board.isWithinBounds(SquareCoordinate.of(0, 0)));
        assertTrue(board.isWithinBounds(SquareCoordinate.of(7, 7)));

        // Invalid Cases (OutOfBounds)
        assertFalse(board.isWithinBounds(SquareCoordinate.of(-1, 0)), "Negative X should be invalid");
        assertFalse(board.isWithinBounds(SquareCoordinate.of(0, -1)), "Negative Y should be invalid");
        assertFalse(board.isWithinBounds(SquareCoordinate.of(8, 0)), "X=8 should be invalid");
        assertFalse(board.isWithinBounds(SquareCoordinate.of(0, 8)), "Y=8 should be invalid");
    }

    @Test
    void testCopyConstructorDeepCopy() {
        // 1. Setup original board
        SquareCoordinate coord = SquareCoordinate.of(3, 3);
        Pawn originalPawn = new Pawn(PlayerColor.WHITE);
        board.setPiece(coord, originalPawn);

        // 2. Create copy
        SquareBoard copyBoard = new SquareBoard(board);

        // 3. Verify copy has the piece
        assertEquals(originalPawn, copyBoard.getPieceAt(coord));

        // 4. Modify Copy (Move piece)
        SquareCoordinate newCoord = SquareCoordinate.of(4, 4);
        copyBoard.movePiece(coord, newCoord);

        // 5. CRITICAL: Ensure Original Board is UNTOUCHED
        assertNotNull(board.getPieceAt(coord), "Original board piece should NOT move");
        assertNull(board.getPieceAt(newCoord), "Original board should not have piece at new location");
        
        // Ensure Copy IS modified
        assertNull(copyBoard.getPieceAt(coord));
        assertEquals(originalPawn, copyBoard.getPieceAt(newCoord));
    }
}