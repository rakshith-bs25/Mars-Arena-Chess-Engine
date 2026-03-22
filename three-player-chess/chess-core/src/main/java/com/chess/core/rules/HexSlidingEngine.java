package com.chess.core.rules;

import com.chess.core.board.HexBoard; 
import com.chess.core.board.Piece;
import com.chess.core.coordinate.DirectionOffset;
import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class HexSlidingEngine {
    
    public static List<HexCoordinate> calculateSlidingMoves(
            HexBoard board, 
            HexCoordinate start,
            DirectionOffset[] directions,
            PlayerColor owner) {

        List<HexCoordinate> moves = new ArrayList<>();

        for (DirectionOffset dir : directions) {
            HexCoordinate current = start.plus(dir.dq(), dir.dr());

            while (board.isWithinBounds(current)) {
                
                Piece target = board.getPieceAt(current);

                if (target == null) {
                    moves.add(current); 
                } else {
                    if (target.getOwner() != owner) {
                        moves.add(current); 
                    }
                    break; 
                }
                current = current.plus(dir.dq(), dir.dr());
            }
        }
        return moves;
    }
}