package com.chess.core.board;

import com.chess.core.coordinate.HexCoordinate;
import com.chess.core.game.PlayerColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HexBoard implements Board {
    private final Map<HexCoordinate, Piece> pieceMap;
    private final List<HexCoordinate> validCoordinates;

    private static final int BOARD_RADIUS = 8;

    public HexBoard() {
        this.pieceMap = new HashMap<>();
        this.validCoordinates = generateFullHexagon();
    }

    public HexBoard(HexBoard other) {
        this.pieceMap = new HashMap<>(other.pieceMap);
        this.validCoordinates = new ArrayList<>(other.validCoordinates);
    }

    private List<HexCoordinate> generateFullHexagon() {
        List<HexCoordinate> coords = new ArrayList<>();
        for (int q = -BOARD_RADIUS; q <= BOARD_RADIUS; q++) {
            for (int r = -BOARD_RADIUS; r <= BOARD_RADIUS; r++) {
                if (Math.abs(q + r) <= BOARD_RADIUS) {
                    coords.add(HexCoordinate.of(q, r));
                }
            }
        }
        return coords;
    }

    public List<HexCoordinate> getPlayableCoordinates() {
        return validCoordinates;
    }

    public boolean isWithinBounds(HexCoordinate coord) {
        return validCoordinates.contains(coord);
    }

    // Standard Methods
    public void setPiece(HexCoordinate coord, Piece piece) {
        if (piece == null)
            pieceMap.remove(coord);
        else
            pieceMap.put(coord, piece);
    }

    public Piece movePiece(HexCoordinate start, HexCoordinate end) {
        Piece movingPiece = pieceMap.remove(start);
        Piece captured = pieceMap.put(end, movingPiece);
        return captured;
    }

    public void undoMove(HexCoordinate start, HexCoordinate end, Piece capturedPiece) {
        Piece movingPiece = pieceMap.remove(end);
        pieceMap.put(start, movingPiece);
        if (capturedPiece != null)
            pieceMap.put(end, capturedPiece);
    }

    public Piece getPieceAt(HexCoordinate coord) {
        return pieceMap.get(coord);
    }

    public Map<HexCoordinate, Piece> getAllPieces() {
        return new HashMap<>(pieceMap);
    }

    @Override
    public Piece getPiece(String coordinateId) {
        HexCoordinate coord = CoordinateMapper.toHex(coordinateId);
        return (coord != null) ? pieceMap.get(coord) : null;
    }

    @Override
    public List<Piece> getPieces(PlayerColor playerColor) {
        return pieceMap.values().stream()
                .filter(p -> p.getOwner() == playerColor)
                .collect(Collectors.toList());
    }
}