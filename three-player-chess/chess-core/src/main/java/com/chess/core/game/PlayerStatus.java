package com.chess.core.game;

public enum PlayerStatus {
    ACTIVE, // Normal play
    TIMEOUT, // Lost on time
    CHECKED, // King is under threat; must move
    STALEMATE, // No legal moves, not in check
    DEFEATED // Checkmated
}