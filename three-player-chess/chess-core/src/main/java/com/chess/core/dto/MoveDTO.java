package com.chess.core.dto;

import com.chess.core.game.PlayerColor;

public record MoveDTO(
        int ply,
        PlayerColor player,
        String fromTile,
        String toTile,
        String piece,
        long timeTakenMs,
        long remainingTimeMs) {
}
