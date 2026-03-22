package com.chess.twoplayer.dto;

import com.chess.core.game.PlayerColor;
import java.time.Instant;

// Ensure these field names match the 3-player version for frontend component compatibility
public record Clock2DDTO(
    long whiteRemainingMs,
    long blackRemainingMs,
    PlayerColor activePlayer,
    Instant turnStartedAt,
    boolean flagged
) {}