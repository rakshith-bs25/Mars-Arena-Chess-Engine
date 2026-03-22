package com.chess.core.board.pieces;

import com.chess.core.board.Board;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PieceMovementTest {

    private FakeBoard board;

    @BeforeEach
    void setUp() {
        board = new FakeBoard();
    }

    @Nested
    class BasicPieceTest {
        @Test
        void testBasicPieceHasNoMoves() {
            BasicPiece basic = new BasicPiece(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            
            List<SquareCoordinate> moves = basic.getValidMoves(board, start);
            
            assertTrue(moves.isEmpty(), "BasicPiece should have 0 valid moves");
        }
    }

    @Nested
    class RookTest {
        @Test
        void testRookMovementOpenBoard() {
            Rook rook = new Rook(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            board.setPiece(start, rook);

            List<SquareCoordinate> moves = rook.getValidMoves(board, start);

            assertEquals(14, moves.size());
            assertTrue(moves.contains(SquareCoordinate.of(3, 7)));
            assertTrue(moves.contains(SquareCoordinate.of(3, 0)));
            assertTrue(moves.contains(SquareCoordinate.of(0, 3)));
            assertTrue(moves.contains(SquareCoordinate.of(7, 3)));
        }

        @Test
        void testRookBlockedByFriend() {
            Rook rook = new Rook(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(0, 0);
             board.setPiece(start, rook);
            board.setPiece(SquareCoordinate.of(0, 2), new Pawn(PlayerColor.WHITE));

            List<SquareCoordinate> moves = rook.getValidMoves(board, start);

            assertTrue(moves.contains(SquareCoordinate.of(0, 1)));
            assertFalse(moves.contains(SquareCoordinate.of(0, 2)));
            assertFalse(moves.contains(SquareCoordinate.of(0, 3)));
        }

        @Test
        void testRookCapturesEnemy() {
            Rook rook = new Rook(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(0, 0);
            board.setPiece(start, rook);
            board.setPiece(SquareCoordinate.of(0, 2), new Pawn(PlayerColor.BLACK));

            List<SquareCoordinate> moves = rook.getValidMoves(board, start);

            assertTrue(moves.contains(SquareCoordinate.of(0, 2)), "Should capture enemy");
            assertFalse(moves.contains(SquareCoordinate.of(0, 3)), "Should not go through enemy");
        }
    }

    @Nested
    class BishopTest {
        @Test
        void testBishopMovementOpenBoard() {
            Bishop bishop = new Bishop(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            board.setPiece(start, bishop);
            List<SquareCoordinate> moves = bishop.getValidMoves(board, start);

            assertEquals(13, moves.size());
            assertTrue(moves.contains(SquareCoordinate.of(0, 0)));
            assertTrue(moves.contains(SquareCoordinate.of(7, 7)));
        }
    }

    @Nested
    class QueenTest {
        @Test
        void testQueenCombinesRookAndBishop() {
            Queen queen = new Queen(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            board.setPiece(start, queen);
            List<SquareCoordinate> moves = queen.getValidMoves(board, start);

            assertEquals(27, moves.size());
            assertTrue(moves.contains(SquareCoordinate.of(3, 7)));
            assertTrue(moves.contains(SquareCoordinate.of(7, 7)));
        }
    }

    @Nested
    class KnightTest {
        @Test
        void testKnightJumps() {
            Knight knight = new Knight(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            board.setPiece(start, knight);
            List<SquareCoordinate> moves = knight.getValidMoves(board, start);

            assertEquals(8, moves.size());
            assertTrue(moves.contains(SquareCoordinate.of(4, 5)));
        }

        @Test
        void testKnightCorner() {
            Knight knight = new Knight(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(0, 0);
        board.setPiece(start, knight);
            List<SquareCoordinate> moves = knight.getValidMoves(board, start);

            assertEquals(2, moves.size());
            assertTrue(moves.contains(SquareCoordinate.of(1, 2)));
            assertTrue(moves.contains(SquareCoordinate.of(2, 1)));
        }
    }

    @Nested
    class PawnTest {
        @Test
        void testWhitePawnForwardAndDouble() {
            Pawn pawn = new Pawn(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 1);
            board.setPiece(start, pawn);

            List<SquareCoordinate> moves = pawn.getValidMoves(board, start);

            assertTrue(moves.contains(SquareCoordinate.of(3, 2)));
            assertTrue(moves.contains(SquareCoordinate.of(3, 3)));
        }

        @Test
        void testBlackPawnForward() {
            Pawn pawn = new Pawn(PlayerColor.BLACK);
            SquareCoordinate start = SquareCoordinate.of(3, 6);
            board.setPiece(start, pawn);
            List<SquareCoordinate> moves = pawn.getValidMoves(board, start);

            assertTrue(moves.contains(SquareCoordinate.of(3, 5)));
            assertTrue(moves.contains(SquareCoordinate.of(3, 4)));
        }

        @Test
        void testPawnBlocked() {
            Pawn pawn = new Pawn(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 1);
            board.setPiece(start, pawn);
            board.setPiece(SquareCoordinate.of(3, 2), new Pawn(PlayerColor.BLACK));

            List<SquareCoordinate> moves = pawn.getValidMoves(board, start);

            assertFalse(moves.contains(SquareCoordinate.of(3, 2)));
            assertFalse(moves.contains(SquareCoordinate.of(3, 3)));
        }

        @Test
        void testPawnCapture() {
            Pawn pawn = new Pawn(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            board.setPiece(start, pawn);
            board.setPiece(SquareCoordinate.of(4, 4), new Pawn(PlayerColor.BLACK));
            board.setPiece(SquareCoordinate.of(2, 4), new Pawn(PlayerColor.WHITE));

            List<SquareCoordinate> moves = pawn.getValidMoves(board, start);

            assertTrue(moves.contains(SquareCoordinate.of(4, 4)));
            assertFalse(moves.contains(SquareCoordinate.of(2, 4)));
            assertTrue(moves.contains(SquareCoordinate.of(3, 4)));
        }
    }

    @Nested
    class KingTest {
        @Test
        void testKingBasicMoves() {
            King king = new King(PlayerColor.WHITE);
            SquareCoordinate start = SquareCoordinate.of(3, 3);
            board.setPiece(start, king);
            List<SquareCoordinate> moves = king.getValidMoves(board, start);

            assertEquals(8, moves.size());
            assertTrue(moves.contains(SquareCoordinate.of(3, 4)));
        }

        @Test
        void testKingCastlingKingside() {
            King king = new King(PlayerColor.WHITE);
            Rook rook = new Rook(PlayerColor.WHITE);
            SquareCoordinate kingStart = SquareCoordinate.of(4, 0);
            SquareCoordinate rookPos = SquareCoordinate.of(7, 0);
            
            board.setPiece(kingStart, king);
            board.setPiece(rookPos, rook);

            List<SquareCoordinate> moves = king.getValidMoves(board, kingStart);

            assertTrue(moves.contains(SquareCoordinate.of(6, 0)));
        }

        @Test
        void testCastlingBlocked() {
            King king = new King(PlayerColor.WHITE);
            Rook rook = new Rook(PlayerColor.WHITE);
            SquareCoordinate kingStart = SquareCoordinate.of(4, 0);
            SquareCoordinate rookPos = SquareCoordinate.of(7, 0);
            board.setPiece(kingStart, king);
            board.setPiece(rookPos, rook);
            board.setPiece(SquareCoordinate.of(5, 0), new Bishop(PlayerColor.WHITE));

            List<SquareCoordinate> moves = king.getValidMoves(board, kingStart);

            assertFalse(moves.contains(SquareCoordinate.of(6, 0)));
        }
    }

    
    // Matches the Board interface EXACTLY 
    static class FakeBoard implements Board {

        private final Map<SquareCoordinate, Piece> pieces = new HashMap<>();

        @Override
        public Piece getPieceAt(SquareCoordinate coord) {
            return pieces.get(coord);
        }

        @Override
        public void setPiece(SquareCoordinate coord, Piece piece) {
            if (piece == null) pieces.remove(coord);
            else pieces.put(coord, piece);
        }

        @Override
        public void movePiece(SquareCoordinate from, SquareCoordinate to) {
            throw new UnsupportedOperationException("movePiece not needed for movement tests");
        }

        @Override
        public boolean isWithinBounds(SquareCoordinate coord) {
            return coord.x() >= 0 && coord.x() < 8 && coord.y() >= 0 && coord.y() < 8;
        }

        // Helper for test setup
        public Map<SquareCoordinate, Piece> getAllPieces() {
            return pieces;
        }
    }
}