package com.chess.core.dto;

import com.chess.core.game.PlayerColor;

public record PieceDTO(String type, PlayerColor owner) {
}