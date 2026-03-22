package com.chess.threeplayer.controller;

import com.chess.core.dto.GameStateDTO;
import com.chess.core.service.GameService;
import com.chess.threeplayer.dto.ClockDTO;
import com.chess.threeplayer.dto.MoveRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/three-player/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public GameStateDTO createGame() {
        return gameService.createGame();
    }

    @GetMapping("/{gameId}")
    public GameStateDTO getGameState(@PathVariable String gameId) {
        return gameService.getGameState(gameId);
    }

    @PostMapping("/{gameId}/move")
    public GameStateDTO makeMove(@PathVariable String gameId, @RequestBody MoveRequest move) {
        return gameService.makeMove(gameId, move.getFrom(), move.getTo(), move.getPlayer());
    }

    @GetMapping("/{gameId}/possible-moves")
    public List<String> getLegalMoves(@PathVariable String gameId, @RequestParam String from) {
        return gameService.getLegalMoves(gameId, from);
    }

    @GetMapping("/{gameId}/clock")
    public ClockDTO getClock(@PathVariable String gameId) {
        if (!(gameService instanceof com.chess.threeplayer.service.PersistentGameService persistent)) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                    "Clock is only available with the persistent service");
        }
        return persistent.getClock(gameId);
    }
}