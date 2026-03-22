package com.chess.twoplayer.controller;

import com.chess.twoplayer.dto.Clock2DDTO;
import com.chess.twoplayer.dto.Game2DDTO;
import com.chess.twoplayer.dto.MoveRequest;
import com.chess.twoplayer.service.TwoPlayerPersistentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/two-player/games")
public class TwoPlayerGameController {

    private final TwoPlayerPersistentService gameService;

    public TwoPlayerGameController(TwoPlayerPersistentService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public Game2DDTO createGame() {
        return gameService.createGame();
    }

    @GetMapping("/{gameId}")
    public Game2DDTO getGame(@PathVariable String gameId) {
        return gameService.getGameState(gameId);
    }

    @PostMapping("/{gameId}/move")
    public Game2DDTO makeMove(@PathVariable String gameId, @RequestBody MoveRequest req) {
        return gameService.makeMove(gameId, req.getFromX(), req.getFromY(), req.getToX(), req.getToY());
    }

    @GetMapping("/{gameId}/possible-moves")
    public List<String> getPossibleMoves(@PathVariable String gameId, @RequestParam int x, @RequestParam int y) {
        return gameService.getLegalMoves(gameId, x, y);
    }

    @GetMapping("/{gameId}/clock")
    public Clock2DDTO getClock(@PathVariable String gameId) {
        return gameService.getClock(gameId);
    }
}