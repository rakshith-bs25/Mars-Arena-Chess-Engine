package com.chess.twoplayer.dto;

import com.chess.core.game.PlayerColor;
import java.util.List;

public class Game2DDTO {
    private String gameId;
    private PlayerColor currentPlayer;
    private List<TileDTO> board;
    private String status;
    private List<Move2DDTO> moves;
    private long whiteRemainingMs;
    private long blackRemainingMs;

    public Game2DDTO(String gameId, PlayerColor currentPlayer, List<TileDTO> board, String status, 
                     List<Move2DDTO> moves, long whiteRemainingMs, long blackRemainingMs) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
        this.board = board;
        this.status = status;
        this.moves = moves;
        this.whiteRemainingMs = whiteRemainingMs;
        this.blackRemainingMs = blackRemainingMs;
    }

    public String getGameId() { return gameId; }
    public PlayerColor getCurrentPlayer() { return currentPlayer; }
    public List<TileDTO> getBoard() { return board; }
    public String getStatus() { return status; }
    public List<Move2DDTO> getMoves() { return moves; }
    public long getWhiteRemainingMs() { return whiteRemainingMs; }
    public long getBlackRemainingMs() { return blackRemainingMs; }
}