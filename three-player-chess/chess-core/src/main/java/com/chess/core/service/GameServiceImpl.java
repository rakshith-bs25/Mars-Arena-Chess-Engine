package com.chess.core.service;

import com.chess.core.board.CoordinateMapper;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.dto.GameStateDTO;
import com.chess.core.dto.TileDTO;
import com.chess.core.game.PlayerColor;
import com.chess.core.game.ThreePlayerGame;
import com.chess.core.rules.MoveValidator;
import com.chess.core.game.GameStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameServiceImpl implements GameService {

    private static final Map<String, ThreePlayerGame> gameRepository = new HashMap<>();

    @Override
    public GameStateDTO createGame() {
        ThreePlayerGame game = new ThreePlayerGame();
        gameRepository.put(game.getId(), game);
        return convertToDTO(game);
    }

    @Override
    public GameStateDTO getGameState(String gameId) {
        ThreePlayerGame game = gameRepository.get(gameId);
        if (game == null)
            throw new IllegalArgumentException("Game not found: " + gameId);
        return convertToDTO(game);
    }

    @Override
    public GameStateDTO makeMove(String gameId, String fromTile, String toTile, PlayerColor player) {
        ThreePlayerGame game = gameRepository.get(gameId);
        if (game == null)
            throw new IllegalArgumentException("Game not found: " + gameId);

        // 1. Move the piece
        boolean moveSuccess = game.makeMove(fromTile, toTile);

        if (moveSuccess) {
            PlayerColor victim = game.getCurrentPlayer();
            HexBoard board = (HexBoard) game.getBoard();

            // 1. Get the current status of the king
            boolean inCheck = com.chess.core.rules.BoardAnalyzer.isKingInCheck(board, victim);
            boolean hasMoves = com.chess.core.rules.MoveValidator.hasAnyValidMoves(board, victim);

            if (!hasMoves) {
                if (inCheck) {
                    PlayerColor winner = getPreviousPlayer(victim);
                    handleGameOver(game, winner);
                } else {
                    handleStalemate(game);
                }
            }
        }

        return convertToDTO(game);
    }

    @Override
    public List<String> getLegalMoves(String gameId, String tileId) {
        ThreePlayerGame game = gameRepository.get(gameId);
        if (game == null)
            return Collections.emptyList();

        HexBoard board = (HexBoard) game.getBoard();
        HexCoordinate start = CoordinateMapper.toHex(tileId);
        if (start == null)
            return Collections.emptyList();

        Piece piece = board.getPieceAt(start);
        if (piece == null)
            return Collections.emptyList();

        List<HexCoordinate> candidates = piece.getValidMoves(board, start);
        return candidates.stream()
                .filter(dest -> MoveValidator.isMoveLegal(board, start, dest, piece.getOwner()))
                .map(CoordinateMapper::toId)
                .collect(Collectors.toList());
    }

    private GameStateDTO convertToDTO(ThreePlayerGame game) {
        HexBoard hexBoard = (HexBoard) game.getBoard();
        List<TileDTO> tiles = new ArrayList<>();

        for (HexCoordinate coord : hexBoard.getPlayableCoordinates()) {
            Piece piece = hexBoard.getPieceAt(coord);
            String coordinate = CoordinateMapper.toId(coord);
            String pieceType = (piece != null) ? piece.getType() : null;
            PlayerColor owner = (piece != null) ? piece.getOwner() : null;
            tiles.add(new TileDTO(coordinate, pieceType, owner));
        }

        return new GameStateDTO(
                game.getId(),
                game.getGameStatus(),
                game.getCurrentPlayer(),
                game.getWinner(),
                tiles);
    }

    private void handleGameOver(ThreePlayerGame game, PlayerColor winner) {
        game.setStatus(GameStatus.FINISHED);
        game.setWinner(winner);
    }

    private void handleStalemate(ThreePlayerGame game) {
        game.setStatus(GameStatus.STALEMATE);
        System.out.println("LOG: Game ended in a Stalemate.");
        game.setWinner(null);
    }

    private PlayerColor getPreviousPlayer(PlayerColor current) {
        if (current == PlayerColor.WHITE)
            return PlayerColor.BLACK;
        if (current == PlayerColor.RED)
            return PlayerColor.WHITE;
        return PlayerColor.RED;
    }

}