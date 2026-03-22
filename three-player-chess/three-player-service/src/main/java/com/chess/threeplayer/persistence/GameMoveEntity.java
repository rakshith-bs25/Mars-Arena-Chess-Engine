package com.chess.threeplayer.persistence;

import com.chess.core.game.PlayerColor;
import jakarta.persistence.*;

@Entity
@Table(name = "game_moves")
public class GameMoveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(name = "move_index", nullable = false)
    private int moveIndex;

    @Column(name = "from_x", nullable = false)
    private int fromX;

    @Column(name = "from_y", nullable = false)
    private int fromY;

    @Column(name = "to_x", nullable = false)
    private int toX;

    @Column(name = "to_y", nullable = false)
    private int toY;

    @Enumerated(EnumType.STRING)
    @Column(name = "player", nullable = false)
    private PlayerColor player;

    @Column(name = "piece", nullable = false)
    private String piece;

    @Column(name = "time_taken_ms", nullable = false)
    private long timeTakenMs;

    @Column(name = "remaining_time_ms", nullable = false)
    private long remainingTimeMs;

    protected GameMoveEntity() {
    }

    public GameMoveEntity(GameEntity game,
            int moveIndex,
            int fromX,
            int fromY,
            int toX,
            int toY,
            PlayerColor player,
            String piece,
            long timeTakenMs,
            long remainingTimeMs) {
        this.game = game;
        this.moveIndex = moveIndex;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.player = player;
        this.piece = piece;
        this.timeTakenMs = timeTakenMs;
        this.remainingTimeMs = remainingTimeMs;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    public PlayerColor getPlayer() {
        return player;
    }

    public String getPiece() {
        return piece;
    }

    public long getTimeTakenMs() {
        return timeTakenMs;
    }

    public long getRemainingTimeMs() {
        return remainingTimeMs;
    }

}
