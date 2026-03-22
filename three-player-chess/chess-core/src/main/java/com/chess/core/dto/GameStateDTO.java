package com.chess.core.dto;

import com.chess.core.game.GameStatus;
import com.chess.core.game.PlayerColor;
import com.chess.core.game.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTO {

    private String gameId;
    private GameStatus status;
    private PlayerColor currentPlayer;
    private PlayerColor winner;
    private List<TileDTO> board;
    private PlayerStatus whiteStatus;
    private PlayerStatus blackStatus;
    private PlayerStatus redStatus;

    // Not final, so no-args constructor works for Jackson
    private List<MoveDTO> moves;

    public GameStateDTO() {
        this.moves = new ArrayList<>();
    }

    public GameStateDTO(String gameId,
            GameStatus status,
            PlayerColor currentPlayer,
            PlayerColor winner,
            List<TileDTO> board,
            List<MoveDTO> moves, PlayerStatus whiteStatus, PlayerStatus blackStatus, PlayerStatus redStatus) {
        this.gameId = gameId;
        this.status = status;
        this.currentPlayer = currentPlayer;
        this.winner = winner;
        this.board = board;
        this.moves = (moves != null) ? moves : new ArrayList<>();
        this.whiteStatus = whiteStatus;
        this.blackStatus = blackStatus;
        this.redStatus = redStatus;
    }

    public GameStateDTO(String id,
            GameStatus gameStatus,
            PlayerColor currentPlayer,
            PlayerColor winner,
            List<TileDTO> tiles) {
        this(id, gameStatus, currentPlayer, winner, tiles, List.of(), null, null, null);
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerColor currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<TileDTO> getBoard() {
        return board;
    }

    public PlayerStatus getWhiteStatus() {
        return this.whiteStatus;
    }

    public PlayerStatus getRedStatus() {
        return this.redStatus;
    }

    public PlayerStatus getBlackStatus() {
        return this.blackStatus;
    }

    public void setBoard(List<TileDTO> board) {
        this.board = board;
    }

    public PlayerColor getWinner() {
        return winner;
    }

    public void setWinner(PlayerColor winner) {
        this.winner = winner;
    }

    public List<MoveDTO> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveDTO> moves) {
        this.moves = (moves != null) ? moves : new ArrayList<>();
    }

    public void setBlackStatus(PlayerStatus blackStatus) {
        this.blackStatus = blackStatus;
    }

    public void setWhiteStatus(PlayerStatus whiteStatus) {
        this.whiteStatus = whiteStatus;
    }

    public void setRedStatus(PlayerStatus redStatus) {
        this.redStatus = redStatus;
    }
}
