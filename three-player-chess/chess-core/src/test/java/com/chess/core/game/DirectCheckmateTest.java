package com.chess.core.game;

import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.pieces.*;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.rules.MoveValidator;
import com.chess.core.rules.BoardAnalyzer;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

public class DirectCheckmateTest {

    @Test
    public void testRingOfFireLogic() {
        // 1. here we Setup a fresh Board (No Game Engine, just Board)
        HexBoard board = new HexBoard();
        
        // 2. Clear Board
        for (HexCoordinate c : board.getPlayableCoordinates()) {
            board.setPiece(c, null);
        }

        // 3.  THE IMPOSSIBLE TRAP
        // Red King at (0,0)
        board.setPiece(HexCoordinate.of(0, 0), new King(PlayerColor.RED));

        // Surround with 6 White Rooks (Perfect coverage)
        board.setPiece(HexCoordinate.of(1, -1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(1, 0), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(0, 1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, 1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, 0), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(0, -1), new Rook(PlayerColor.WHITE));

        // We collect all the "Valid Moves" it claims to find.
        List<String> escapeRoutes = new ArrayList<>();
        
        Piece king = board.getPieceAt(HexCoordinate.of(0, 0));
        List<HexCoordinate> kingMoves = king.getValidMoves(board, HexCoordinate.of(0, 0));
        
        for (HexCoordinate dest : kingMoves) {
            // Check if this move is actually legal (handling check/self-check)
            if (MoveValidator.isMoveLegal(board, HexCoordinate.of(0, 0), dest, PlayerColor.RED)) {
                escapeRoutes.add("King to " + dest.toString());
            }
        }
        
        // 5. CHECK THE RESULTS
        boolean isCheck = BoardAnalyzer.isKingInCheck(board, PlayerColor.RED);
        boolean isMate = MoveValidator.isCheckMate(board, PlayerColor.RED);

        // 6. FAILURE REPORTING
        if (!isCheck) {
            throw new RuntimeException("FAILURE: Logic says King is NOT in Check! (But he is surrounded)");
        }
        
        if (!escapeRoutes.isEmpty()) {
            throw new RuntimeException("FAILURE: King has escape routes: " + escapeRoutes.toString());
        }
        
        if (!isMate) {
            throw new RuntimeException("FAILURE: isCheck=true, Moves=0, but isCheckMate returns FALSE.");
        }

        System.out.println("SUCCESS: Direct Checkmate Logic is working perfectly.");
    }

      @Test
    public void testStalemateLogic() {
        // 1. Setup board
        HexBoard board = new HexBoard();
        for (HexCoordinate c : board.getPlayableCoordinates()) {
            board.setPiece(c, null);
        }

        // 2. Place Red King at center
        board.setPiece(HexCoordinate.of(0, 0), new King(PlayerColor.RED));

        // 3. Surround King with enemy pieces that do NOT attack him (stalemate)
        // These pieces block all moves, but King is not in check
        board.setPiece(HexCoordinate.of(2, -1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(1, 1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, 2), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-2, 1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, -1), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(1, -2), new Rook(PlayerColor.WHITE));

        boolean isCheck = BoardAnalyzer.isKingInCheck(board, PlayerColor.RED);
        boolean isStalemate = MoveValidator.isStalemate(board, PlayerColor.RED);

        if (isCheck)
            throw new RuntimeException("FAILURE: King should NOT be in check for stalemate!");
        if (!isStalemate)
            throw new RuntimeException("FAILURE: Stalemate not detected correctly!");

        System.out.println("SUCCESS: Stalemate logic works perfectly.");
    }
}

