// src/components/HexBoard.jsx
import React, { useMemo } from "react";
import "./HexBoard.css";
import PieceIcon from "./PieceIcon";

function normPlayer(v) {
  if (v == null) return null;
  const s = String(v).trim().toLowerCase();
  if (s === "white" || s === "w") return "white";
  if (s === "black" || s === "b") return "black";
  if (s === "red" || s === "r") return "red";
  return s;
}

function pieceOwner(piece) {
  if (!piece) return null;
  return piece.color ?? piece.player ?? piece.owner ?? piece.side ?? null;
}

export function HexBoard({
  tiles = [],
  fromTileId = null,
  toTileId = null,
  legalMoveTileIds = [],
  onTileClick,
  disabled = false,
  currentPlayer = null, 
}) {
  const legalSet = useMemo(() => new Set(legalMoveTileIds || []), [legalMoveTileIds]);

  const selectedTile = useMemo(() => {
    if (!fromTileId) return null;
    return (tiles || []).find((t) => t.tileId === fromTileId) || null;
  }, [tiles, fromTileId]);

  const selectedOwner = useMemo(() => {
    if (!selectedTile || !selectedTile.piece) return null;
    return normPlayer(pieceOwner(selectedTile.piece));
  }, [selectedTile]);

  const current = useMemo(() => normPlayer(currentPlayer), [currentPlayer]);

  // If current player isn't provided, don't force grey. Show yellow as default.
  const isSelectedByCurrentPlayer = useMemo(() => {
    if (!selectedOwner) return true;       
    if (!current) return true;             
    return selectedOwner === current;
  }, [fromTileId, selectedOwner, current]);

  return (
    <div
      className={[
        "hex-board-root",
        disabled ? "hex-board-disabled" : "",
        fromTileId ? (isSelectedByCurrentPlayer ? "legal-current" : "legal-not-current") : "",
      ]
        .filter(Boolean)
        .join(" ")}
    >
      <div className="hex-board-grid">
        {tiles.map((tile) => {
          const isFrom = tile.tileId === fromTileId;
          const isTo = tile.tileId === toTileId;
          const isLegalTarget = legalSet.has(tile.tileId);

          const extraClass = [
            isFrom ? "selected-from" : "",
            isTo ? "selected-to" : "",
            isLegalTarget ? "legal-target" : "",
          ]
            .filter(Boolean)
            .join(" ");

          return (
            <div
              key={tile.tileId}
              className={["hex-tile", tile.color, extraClass].filter(Boolean).join(" ")}
              style={{
                "--q": tile.q,
                "--r": tile.r,
              }}
              onClick={() => {
                if (disabled) return;
                if (onTileClick) onTileClick(tile.tileId);
              }}
            >
              {/* coordinate label inside each cell */}
              <span className="tile-label">
                {tile.q},{tile.r}
              </span>

              <div className="piece-container">
                {tile.piece ? <PieceIcon piece={tile.piece} /> : null}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
