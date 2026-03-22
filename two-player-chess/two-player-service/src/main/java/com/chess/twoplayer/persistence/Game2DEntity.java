package com.chess.twoplayer.persistence;

import com.chess.core.game.PlayerColor;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games_2d")
public class Game2DEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "game_status", nullable = false)
    private String gameStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_player", nullable = false)
    private PlayerColor currentPlayer;

    @Column(name = "time_control_seconds", nullable = false)
    private long timeControlSeconds;

    @Column(name = "white_remaining_ms", nullable = false)
    private long whiteRemainingMs;

    @Column(name = "black_remaining_ms", nullable = false)
    private long blackRemainingMs;

    @Column(name = "turn_started_at", nullable = false)
    private Instant turnStartedAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("moveIndex ASC")
    private List<GameMove2DEntity> moves = new ArrayList<>();

    protected Game2DEntity() {
    }

    public Game2DEntity(String id, String gameStatus, PlayerColor currentPlayer, long timeControlSeconds,
            long whiteRemainingMs, long blackRemainingMs, Instant turnStartedAt) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.currentPlayer = currentPlayer;
        this.timeControlSeconds = timeControlSeconds;
        this.whiteRemainingMs = whiteRemainingMs;
        this.blackRemainingMs = blackRemainingMs;
        this.turnStartedAt = turnStartedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerColor currentPlayer) {
        this.currentPlayer = currentPlayer;
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

    public Instant getTurnStartedAt() {
        return turnStartedAt;
    }

    public void setTurnStartedAt(Instant turnStartedAt) {
        this.turnStartedAt = turnStartedAt;
    }

    public List<GameMove2DEntity> getMoves() {
        return moves;
    }
}