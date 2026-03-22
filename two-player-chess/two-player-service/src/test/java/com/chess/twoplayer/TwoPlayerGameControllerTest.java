package com.chess.twoplayer;

import com.chess.core.game.PlayerColor;
import com.chess.twoplayer.controller.TwoPlayerGameController;
import com.chess.twoplayer.dto.Clock2DDTO;
import com.chess.twoplayer.dto.Game2DDTO;
import com.chess.twoplayer.dto.MoveRequest;
import com.chess.twoplayer.service.TwoPlayerPersistentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TwoPlayerGameController.class)
class TwoPlayerGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwoPlayerPersistentService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /two-player/games - Should create game and return 200")
    void createGame_Success() throws Exception {
        // Arrange
        Game2DDTO mockGame = new Game2DDTO("game-123", PlayerColor.WHITE, Collections.emptyList(), "IN_PROGRESS", Collections.emptyList(), 60000L, 60000L);
        when(gameService.createGame()).thenReturn(mockGame);

        // Act & Assert
        mockMvc.perform(post("/two-player/games")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("game-123"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS")); 
    }

    @Test
    @DisplayName("GET /two-player/games/{gameId} - Should return game state")
    void getGame_Success() throws Exception {
        String gameId = "game-123";
        Game2DDTO mockGame = new Game2DDTO(gameId, PlayerColor.WHITE, Collections.emptyList(), "IN_PROGRESS", Collections.emptyList(), 60000L, 60000L);
        when(gameService.getGameState(gameId)).thenReturn(mockGame);

        mockMvc.perform(get("/two-player/games/{gameId}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId));
    }

    @Test
    @DisplayName("POST /two-player/games/{gameId}/move - Should execute move")
    void makeMove_Success() throws Exception {
        String gameId = "game-123";
        MoveRequest req = new MoveRequest();
        req.setFromX(6); req.setFromY(4); req.setToX(4); req.setToY(4);

        Game2DDTO updatedGame = new Game2DDTO(gameId, PlayerColor.BLACK, Collections.emptyList(), "IN_PROGRESS", Collections.emptyList(), 60000L, 60000L);
        
        when(gameService.makeMove(eq(gameId), eq(6), eq(4), eq(4), eq(4))).thenReturn(updatedGame);

        mockMvc.perform(post("/two-player/games/{gameId}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPlayer").value("BLACK"));
    }

    @Test
    @DisplayName("GET /two-player/games/{gameId}/possible-moves - Should return legal moves list")
    void getPossibleMoves_Success() throws Exception {
        String gameId = "game-123";
        List<String> moves = List.of("e3", "e4");
        when(gameService.getLegalMoves(eq(gameId), anyInt(), anyInt())).thenReturn(moves);

        mockMvc.perform(get("/two-player/games/{gameId}/possible-moves", gameId)
                .param("x", "6")
                .param("y", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0]").value("e3"));
    }

    @Test
    @DisplayName("GET /two-player/games/{gameId}/clock - Should return clock info")
    void getClock_Success() throws Exception {
        String gameId = "game-123";
        Clock2DDTO clock = new Clock2DDTO(59000L, 60000L, PlayerColor.WHITE, Instant.now(), false);
        when(gameService.getClock(gameId)).thenReturn(clock);

        mockMvc.perform(get("/two-player/games/{gameId}/clock", gameId))
                .andExpect(status().isOk())
                // Fixed field names below:
                .andExpect(jsonPath("$.whiteRemainingMs").value(59000L)) 
                .andExpect(jsonPath("$.flagged").value(false));
    }
}