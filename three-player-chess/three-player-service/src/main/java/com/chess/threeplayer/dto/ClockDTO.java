package com.chess.threeplayer.dto;

import com.chess.core.game.PlayerColor;
import com.chess.core.game.GameStatus;

import java.time.Instant;

public record ClockDTO(
                long whiteRemainingMs,
                long blackRemainingMs,
                long redRemainingMs,
                PlayerColor activePlayer,
                Instant turnStartedAt,
                boolean flagged,
                GameStatus gameStatus,
                PlayerColor winner) {
}
