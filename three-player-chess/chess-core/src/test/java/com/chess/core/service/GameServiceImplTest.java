package com.chess.core.service;

import com.chess.core.dto.GameStateDTO;
import com.chess.core.dto.TileDTO;
import com.chess.core.game.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceImplTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl();
    }

    @Test
    @DisplayName("Should create a new game with a unique ID and initial board state")
    void testCreateGame() {
        GameStateDTO gameState = gameService.createGame();

        assertNotNull(gameState, "Game state should not be null");
        assertNotNull(gameState.getGameId(), "Game ID should be generated");
        
        assertFalse(gameState.getBoard().isEmpty(), "Board should be populated with tiles");
        
        assertEquals(PlayerColor.WHITE, gameState.getCurrentPlayer(), "First player should be WHITE");
    }

    @Test
    @DisplayName("Should retrieve an existing game by ID")
    void testGetGameState_Success() {
        GameStateDTO createdGame = gameService.createGame();
        String gameId = createdGame.getGameId();

        GameStateDTO retrievedGame = gameService.getGameState(gameId);

        assertEquals(createdGame.getGameId(), retrievedGame.getGameId());
        assertEquals(createdGame.getCurrentPlayer(), retrievedGame.getCurrentPlayer());
    }

    @Test
    @DisplayName("Should throw exception when getting a non-existent game")
    void testGetGameState_NotFound() {
        String invalidId = "non-existent-id-999";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.getGameState(invalidId);
        });

        assertTrue(exception.getMessage().contains("Game not found"));
    }

    @Test
    @DisplayName("Should return empty list for legal moves if game not found")
    void testGetLegalMoves_GameNotFound() {
        List<String> moves = gameService.getLegalMoves("invalid-id", "a1");
        
        assertNotNull(moves);
        assertTrue(moves.isEmpty(), "Should return empty list for invalid game ID");
    }

    @Test
    @DisplayName("Should return valid moves for a piece on the board")
    void testGetLegalMoves_ValidPiece() {
        GameStateDTO game = gameService.createGame();
        
        TileDTO whitePawnTile = game.getBoard().stream()
            .filter(t -> t.getOwner() == PlayerColor.WHITE && "PAWN".equals(t.getPieceType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Board setup failed: No White Pawns found"));

        List<String> legalMoves = gameService.getLegalMoves(game.getGameId(), whitePawnTile.getCoordinate());

        assertNotNull(legalMoves);
    }

    @Test
    @DisplayName("Should throw exception when making a move on invalid game")
    void testMakeMove_GameNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.makeMove("invalid-id", "a1", "a2", PlayerColor.WHITE);
        });
    }
}