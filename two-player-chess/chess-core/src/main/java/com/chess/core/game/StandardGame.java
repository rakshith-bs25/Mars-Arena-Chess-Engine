package com.chess.core.game;

import com.chess.core.board.SquareBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.pieces.*;
import com.chess.core.coordinate.SquareCoordinate;
import com.chess.core.board.BasePiece;
import com.chess.core.rules.TwoPlayerMoveValidator;
import com.chess.core.rules.TwoPlayerBoardAnalyzer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StandardGame {
    private final String gameId;
    private final SquareBoard board;
    private PlayerColor currentPlayer;
    private String status; // "IN_PROGRESS", "CHECKMATE", "STALEMATE"

    public StandardGame() {
        this.gameId = UUID.randomUUID().toString();
        this.board = new SquareBoard();
        this.currentPlayer = PlayerColor.WHITE;
        this.status = "IN_PROGRESS";
        initializeStandardBoard();
    }

    private void initializeStandardBoard() {
        placeMajorPieces(0, PlayerColor.WHITE);
        for (int x = 0; x < 8; x++) board.setPiece(SquareCoordinate.of(x, 1), new Pawn(PlayerColor.WHITE));
        
        placeMajorPieces(7, PlayerColor.BLACK);
        for (int x = 0; x < 8; x++) board.setPiece(SquareCoordinate.of(x, 6), new Pawn(PlayerColor.BLACK));
    }

    private void placeMajorPieces(int row, PlayerColor color) {
        board.setPiece(SquareCoordinate.of(0, row), new Rook(color));
        board.setPiece(SquareCoordinate.of(1, row), new Knight(color));
        board.setPiece(SquareCoordinate.of(2, row), new Bishop(color));
        board.setPiece(SquareCoordinate.of(3, row), new Queen(color));
        board.setPiece(SquareCoordinate.of(4, row), new King(color));
        board.setPiece(SquareCoordinate.of(5, row), new Bishop(color));
        board.setPiece(SquareCoordinate.of(6, row), new Knight(color));
        board.setPiece(SquareCoordinate.of(7, row), new Rook(color));
    }

    // New Method for API
    public List<String> getLegalMoves(int x, int y) {
        if (!status.equals("IN_PROGRESS")) return Collections.emptyList();

        SquareCoordinate from = SquareCoordinate.of(x, y);
        Piece p = board.getPieceAt(from);
        
        if (p == null) return Collections.emptyList();

        // Get Valid Geometric Moves -> Filter by Rules (Check)
        return p.getValidMoves(board, from).stream()
                .filter(to -> TwoPlayerMoveValidator.isMoveLegal(board, from, to, p.getOwner()))
                .map(SquareCoordinate::toString) // "x,y"
                .collect(Collectors.toList());
    }

    public boolean makeMove(int fromX, int fromY, int toX, int toY) {
        if (!status.equals("IN_PROGRESS")) return false;

        SquareCoordinate from = SquareCoordinate.of(fromX, fromY);
        SquareCoordinate to = SquareCoordinate.of(toX, toY);
        Piece p = board.getPieceAt(from);

        // 1. Basic & Legal Validation
        if (!TwoPlayerMoveValidator.isMoveLegal(board, from, to, currentPlayer)) {
            return false;
        }

        // 2. Castling Execution (Side Effect)
        if (p instanceof King && Math.abs(fromX - toX) == 2) {
            int row = fromY;
            boolean kingside = toX > fromX;
            int rookSrcX = kingside ? 7 : 0;
            int rookDestX = kingside ? 5 : 3;
            
            SquareCoordinate rookSrc = SquareCoordinate.of(rookSrcX, row);
            SquareCoordinate rookDest = SquareCoordinate.of(rookDestX, row);
            board.movePiece(rookSrc, rookDest); // Move the rook
        }

        // 3. Execute Main Move
        board.movePiece(from, to);
        if (p instanceof BasePiece) ((BasePiece) p).setHasMoved(true);

        // 4. Promotion
        if (p instanceof Pawn && (to.y() == 0 || to.y() == 7)) {
            board.setPiece(to, new Queen(currentPlayer));
        }

        // 5. Switch Turn
        currentPlayer = (currentPlayer == PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;

        // 6. Check Game Over Conditions
        if (!TwoPlayerMoveValidator.hasAnyLegalMoves(board, currentPlayer)) {
            if (TwoPlayerBoardAnalyzer.isKingInCheck(board, currentPlayer)) {
                status = "CHECKMATE"; // Previous player wins
            } else {
                status = "STALEMATE"; // Draw
            }
        }

        return true;
    }

    public String getId() { return gameId; }
    public SquareBoard getBoard() { return board; }
    public PlayerColor getCurrentPlayer() { return currentPlayer; }
    public String getStatus() { return status; }
}