package com.chess.core.service;

import com.chess.core.dto.GameStateDTO;
import com.chess.core.game.PlayerColor;
import java.util.List;

public interface GameService {
    GameStateDTO createGame();

    GameStateDTO getGameState(String gameId);

    GameStateDTO makeMove(String gameId, String fromTile, String toTile, PlayerColor player);

    List<String> getLegalMoves(String gameId, String tileId);
}