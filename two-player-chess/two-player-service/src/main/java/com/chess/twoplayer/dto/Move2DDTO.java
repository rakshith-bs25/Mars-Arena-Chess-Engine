package com.chess.twoplayer.dto;

import com.chess.core.game.PlayerColor;

public record Move2DDTO(
    int ply,
    PlayerColor player,
    String fromTile, 
    String toTile,   
    String piece,
    long timeTakenMs,
    long remainingTimeMs
) {}