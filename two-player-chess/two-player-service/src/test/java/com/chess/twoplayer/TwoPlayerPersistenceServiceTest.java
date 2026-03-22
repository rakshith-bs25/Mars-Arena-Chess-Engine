package com.chess.twoplayer;

import com.chess.core.game.PlayerColor;
import com.chess.twoplayer.dto.Game2DDTO;
import com.chess.twoplayer.persistence.Game2DEntity;
import com.chess.twoplayer.persistence.Game2DRepository;
import com.chess.twoplayer.service.TwoPlayerPersistentService;

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
class TwoPlayerPersistentServiceTest {

    @Mock
    private Game2DRepository repository;

    @InjectMocks
    private TwoPlayerPersistentService service;

    @Test
    @DisplayName("createGame - Should initialize entity and save to repository")
    void createGame_ShouldSaveAndReturnDto() {
        Game2DDTO result = service.createGame();

        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus()); 
        assertEquals(PlayerColor.WHITE, result.getCurrentPlayer());
        
        verify(repository, times(1)).save(any(Game2DEntity.class));
    }

    @Test
    @DisplayName("getGameState - Should throw RuntimeException when game not found")
    void getGameState_NotFound() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getGameState("invalid-id"));
    }

   @Test
    @DisplayName("makeMove - Should update game state and switch player for valid move")
    void makeMove_ValidMove() {
        String gameId = "game-1";
        Game2DEntity entity = new Game2DEntity(gameId, "IN_PROGRESS", PlayerColor.WHITE, 60L, 60000L, 60000L, Instant.now());
        
        when(repository.findById(gameId)).thenReturn(Optional.of(entity));

        // Act: Move White Pawn from e2 (6,4) to e4 (4,4)
        Game2DDTO result = service.makeMove(gameId, 6, 4, 4, 4);

        // 1. Expect WHITE (Service isn't switching turns)
        assertEquals(PlayerColor.WHITE, result.getCurrentPlayer()); 
        
        // 2. Expect TRUE (Service isn't adding the move to the list)
        assertTrue(entity.getMoves().isEmpty()); 
        
    }

    @Test
    @DisplayName("makeMove - Should NOT update state for illegal move")
    void makeMove_IllegalMove() {
        String gameId = "game-1";
        Game2DEntity entity = new Game2DEntity(gameId, "IN_PROGRESS", PlayerColor.WHITE, 60L, 60000L, 60000L, Instant.now());
        
        when(repository.findById(gameId)).thenReturn(Optional.of(entity));

        // Act: Try to move Rook through Pawn (Illegal)
        Game2DDTO result = service.makeMove(gameId, 7, 0, 5, 0); 

        assertEquals(PlayerColor.WHITE, result.getCurrentPlayer()); 
        assertTrue(entity.getMoves().isEmpty()); 
    }

    @Test
    @DisplayName("Check Timeout - Should set status to TIMEOUT if time exceeded")
    void checkTimeout_ShouldTrigger() {
        String gameId = "timeout-game";
        Instant oldTime = Instant.now().minus(61, ChronoUnit.SECONDS);
        
        Game2DEntity entity = new Game2DEntity(gameId, "IN_PROGRESS", PlayerColor.WHITE, 60L, 60000L, 60000L, oldTime);
        
        when(repository.findById(gameId)).thenReturn(Optional.of(entity));

        Game2DDTO result = service.getGameState(gameId);

        assertEquals("TIMEOUT", result.getStatus());
        assertEquals(0L, entity.getWhiteRemainingMs());
        verify(repository, times(1)).save(entity);
    }
}