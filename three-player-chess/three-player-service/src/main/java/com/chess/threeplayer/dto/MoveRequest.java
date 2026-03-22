package com.chess.threeplayer.dto;

import com.chess.core.game.PlayerColor;

public class MoveRequest {

    private String from;
    private String to;
    private PlayerColor player;

    public MoveRequest() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public PlayerColor getPlayer() {
        return player;
    }

    public void setPlayer(PlayerColor player) {
        this.player = player;
    }
}