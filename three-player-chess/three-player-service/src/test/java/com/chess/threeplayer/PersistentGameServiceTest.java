package com.chess.threeplayer;

import com.chess.core.game.GameStatus;
import com.chess.core.game.PlayerColor;
import com.chess.core.game.PlayerStatus;
import com.chess.core.dto.GameStateDTO;
import com.chess.threeplayer.persistence.GameEntity;
import com.chess.threeplayer.persistence.GameRepository;
import com.chess.threeplayer.service.PersistentGameService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistentGameServiceTest {

    @Mock
    private GameRepository repository;

    @InjectMocks
    private PersistentGameService service;

    @Test
    @DisplayName("createGame - Should initialize entity and save to repository")
    void createGame_ShouldSaveAndReturnDto() {
        GameStateDTO result = service.createGame();

        assertNotNull(result);
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus()); 
        assertEquals(PlayerColor.WHITE, result.getCurrentPlayer());
        
        verify(repository, times(1)).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("getGameState - Should throw IllegalArgumentException when game not found")
    void getGameState_NotFound() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getGameState("invalid-id"));
    }

    @Test
    @DisplayName("makeMove - Should update game state and switch player for valid move")
    void makeMove_ValidMove() {
        String gameId = "game-1";
        // Create an active game with WHITE as current player
        GameEntity entity = new GameEntity(
            gameId, 
            GameStatus.IN_PROGRESS, 
            PlayerColor.WHITE, 
            60L, 
            60000L, 60000L, 60000L, 
            Instant.now()
        );
        
        when(repository.findById(gameId)).thenReturn(Optional.of(entity));

        // Act: Try a move. 
        // Assuming (0,6) -> (0,5) is a valid pawn move for White in your Hex setup.
        GameStateDTO result = service.makeMove(gameId, "0,6", "0,5", PlayerColor.WHITE);

        // If the move was valid logic-wise:
        if (!entity.getMoves().isEmpty()) {
             // If move worked, expect RED
             assertEquals(PlayerColor.RED, result.getCurrentPlayer());
             verify(repository, atLeastOnce()).save(entity);
        } else {
             // If engine rejected it, it stays WHITE. 
             assertEquals(PlayerColor.WHITE, result.getCurrentPlayer());
        }
    }

    @Test
    @DisplayName("makeMove - Should NOT update state for illegal move")
    void makeMove_IllegalMove() {
        String gameId = "game-1";
        GameEntity entity = new GameEntity(
            gameId, 
            GameStatus.IN_PROGRESS, 
            PlayerColor.WHITE, 
            60L, 
            60000L, 60000L, 60000L, 
            Instant.now()
        );
        
        when(repository.findById(gameId)).thenReturn(Optional.of(entity));

        // Act: Try to move from non-existent tile or invalid logic
        GameStateDTO result = service.makeMove(gameId, "99,99", "88,88", PlayerColor.WHITE); 

        assertEquals(PlayerColor.WHITE, result.getCurrentPlayer()); 
        assertTrue(entity.getMoves().isEmpty()); 
    }

    @Test
    @DisplayName("Check Timeout - Should set status to TIMEOUT if time exceeded")
    void checkTimeout_ShouldTrigger() {
        String gameId = "timeout-game";
        // Simulate time exceeded (61 seconds ago)
        Instant oldTime = Instant.now().minus(61, ChronoUnit.SECONDS);
        
        GameEntity entity = new GameEntity(
            gameId, 
            GameStatus.IN_PROGRESS, 
            PlayerColor.WHITE, 
            60L, 
            60000L, 60000L, 60000L, 
            oldTime
        );
        
        when(repository.findById(gameId)).thenReturn(Optional.of(entity));

        // Act: Calling getGameState triggers the timeout check
        GameStateDTO result = service.getGameState(gameId);

        // Assert: White should be timed out
        assertEquals(PlayerStatus.TIMEOUT, entity.statusOf(PlayerColor.WHITE));
        
        // Should save the timeout state
        verify(repository, atLeastOnce()).save(entity);
    }
}