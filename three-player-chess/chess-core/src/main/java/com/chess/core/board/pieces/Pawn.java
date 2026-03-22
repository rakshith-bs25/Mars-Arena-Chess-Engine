package com.chess.core.board.pieces;

import com.chess.core.board.*;
import com.chess.core.coordinate.*;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends BasePiece {

    public Pawn(PlayerColor owner) {
        super(owner, PieceType.PAWN);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        List<HexCoordinate> moves = new ArrayList<>();
        HexBoard hexBoard = (HexBoard) board;

        HexDirection[] forwards = getForwardDirections(getOwner());
        HexDiagonal[] captures = getCaptureDiagonals(getOwner());

        // ---------- Forward move ----------
        for (HexDirection forward : forwards) {
            HexCoordinate oneStep = start.plus(forward.dq(), forward.dr());
            if (hexBoard.isWithinBounds(oneStep) && hexBoard.getPieceAt(oneStep) == null) {
                moves.add(oneStep);

                // Double move from initial rank
                if (!this.hasMoved()) {
                    HexCoordinate twoStep = oneStep.plus(forward.dq(), forward.dr());
                    if (hexBoard.isWithinBounds(twoStep)
                            && hexBoard.getPieceAt(twoStep) == null) {
                        moves.add(twoStep);
                    }
                }
            }
        }

        // Captures
        for (HexDiagonal diag : captures) {
            HexCoordinate target = start.plus(diag.dq(), diag.dr());
            if (!hexBoard.isWithinBounds(target))
                continue;

            Piece enemy = hexBoard.getPieceAt(target);
            if (enemy != null && enemy.getOwner() != getOwner()) {
                moves.add(target);
            }
        }

        return moves;
    }

    // ONE forward direction per color
    private HexDirection[] getForwardDirections(PlayerColor color) {
        switch (color) {
            case WHITE:
                return new HexDirection[] {
                        HexDirection.NORTH,
                        HexDirection.NORTH_EAST
                };
            case RED:
                return new HexDirection[] {
                        HexDirection.NORTH_WEST,
                        HexDirection.SOUTH_WEST
                };
            case BLACK:
                return new HexDirection[] {
                        HexDirection.SOUTH_EAST,
                        HexDirection.SOUTH
                };
            default:
                throw new IllegalStateException("Unknown player color: " + color);
        }
    }

    // TWO diagonal capture directions
    private HexDiagonal[] getCaptureDiagonals(PlayerColor color) {
        switch (color) {
            case WHITE:
                return new HexDiagonal[] {
                        HexDiagonal.D6, HexDiagonal.D1
                };
            case BLACK:
                return new HexDiagonal[] {
                        HexDiagonal.D3, HexDiagonal.D4
                };
            case RED:
                return new HexDiagonal[] {
                        HexDiagonal.D5, HexDiagonal.D2
                };
            default:
                return new HexDiagonal[0];
        }
    }
}
