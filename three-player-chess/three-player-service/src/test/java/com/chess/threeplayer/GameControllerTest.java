package com.chess.threeplayer;

import com.chess.core.dto.GameStateDTO;
import com.chess.core.game.GameStatus;
import com.chess.core.game.PlayerColor;
import com.chess.threeplayer.controller.GameController;
import com.chess.threeplayer.dto.MoveRequest;
import com.chess.threeplayer.service.PersistentGameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

   
    @MockBean
    private PersistentGameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /three-player/games - Should create game and return 200")
    void createGame_Success() throws Exception {
        GameStateDTO mockGame = new GameStateDTO("game-123", GameStatus.IN_PROGRESS, PlayerColor.WHITE, null, Collections.emptyList());
        when(gameService.createGame()).thenReturn(mockGame);

        mockMvc.perform(post("/three-player/games")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("game-123"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("GET /three-player/games/{gameId} - Should return game state")
    void getGame_Success() throws Exception {
        String gameId = "game-123";
        GameStateDTO mockGame = new GameStateDTO(gameId, GameStatus.IN_PROGRESS, PlayerColor.WHITE, null, Collections.emptyList());
        when(gameService.getGameState(gameId)).thenReturn(mockGame);

        mockMvc.perform(get("/three-player/games/{gameId}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId));
    }

    @Test
    @DisplayName("POST /three-player/games/{gameId}/move - Should execute move")
    void makeMove_Success() throws Exception {
        String gameId = "game-123";
        
        MoveRequest req = new MoveRequest();
        req.setFrom("0,6");
        req.setTo("0,5");
        req.setPlayer(PlayerColor.WHITE);

        GameStateDTO updatedGame = new GameStateDTO(gameId, GameStatus.IN_PROGRESS, PlayerColor.RED, null, Collections.emptyList());
        
        when(gameService.makeMove(eq(gameId), eq("0,6"), eq("0,5"), eq(PlayerColor.WHITE))).thenReturn(updatedGame);

        mockMvc.perform(post("/three-player/games/{gameId}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPlayer").value("RED"));
    }

    @Test
    @DisplayName("GET /three-player/games/{gameId}/possible-moves - Should return legal moves list")
    void getPossibleMoves_Success() throws Exception {
        String gameId = "game-123";
        List<String> moves = List.of("0,5", "1,5");
        when(gameService.getLegalMoves(eq(gameId), eq("0,6"))).thenReturn(moves);

        mockMvc.perform(get("/three-player/games/{gameId}/possible-moves", gameId)
                .param("from", "0,6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0]").value("0,5"));
    }
}