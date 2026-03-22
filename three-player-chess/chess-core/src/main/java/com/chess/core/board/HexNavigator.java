package com.chess.core.board;

import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.coordinate.HexDirection;

import java.util.ArrayList;
import java.util.List;

public final class HexNavigator {

    private HexNavigator() {
    }

    // Returns the next hex in the given axial direction.
    public static HexCoordinate next(HexCoordinate current, HexDirection dir) {
        if (current == null || dir == null) {
            return null;
        }
        return current.plus(dir.dq(), dir.dr());
    }

    // Returns all 6 orthogonal neighbors.
    public static List<HexCoordinate> getAllNeighbors(HexCoordinate c) {
        List<HexCoordinate> list = new ArrayList<>();
        for (HexDirection d : HexDirection.values()) {
            list.add(next(c, d));
        }
        return list;
    }
}