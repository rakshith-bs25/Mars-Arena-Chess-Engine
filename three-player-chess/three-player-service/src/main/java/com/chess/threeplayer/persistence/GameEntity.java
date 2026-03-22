package com.chess.threeplayer.persistence;

import com.chess.core.game.GameStatus;
import com.chess.core.game.PlayerColor;
import com.chess.core.game.PlayerStatus;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status", nullable = false)
    private GameStatus gameStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_player", nullable = false)
    private PlayerColor currentPlayer;

    @Enumerated(EnumType.STRING)
    @Column(name = "white_status", nullable = false)
    private PlayerStatus whiteStatus = PlayerStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "black_status", nullable = false)
    private PlayerStatus blackStatus = PlayerStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "red_status", nullable = false)
    private PlayerStatus redStatus = PlayerStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "winner", nullable = true)
    private PlayerColor winner = null;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("moveIndex ASC")
    private List<GameMoveEntity> moves = new ArrayList<>();

    @Column(name = "time_control_seconds", nullable = false)
    private long timeControlSeconds;

    @Column(name = "white_remaining_ms", nullable = false)
    private long whiteRemainingMs;

    @Column(name = "black_remaining_ms", nullable = false)
    private long blackRemainingMs;

    @Column(name = "red_remaining_ms", nullable = false)
    private long redRemainingMs;

    @Column(name = "turn_started_at", nullable = false)
    private Instant turnStartedAt;

    protected GameEntity() {
        // for JPA
    }

    public GameEntity(
            String id,
            GameStatus gameStatus,
            PlayerColor currentPlayer,
            long timeControlSeconds,
            long whiteRemainingMs,
            long blackRemainingMs,
            long redRemainingMs,
            Instant turnStartedAt) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.currentPlayer = currentPlayer;
        this.timeControlSeconds = timeControlSeconds;
        this.whiteRemainingMs = whiteRemainingMs;
        this.blackRemainingMs = blackRemainingMs;
        this.redRemainingMs = redRemainingMs;
        this.turnStartedAt = turnStartedAt;
    }

    public String getId() {
        return id;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerColor currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<GameMoveEntity> getMoves() {
        return moves;
    }

    public long getTimeControlSeconds() {
        return timeControlSeconds;
    }

    public long getWhiteRemainingMs() {
        return whiteRemainingMs;
    }

    public void setWhiteRemainingMs(long whiteRemainingMs) {
        this.whiteRemainingMs = whiteRemainingMs;
    }

    public long getBlackRemainingMs() {
        return blackRemainingMs;
    }

    public void setBlackRemainingMs(long blackRemainingMs) {
        this.blackRemainingMs = blackRemainingMs;
    }

    public long getRedRemainingMs() {
        return redRemainingMs;
    }

    public void setRedRemainingMs(long redRemainingMs) {
        this.redRemainingMs = redRemainingMs;
    }

    public Instant getTurnStartedAt() {
        return turnStartedAt;
    }

    public PlayerColor getWinner() {
        return winner;
    }

    public void setWinner(PlayerColor winner) {
        this.winner = winner;
    }

    public void setTurnStartedAt(Instant turnStartedAt) {
        this.turnStartedAt = turnStartedAt;
    }

    public PlayerStatus statusOf(PlayerColor c) {
        switch (c) {
            case WHITE:
                return whiteStatus;
            case BLACK:
                return blackStatus;
            case RED:
                return redStatus;
            default:
                return whiteStatus;
        }
    }

    public void setStatus(PlayerColor c, PlayerStatus s) {
        switch (c) {
            case WHITE:
                this.whiteStatus = s;
                break;
            case BLACK:
                this.blackStatus = s;
                break;
            case RED:
                this.redStatus = s;
                break;
            default:
                this.whiteStatus = s;
                break;
        }
    }

}
