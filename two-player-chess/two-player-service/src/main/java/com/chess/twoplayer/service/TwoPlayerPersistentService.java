package com.chess.twoplayer.service;

import com.chess.core.board.Piece;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.game.PlayerColor;
import com.chess.core.game.StandardGame;
import com.chess.core.rules.TwoPlayerMoveValidator;
import com.chess.twoplayer.dto.Clock2DDTO;
import com.chess.twoplayer.dto.Game2DDTO;
import com.chess.twoplayer.dto.Move2DDTO;
import com.chess.twoplayer.dto.TileDTO;
import com.chess.twoplayer.persistence.Game2DEntity;
import com.chess.twoplayer.persistence.Game2DRepository;
import com.chess.twoplayer.persistence.GameMove2DEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TwoPlayerPersistentService {

    private static final long BLITZ_TIME_SECONDS = 60;
    private final Game2DRepository repository;

    public TwoPlayerPersistentService(Game2DRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Game2DDTO createGame() {
        StandardGame engine = new StandardGame();
        long initialMs = Duration.ofSeconds(BLITZ_TIME_SECONDS).toMillis();
        Game2DEntity entity = new Game2DEntity(engine.getId(), "IN_PROGRESS", engine.getCurrentPlayer(), 
                                               BLITZ_TIME_SECONDS, initialMs, initialMs, Instant.now());
        repository.save(entity);
        return toDto(engine, entity);
    }

    @Transactional
    public Game2DDTO getGameState(String gameId) {
        Game2DEntity entity = repository.findById(gameId).orElseThrow(() -> new RuntimeException("Not Found"));
        checkAndApplyTimeout(entity);
        return toDto(rebuildEngine(entity), entity);
    }

    @Transactional
    public List<String> getLegalMoves(String gameId, int x, int y) {
        Game2DEntity entity = repository.findById(gameId).orElse(null);
        if (entity == null) return Collections.emptyList();
        StandardGame engine = rebuildEngine(entity);
        SquareCoordinate start = SquareCoordinate.of(x, y);
        Piece p = engine.getBoard().getPieceAt(start);
        if (p == null) return Collections.emptyList();

        return p.getValidMoves(engine.getBoard(), start).stream()
                .filter(dest -> TwoPlayerMoveValidator.isMoveLegal(engine.getBoard(), start, dest, p.getOwner()))
                .map(SquareCoordinate::toString)
                .collect(Collectors.toList());
    }

    @Transactional
    public Game2DDTO makeMove(String gameId, int fromX, int fromY, int toX, int toY) {
        Game2DEntity entity = repository.findById(gameId).orElseThrow(() -> new RuntimeException("Not Found"));
        if (checkAndApplyTimeout(entity)) return toDto(rebuildEngine(entity), entity);

        StandardGame engine = rebuildEngine(entity);
        if (!"IN_PROGRESS".equals(entity.getGameStatus())) return toDto(engine, entity);

        Instant now = Instant.now();
        PlayerColor active = entity.getCurrentPlayer();
        long elapsed = Duration.between(entity.getTurnStartedAt(), now).toMillis();
        
        if (active == PlayerColor.WHITE) entity.setWhiteRemainingMs(Math.max(0, entity.getWhiteRemainingMs() - elapsed));
        else entity.setBlackRemainingMs(Math.max(0, entity.getBlackRemainingMs() - elapsed));

        long remaining = (active == PlayerColor.WHITE) ? entity.getWhiteRemainingMs() : entity.getBlackRemainingMs();
        if (remaining <= 0) {
            entity.setGameStatus("TIMEOUT"); // Standard string
            repository.save(entity);
            return toDto(engine, entity);
        }

        Piece movingPiece = engine.getBoard().getPieceAt(SquareCoordinate.of(fromX, fromY));
        String type = (movingPiece != null) ? movingPiece.getType() : "UNKNOWN";

        if (engine.makeMove(fromX, fromY, toX, toY)) {
            entity.getMoves().add(new GameMove2DEntity(entity, entity.getMoves().size(), fromX, fromY, toX, toY, active, type, elapsed, remaining));
            entity.setCurrentPlayer(engine.getCurrentPlayer());
            entity.setGameStatus(engine.getStatus());
            entity.setTurnStartedAt(now);
            repository.save(entity);
        }
        return toDto(engine, entity);
    }

    @Transactional(readOnly = true)
    public Clock2DDTO getClock(String gameId) {
        Game2DEntity entity = repository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        
        // authoritative check
        checkAndApplyTimeout(entity);
        
        long w = entity.getWhiteRemainingMs();
        long b = entity.getBlackRemainingMs();
        
        // Calculate "Live" time for the active player
        if ("IN_PROGRESS".equals(entity.getGameStatus()) && entity.getTurnStartedAt() != null) {
            long elapsed = Duration.between(entity.getTurnStartedAt(), Instant.now()).toMillis();
            if (entity.getCurrentPlayer() == PlayerColor.WHITE) {
                w = Math.max(0, w - elapsed);
            } else if (entity.getCurrentPlayer() == PlayerColor.BLACK) {
                b = Math.max(0, b - elapsed);
            }
        }
        
        return new Clock2DDTO(
            w, 
            b, 
            entity.getCurrentPlayer(), 
            entity.getTurnStartedAt(), 
            (w <= 0 || b <= 0)
        );
    }

    private boolean checkAndApplyTimeout(Game2DEntity entity) {
        if (!"IN_PROGRESS".equals(entity.getGameStatus())) return false;
        long elapsed = Duration.between(entity.getTurnStartedAt(), Instant.now()).toMillis();
        long rem = (entity.getCurrentPlayer() == PlayerColor.WHITE) ? entity.getWhiteRemainingMs() : entity.getBlackRemainingMs();
        if (elapsed >= rem) {
            if (entity.getCurrentPlayer() == PlayerColor.WHITE) entity.setWhiteRemainingMs(0L);
            else entity.setBlackRemainingMs(0L);
            entity.setGameStatus("TIMEOUT"); // Standard string
            repository.save(entity);
            return true;
        }
        return false;
    }

    private StandardGame rebuildEngine(Game2DEntity entity) {
        StandardGame engine = new StandardGame();
        entity.getMoves().stream()
                .sorted(Comparator.comparingInt(GameMove2DEntity::getMoveIndex))
                .forEach(m -> engine.makeMove(m.getFromX(), m.getFromY(), m.getToX(), m.getToY()));
        return engine;
    }

    private Game2DDTO toDto(StandardGame engine, Game2DEntity entity) {
        List<TileDTO> tiles = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = engine.getBoard().getPieceAt(SquareCoordinate.of(x, y));
                if (p != null) tiles.add(new TileDTO(x, y, p.getType(), p.getOwner().name()));
            }
        }
        List<Move2DDTO> moves = entity.getMoves().stream()
                .map(m -> new Move2DDTO(
                    m.getMoveIndex(), 
                    m.getPlayer(), 
                    m.getFromX() + "," + m.getFromY(), 
                    m.getToX() + "," + m.getToY(), 
                    m.getPiece(), 
                    m.getTimeTakenMs(), 
                    m.getRemainingTimeMs()
                ))
                .collect(Collectors.toList());

        return new Game2DDTO(entity.getId(), entity.getCurrentPlayer(), tiles, entity.getGameStatus(), moves, entity.getWhiteRemainingMs(), entity.getBlackRemainingMs());
    }
}