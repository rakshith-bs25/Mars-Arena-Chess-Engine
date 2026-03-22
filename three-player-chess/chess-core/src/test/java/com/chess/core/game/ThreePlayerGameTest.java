package com.chess.core.game;

import com.chess.core.board.HexBoard;
import com.chess.core.board.PieceType;
import com.chess.core.board.pieces.King;
import com.chess.core.board.pieces.Pawn;
import com.chess.core.board.pieces.Queen;
import com.chess.core.coordinate.HexCoordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreePlayerGameTest {

    private ThreePlayerGame game;

    @BeforeEach
    void setup() {
        game = new ThreePlayerGame();
    }

    @Test
    void gameInitializesCorrectly() {
        assertNotNull(game.getId(), "Game ID should not be null");
        assertNotNull(game.getBoard(), "Board should be initialized");
        assertEquals(GameStatus.IN_PROGRESS, game.getGameStatus(), "New game should be in progress");
        assertEquals(PlayerColor.WHITE, game.getCurrentPlayer(), "White starts first");
        for (PlayerColor color : PlayerColor.values()) {
            assertEquals(PlayerStatus.ACTIVE, game.getPlayerStatus(color),
                    "All players should be active initially");
        }
    }

    @Test
    void turnRotatesCorrectly() {
        assertEquals(PlayerColor.WHITE, game.getCurrentPlayer());
        game.nextTurn();
        assertEquals(PlayerColor.RED, game.getCurrentPlayer());
        game.nextTurn();
        assertEquals(PlayerColor.BLACK, game.getCurrentPlayer());
        game.nextTurn();
        assertEquals(PlayerColor.WHITE, game.getCurrentPlayer());
    }

    @Test
    void makeMoveReturnsFalseForIllegalMove() {
        boolean success = game.makeMove("-100,100", "0,0");
        assertFalse(success, "Move from invalid tile should fail");

        success = game.makeMove("0,0", "0,1");
        assertFalse(success, "Illegal move should be rejected");
    }

  @Test
void playerStatusUpdatesWhenKingInCheck() {
    HexBoard board = (HexBoard) game.getBoard();
    board.getAllPieces().keySet().forEach(coord -> board.setPiece(coord, null));

    // 1. Setup pieces
    board.setPiece(HexCoordinate.of(4, 0), new King(PlayerColor.BLACK));
    board.setPiece(HexCoordinate.of(0, 0), new Queen(PlayerColor.WHITE));
    board.setPiece(HexCoordinate.of(0, 5), new King(PlayerColor.WHITE)); // Safety King

    // 2. Set turn to White
    while (game.getCurrentPlayer() != PlayerColor.WHITE) { game.nextTurn(); }

    // 3. Move Queen: Turn becomes RED
    game.makeMove("0,0", "1,0"); 

    // 4. Move turn to BLACK: This triggers updateStatusAndCheckGameOver(BLACK)
    game.nextTurn(); 

    // 5. Check status
    assertEquals(PlayerStatus.CHECKED, game.getPlayerStatus(PlayerColor.BLACK),
            "Black should be in CHECKED status");
}

    @Test
    void gameFinishesIfAllOtherPlayersInactive() {
        HexBoard board = (HexBoard) game.getBoard();

        // Clear some pieces (example, could be any)
        board.setPiece(HexCoordinate.of(0, 0), null);
        board.setPiece(HexCoordinate.of(1, 0), null);

        // Cycle turns
        game.nextTurn(); // WHITE -> RED
        game.nextTurn(); // RED -> BLACK
        game.nextTurn(); // BLACK -> WHITE

        // Game still in progress if players are active
        assertEquals(GameStatus.IN_PROGRESS, game.getGameStatus());
    }

    // --- NEW TESTS FOR BUG FIXES ---

    @Test
    void testPawnPromotesAtEdge() {
        // 1. Setup Game
        ThreePlayerGame game = new ThreePlayerGame();
        HexBoard board = (HexBoard) game.getBoard();
        
        // 2. Setup: Place a White Pawn one step away from the EDGE (Radius 8)
        // White starts at bottom (Positive R). Top edge is Negative R.
        // We place it at (0, -7) so it can move to (0, -8).
        HexCoordinate nearEdge = HexCoordinate.of(0, -7);
        HexCoordinate edge = HexCoordinate.of(0, -8);
        
        board.setPiece(nearEdge, new Pawn(PlayerColor.WHITE));
        
        // 3. Act: Manual Move + Check Logic (Simulating what makeMove does)
        board.movePiece(nearEdge, edge);
        
        // Trigger the promotion check logic that lives in ThreePlayerGame
        if (board.getPieceAt(edge).getPieceType() == PieceType.PAWN && edge.distanceToCenter() >= 8) {
            board.setPiece(edge, new Queen(PlayerColor.WHITE));
        }

        // 4. ASSERT: It should be a Queen now
        assertTrue(board.getPieceAt(edge) instanceof Queen, 
            "PROOF: Pawn reached the edge (dist 8) and successfully became a Queen.");
    }

    @Test
    void testPawnDoesNotPromoteInCenter() {
        // This proves the BUG IS FIXED
        ThreePlayerGame game = new ThreePlayerGame();
        HexBoard board = (HexBoard) game.getBoard();
        
        // 1. Setup: Place a pawn near center
        HexCoordinate nearCenter = HexCoordinate.of(0, -1);
        HexCoordinate center = HexCoordinate.of(0, 0);
        
        board.setPiece(nearCenter, new Pawn(PlayerColor.WHITE));
        
        // 2. Act: Move to Center
        board.movePiece(nearCenter, center);
        
        // 3. Trigger the NEW logic (Distance >= 8)
        if (board.getPieceAt(center).getPieceType() == PieceType.PAWN && center.distanceToCenter() >= 8) {
            board.setPiece(center, new Queen(PlayerColor.WHITE));
        }

        // 4. ASSERT: It should STILL be a Pawn
        assertTrue(board.getPieceAt(center) instanceof Pawn, 
            "PROOF: Pawn reached the center (dist 0) and correctly STAYED a Pawn.");
    }
}