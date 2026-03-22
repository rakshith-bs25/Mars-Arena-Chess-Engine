package com.chess.threeplayer.service;

import com.chess.core.board.CoordinateMapper;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.dto.GameStateDTO;
import com.chess.core.dto.TileDTO;
import com.chess.core.game.GameStatus;
import com.chess.core.game.PlayerColor;
import com.chess.core.game.PlayerStatus;
import com.chess.core.game.ThreePlayerGame;
import com.chess.core.rules.MoveValidator;
import com.chess.core.service.GameService;
import com.chess.threeplayer.dto.ClockDTO;
import com.chess.core.dto.MoveDTO;
import com.chess.threeplayer.persistence.GameEntity;
import com.chess.threeplayer.persistence.GameMoveEntity;
import com.chess.threeplayer.persistence.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersistentGameService implements GameService {

    private static final long DEFAULT_TIME_CONTROL_SECONDS = 60;
    private static final long MAGE_BONUS_MS = 5000;

    private final GameRepository repository;

    public PersistentGameService(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public GameStateDTO createGame() {
        ThreePlayerGame game = new ThreePlayerGame();
        long initialMs = Duration.ofSeconds(DEFAULT_TIME_CONTROL_SECONDS).toMillis();
        Instant now = Instant.now();

        GameEntity entity = new GameEntity(
                game.getId(),
                game.getGameStatus(),
                game.getCurrentPlayer(),
                DEFAULT_TIME_CONTROL_SECONDS,
                initialMs,
                initialMs,
                initialMs,
                now);

        repository.save(entity);
        return toDto(game, entity);
    }

    @Override
    @Transactional
    public GameStateDTO getGameState(String gameId) {
        GameEntity entity = repository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        checkAndApplyTimeout(entity);

        ThreePlayerGame game = rebuildGame(entity);
        return toDto(game, entity);
    }

    @Override
    @Transactional
    public GameStateDTO makeMove(String gameId, String fromTile, String toTile, PlayerColor player) {
        GameEntity entity = repository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        if (checkAndApplyTimeout(entity)) {
            ThreePlayerGame g = rebuildGame(entity);
            g.forceCurrentPlayer(entity.getCurrentPlayer()); 
            return toDto(g, entity);
        }

        ThreePlayerGame game = rebuildGame(entity);
        game.forceCurrentPlayer(entity.getCurrentPlayer());

        if (entity.getGameStatus() != GameStatus.IN_PROGRESS) {
            return toDto(game, entity);
        }

        if (player != null && player != entity.getCurrentPlayer()) {
            return toDto(game, entity);
        }

        Instant now = Instant.now();
        PlayerColor activeColor = entity.getCurrentPlayer();

        long timeElapsedSinceLastTurn = Math.max(0L, Duration.between(entity.getTurnStartedAt(), now).toMillis());
        long remainingTimeAfterTurn = calculateNewRemainingTime(entity, activeColor, timeElapsedSinceLastTurn, game, fromTile, toTile);

        setRemainingMs(entity, activeColor, remainingTimeAfterTurn);

        if (remainingTimeAfterTurn <= 0) {
            handlePlayerTimeout(entity, activeColor, now, game);
            ThreePlayerGame rotated = rebuildGame(entity);
            rotated.forceCurrentPlayer(entity.getCurrentPlayer());
            return toDto(rotated, entity);
        }

        String pieceLabel = resolvePieceString(game, fromTile);
        boolean moveExecutionSuccessful = game.makeMove(fromTile, toTile);
        if (!moveExecutionSuccessful) {
            repository.save(entity);
            return toDto(game, entity);
        }

        recordMoveAndRotateTurn(entity, fromTile, toTile, activeColor, pieceLabel, timeElapsedSinceLastTurn, remainingTimeAfterTurn, now, game);
        return toDto(game, entity);
    }

    private long calculateNewRemainingTime(GameEntity entity, PlayerColor color, long elapsed, ThreePlayerGame game, String from, String to) {
        long currentRemaining = remainingMs(entity, color);
        long newRemaining = currentRemaining - elapsed;

        HexBoard board = (HexBoard) game.getBoard();
        Piece attacker = board.getPieceAt(CoordinateMapper.toHex(from));
        Piece target = board.getPieceAt(CoordinateMapper.toHex(to));

        if (attacker != null && attacker.getPieceType() == PieceType.MAGE && target != null) {
            newRemaining += MAGE_BONUS_MS;
        }
        return newRemaining;
    }

    private void handlePlayerTimeout(GameEntity entity, PlayerColor active, Instant now, ThreePlayerGame game) {
        setRemainingMs(entity, active, 0L);
        entity.setStatus(active, PlayerStatus.TIMEOUT);
        if (activePlayerCount(entity) < 2) {
            entity.setGameStatus(GameStatus.FINISHED);
            entity.setWinner(getTimeoutWinner(entity));
        } else {
            PlayerColor engineNext = game.getCurrentPlayer();
            PlayerColor nextActive = nextActivePlayer(entity, engineNext);
            entity.setCurrentPlayer(nextActive);
        }
        entity.setTurnStartedAt(now);
        repository.save(entity);
    }

    private void recordMoveAndRotateTurn(GameEntity entity, String fromTile, String toTile, PlayerColor active, String piece, long elapsed, long remaining, Instant now, ThreePlayerGame game) {
        int[] fromCoords = parseTile(fromTile);
        int[] toCoords = parseTile(toTile);

        GameMoveEntity moveRecord = new GameMoveEntity(
                entity, entity.getMoves().size(), fromCoords[0], fromCoords[1], toCoords[0], toCoords[1],
                active, piece, elapsed, Math.max(0L, remaining));
        entity.getMoves().add(moveRecord);

        PlayerColor nextActive = nextActivePlayer(entity, game.getCurrentPlayer());
        entity.setCurrentPlayer(nextActive);
        entity.setGameStatus(game.getGameStatus());
        entity.setTurnStartedAt(now);
        repository.save(entity);
    }

    private boolean checkAndApplyTimeout(GameEntity entity) {
        if (entity.getGameStatus() != GameStatus.IN_PROGRESS) return false;

        Instant now = Instant.now();
        if (activePlayerCount(entity) < 2) {
            entity.setGameStatus(GameStatus.FINISHED);
            entity.setWinner(getTimeoutWinner(entity));
            repository.save(entity);
            return true;
        }

        PlayerColor active = entity.getCurrentPlayer();
        if (entity.statusOf(active) != PlayerStatus.ACTIVE) {
            entity.setCurrentPlayer(nextActivePlayer(entity, active));
            entity.setTurnStartedAt(now);
            repository.save(entity);
            return true;
        }

        long elapsed = Math.max(0L, Duration.between(entity.getTurnStartedAt(), now).toMillis());
        if (elapsed < remainingMs(entity, active)) return false;

        setRemainingMs(entity, active, 0L);
        entity.setStatus(active, PlayerStatus.TIMEOUT);

        if (activePlayerCount(entity) < 2) {
            entity.setGameStatus(GameStatus.FINISHED);
            entity.setWinner(getTimeoutWinner(entity));
        } else {
            entity.setCurrentPlayer(nextActivePlayer(entity, nextColor(active)));
        }

        entity.setTurnStartedAt(now);
        repository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public List<String> getLegalMoves(String gameId, String tileId) {
        Optional<GameEntity> entityOpt = repository.findById(gameId);
        if (entityOpt.isEmpty()) return Collections.emptyList();

        GameEntity entity = entityOpt.get();
        checkAndApplyTimeout(entity); 

        ThreePlayerGame game = rebuildGame(entity);
        HexBoard board = (HexBoard) game.getBoard();
        HexCoordinate start = CoordinateMapper.toHex(tileId);
        
        if (start == null || board.getPieceAt(start) == null) return Collections.emptyList();

        Piece piece = board.getPieceAt(start);
        return piece.getValidMoves(board, start).stream()
                .filter(dest -> MoveValidator.isMoveLegal(board, start, dest, piece.getOwner()))
                .map(CoordinateMapper::toId)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClockDTO getClock(String gameId) {
        GameEntity entity = repository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        checkAndApplyTimeout(entity);
        return computeClock(entity, Instant.now());
    }

    private ThreePlayerGame rebuildGame(GameEntity entity) {
        ThreePlayerGame game = new ThreePlayerGame(entity.getId());
        List<GameMoveEntity> ordered = entity.getMoves().stream()
                .sorted(Comparator.comparingInt(GameMoveEntity::getMoveIndex))
                .collect(Collectors.toList());

        for (GameMoveEntity move : ordered) {
            game.forceCurrentPlayer(move.getPlayer());
            game.makeMove(move.getFromX() + "," + move.getFromY(), move.getToX() + "," + move.getToY());
        }
        game.forceCurrentPlayer(entity.getCurrentPlayer());
        return game;
    }

    private static String resolvePieceString(ThreePlayerGame game, String fromTile) {
        HexBoard board = (HexBoard) game.getBoard();
        Piece piece = board.getPieceAt(CoordinateMapper.toHex(fromTile));
        return (piece != null) ? piece.getType() : "UNKNOWN";
    }

    private static GameStateDTO toDto(ThreePlayerGame game, GameEntity entity) {
        HexBoard hexBoard = (HexBoard) game.getBoard();
        List<TileDTO> tiles = new ArrayList<>();

        for (HexCoordinate coord : hexBoard.getPlayableCoordinates()) {
            Piece piece = hexBoard.getPieceAt(coord);
            tiles.add(new TileDTO(CoordinateMapper.toId(coord), (piece != null) ? piece.getType() : null, (piece != null) ? piece.getOwner() : null));
        }

        List<MoveDTO> moves = entity.getMoves().stream()
                .sorted(Comparator.comparingInt(GameMoveEntity::getMoveIndex))
                .map(m -> new MoveDTO(m.getMoveIndex(), m.getPlayer(), m.getFromX() + "," + m.getFromY(), m.getToX() + "," + m.getToY(), m.getPiece(), m.getTimeTakenMs(), m.getRemainingTimeMs()))
                .collect(Collectors.toList());

        return new GameStateDTO(entity.getId(), game.getGameStatus(), entity.getCurrentPlayer(), game.getWinner(), tiles, moves, entity.statusOf(PlayerColor.WHITE), entity.statusOf(PlayerColor.BLACK), entity.statusOf(PlayerColor.RED));
    }

    private static PlayerColor nextColor(PlayerColor c) {
        return switch (c) {
            case WHITE -> PlayerColor.RED;
            case BLACK -> PlayerColor.WHITE;
            case RED -> PlayerColor.BLACK;
        };
    }

    private static PlayerColor nextActivePlayer(GameEntity entity, PlayerColor from) {
        PlayerColor candidate = from;
        for (int i = 0; i < 3; i++) {
            if (entity.statusOf(candidate) == PlayerStatus.ACTIVE) return candidate;
            candidate = nextColor(candidate);
        }
        return from;
    }

    private static long remainingMs(GameEntity entity, PlayerColor player) {
        return switch (player) {
            case WHITE -> entity.getWhiteRemainingMs();
            case BLACK -> entity.getBlackRemainingMs();
            case RED -> entity.getRedRemainingMs();
        };
    }

    private static void setRemainingMs(GameEntity entity, PlayerColor player, long valueMs) {
        switch (player) {
            case WHITE -> entity.setWhiteRemainingMs(valueMs);
            case BLACK -> entity.setBlackRemainingMs(valueMs);
            case RED -> entity.setRedRemainingMs(valueMs);
        }
    }

    private static int[] parseTile(String tile) {
        String[] parts = tile.split(",");
        return new int[] { Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()) };
    }

    private ClockDTO computeClock(GameEntity entity, Instant now) {
        long w = entity.getWhiteRemainingMs();
        long b = entity.getBlackRemainingMs();
        long r = entity.getRedRemainingMs();

        if (entity.getGameStatus() == GameStatus.IN_PROGRESS) {
            long elapsed = Math.max(0L, Duration.between(entity.getTurnStartedAt(), now).toMillis());
            switch (entity.getCurrentPlayer()) {
                case WHITE -> w -= elapsed;
                case BLACK -> b -= elapsed;
                case RED -> r -= elapsed;
            }
        }
        return new ClockDTO(Math.max(0L, w), Math.max(0L, b), Math.max(0L, r), entity.getCurrentPlayer(), entity.getTurnStartedAt(), (w <= 0 || b <= 0 || r <= 0), entity.getGameStatus(), entity.getWinner());
    }

    private static int activePlayerCount(GameEntity entity) {
        int count = 0;
        if (entity.statusOf(PlayerColor.WHITE) == PlayerStatus.ACTIVE) count++;
        if (entity.statusOf(PlayerColor.BLACK) == PlayerStatus.ACTIVE) count++;
        if (entity.statusOf(PlayerColor.RED) == PlayerStatus.ACTIVE) count++;
        return count;
    }

    private static PlayerColor getTimeoutWinner(GameEntity entity) {
        if (entity.statusOf(PlayerColor.WHITE) == PlayerStatus.ACTIVE) return PlayerColor.WHITE;
        if (entity.statusOf(PlayerColor.BLACK) == PlayerStatus.ACTIVE) return PlayerColor.BLACK;
        if (entity.statusOf(PlayerColor.RED) == PlayerStatus.ACTIVE) return PlayerColor.RED;
        return null;
    }
}