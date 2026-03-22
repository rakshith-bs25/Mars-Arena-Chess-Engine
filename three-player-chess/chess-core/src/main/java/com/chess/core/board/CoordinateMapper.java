package com.chess.core.board;

import com.chess.core.coordinate.HexCoordinate;

// Translates between internal Axial Coordinates (math) and External Tile IDs (UI).
public class CoordinateMapper {

    public static HexCoordinate toHex(String tileId) {
        if (tileId == null || !tileId.contains(","))
            return null;
        try {
            String[] parts = tileId.split(",");
            int q = Integer.parseInt(parts[0].trim());
            int r = Integer.parseInt(parts[1].trim());
            return HexCoordinate.of(q, r);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String toId(HexCoordinate hex) {
        return hex.q() + "," + hex.r();
    }
}