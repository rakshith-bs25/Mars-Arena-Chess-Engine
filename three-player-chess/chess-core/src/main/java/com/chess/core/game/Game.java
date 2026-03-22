package com.chess.core.game;

import com.chess.core.board.Board;

/**
 * The primary API facade for the Chess Core.
 * Spring Boot interact with this interface.
 */
public interface Game {
    String getId();
    Board getBoard();
    GameStatus getGameStatus();
    PlayerColor getCurrentPlayer();
    PlayerStatus getPlayerStatus(PlayerColor player);
}