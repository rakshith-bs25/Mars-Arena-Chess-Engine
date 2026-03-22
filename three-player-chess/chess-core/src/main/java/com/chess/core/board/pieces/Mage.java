package com.chess.core.board.pieces;

import com.chess.core.board.BasePiece;
import com.chess.core.board.Board;
import com.chess.core.board.HexBoard;
import com.chess.core.board.Piece;
import com.chess.core.board.PieceType;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDiagonal;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class Mage extends BasePiece {
    public Mage(PlayerColor owner) {
        super(owner, PieceType.MAGE);
    }

    @Override
    public List<HexCoordinate> getValidMoves(Board board, HexCoordinate start) {
        List<HexCoordinate> moves = new ArrayList<>();
        HexBoard hexBoard = (HexBoard) board;

        for (HexDiagonal dir : HexDiagonal.values()) {
            HexCoordinate current = start;
            while (true) {
                current = current.plus(dir.dq(), dir.dr());
                if (!hexBoard.isWithinBounds(current)) break;

                Piece target = hexBoard.getPieceAt(current);
                if (target == null) {
                    moves.add(current);
                } else {
                    if (target.getOwner() != this.getOwner()) {
                        moves.add(current);
                    }
                    break;
                }
            }
        }
        return moves;
    }
}