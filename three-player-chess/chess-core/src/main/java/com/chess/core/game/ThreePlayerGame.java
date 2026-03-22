package com.chess.core.game;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.CoordinateMapper;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.board.pieces.*;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.rules.BoardAnalyzer;
import com.chess.core.rules.MoveValidator;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class ThreePlayerGame implements Game {
    private final String gameId;
    private final HexBoard board;
    private PlayerColor currentPlayer;
    private GameStatus gameStatus;
    private PlayerColor winner = null;
    private final Map<PlayerColor, PlayerStatus> playerStatuses;

    public ThreePlayerGame() {
        this(UUID.randomUUID().toString());
    }

    public ThreePlayerGame(String gameId) {
        this.gameId = (gameId == null || gameId.isBlank()) ? UUID.randomUUID().toString() : gameId;
        this.board = new HexBoard();
        initializeBoard();
        this.currentPlayer = PlayerColor.WHITE;
        this.gameStatus = GameStatus.IN_PROGRESS;

        this.playerStatuses = new EnumMap<>(PlayerColor.class);
        for (PlayerColor color : PlayerColor.values()) {
            playerStatuses.put(color, PlayerStatus.ACTIVE);
        }
    }

    public void nextTurn() {
        // 1. Move to the next player
        this.currentPlayer = nextPlayer(this.currentPlayer);

        // 2. Check if the NEW current player is checkmated or stalemated
        updateStatusAndCheckGameOver(this.currentPlayer);
    }

    private void updateStatusAndCheckGameOver(PlayerColor player) {
        boolean inCheck = BoardAnalyzer.isKingInCheck(board, player);
        boolean hasMoves = hasAnyLegalMoves(player);

        if (!hasMoves) {
            if (inCheck) {
                // MUST set the status so the UI/Test can see it
                playerStatuses.put(player, PlayerStatus.DEFEATED);
                this.gameStatus = GameStatus.FINISHED;
                this.winner = getPreviousPlayer(player);
            } else {
                playerStatuses.put(player, PlayerStatus.STALEMATE);
                this.gameStatus = GameStatus.STALEMATE;
                this.winner = null;
            }
        } else if (inCheck) {
            playerStatuses.put(player, PlayerStatus.CHECKED);
        } else {
            playerStatuses.put(player, PlayerStatus.ACTIVE);
        }
    }

    private PlayerColor nextPlayer(PlayerColor current) {
        switch (current) {
            case WHITE:
                return PlayerColor.RED;
            case BLACK:
                return PlayerColor.WHITE;
            case RED:
                return PlayerColor.BLACK;
            default:
                return PlayerColor.WHITE;
        }
    }

    public boolean makeMove(String fromTile, String toTile) {
        try {
            HexCoordinate from = CoordinateMapper.toHex(fromTile);
            HexCoordinate to = CoordinateMapper.toHex(toTile);
            if (from == null || to == null)
                return false;

            Piece target = board.getPieceAt(to);
            if (target != null && target.getPieceType() == PieceType.KING) {
                return false; // User cannot click to capture a King
            }

            if (!MoveValidator.isMoveLegal(board, from, to, currentPlayer)) {
                return false;
            }

            Piece p = board.getPieceAt(from);
            if (p instanceof BasePiece) {
                ((BasePiece) p).setHasMoved(true);
            }
            board.movePiece(from, to);

            Piece movedPiece = board.getPieceAt(to);

            if (movedPiece.getPieceType() == PieceType.PAWN) {
                if (isPromotionZone(to, currentPlayer)) {
                    board.setPiece(to, new Queen(currentPlayer));
                }
            }

            nextTurn();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasAnyLegalMoves(PlayerColor player) {
        Map<HexCoordinate, Piece> allPieces = board.getAllPieces();

        for (Map.Entry<HexCoordinate, Piece> entry : allPieces.entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getOwner() != player)
                continue;

            HexCoordinate start = entry.getKey();
            List<HexCoordinate> potentialMoves = piece.getValidMoves(board, start);

            for (HexCoordinate end : potentialMoves) {
                // If we find at least one legal move, the player is NOT checkmated
                if (MoveValidator.isMoveLegal(board, start, end, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void initializeBoard() {
        // Radius 8 Hexagon Setup

        // WHITE (Bottom)

        // King & Queen Center-ish
        board.setPiece(HexCoordinate.of(-5, 8), new Queen(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-3, 8), new King(PlayerColor.WHITE)); // Bottom Right-ish

        // Bishops/Knights/Rooks
        board.setPiece(HexCoordinate.of(-6, 8), new Bishop(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-2, 8), new Bishop(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-4, 8), new Mage(PlayerColor.WHITE)); // Mage added here

        board.setPiece(HexCoordinate.of(-7, 8), new Knight(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, 8), new Knight(PlayerColor.WHITE)); // Tucked in

        board.setPiece(HexCoordinate.of(-8, 8), new Rook(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(0, 8), new Rook(PlayerColor.WHITE)); // Flank

        // White Pawns (r=4 and nearby)
        board.setPiece(HexCoordinate.of(1, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(0, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-2, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-3, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-4, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-5, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-6, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-7, 7), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-8, 7), new Pawn(PlayerColor.WHITE));

        board.setPiece(HexCoordinate.of(1, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(0, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-1, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-2, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-3, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-4, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-5, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-6, 6), new Pawn(PlayerColor.WHITE));
        board.setPiece(HexCoordinate.of(-7, 6), new Pawn(PlayerColor.WHITE));

        // BLACK (Top Left) - Rotated Logic
        board.setPiece(HexCoordinate.of(-3, -5), new King(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-5, -3), new Queen(PlayerColor.BLACK));

        board.setPiece(HexCoordinate.of(-2, -6), new Bishop(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-6, -2), new Bishop(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-4, -4), new Mage(PlayerColor.BLACK)); // Mage added here

        board.setPiece(HexCoordinate.of(-1, -7), new Knight(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-7, -1), new Knight(PlayerColor.BLACK));

        board.setPiece(HexCoordinate.of(0, -8), new Rook(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-8, 0), new Rook(PlayerColor.BLACK));

        // Black Pawns
        board.setPiece(HexCoordinate.of(-8, 1), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-7, 0), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-6, -1), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-5, -2), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-4, -3), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-3, -4), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-2, -5), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-1, -6), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(0, -7), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(1, -8), new Pawn(PlayerColor.BLACK));

        board.setPiece(HexCoordinate.of(-7, 1), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-6, 0), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-5, -1), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-4, -2), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-3, -3), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-2, -4), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(-1, -5), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(0, -6), new Pawn(PlayerColor.BLACK));
        board.setPiece(HexCoordinate.of(1, -7), new Pawn(PlayerColor.BLACK));

        // RED (Top Right)
        board.setPiece(HexCoordinate.of(8, -3), new King(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(8, -5), new Queen(PlayerColor.RED));

        board.setPiece(HexCoordinate.of(8, -2), new Bishop(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(8, -6), new Bishop(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(8, -4), new Mage(PlayerColor.RED)); // Mage added here

        board.setPiece(HexCoordinate.of(8, -1), new Knight(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(8, -7), new Knight(PlayerColor.RED));

        board.setPiece(HexCoordinate.of(8, 0), new Rook(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(8, -8), new Rook(PlayerColor.RED));

        // Red Pawns
        board.setPiece(HexCoordinate.of(7, 1), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, 0), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -1), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -2), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -3), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -4), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -5), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -6), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -7), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(7, -8), new Pawn(PlayerColor.RED));

        board.setPiece(HexCoordinate.of(6, -7), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, -6), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, -5), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, -4), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, -3), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, -2), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, -1), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, 0), new Pawn(PlayerColor.RED));
        board.setPiece(HexCoordinate.of(6, 1), new Pawn(PlayerColor.RED));
    }

    @Override
    public String getId() {
        return gameId;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @Override
    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public PlayerStatus getPlayerStatus(PlayerColor p) {
        return playerStatuses.get(p);
    }

    public void forceCurrentPlayer(PlayerColor player) {
        this.currentPlayer = player;
    }

    public PlayerColor getWinner() {
        return winner;
    }

    public void setWinner(PlayerColor winner) {
        this.winner = winner;
    }

    public void setStatus(GameStatus status) {
        this.gameStatus = status;
    }

    private boolean isPromotionZone(HexCoordinate coord, PlayerColor color) {
        int q = coord.q();
        int r = coord.r();
        int s = coord.s(); // Recall s = -q - r

        switch (color) {
            case WHITE:
                return r == -8 || s == -8;

            case BLACK:
                return r == 8 || q == 8;

            case RED:
                return q == -8 || s == 8;

            default:
                return false;
        }
    }

    private PlayerColor getPreviousPlayer(PlayerColor current) {
        switch (current) {
            case WHITE:
                return PlayerColor.BLACK;
            case RED:
                return PlayerColor.WHITE;
            case BLACK:
                return PlayerColor.RED;
            default:
                return PlayerColor.WHITE;
        }
    }
}