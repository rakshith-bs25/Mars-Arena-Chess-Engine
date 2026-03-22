import React from "react";

// Piece SVGs
import whiteKing from "../assets/pieces/white/King.svg";
import whiteQueen from "../assets/pieces/white/Queen.svg";
import whiteRook from "../assets/pieces/white/Rook.svg";
import whiteBishop from "../assets/pieces/white/Bishop.svg";
import whiteKnight from "../assets/pieces/white/Knight.svg";
import whitePawn from "../assets/pieces/white/Pawn.svg";

import blackKing from "../assets/pieces/black/King.svg";
import blackQueen from "../assets/pieces/black/Queen.svg";
import blackRook from "../assets/pieces/black/Rook.svg";
import blackBishop from "../assets/pieces/black/Bishop.svg";
import blackKnight from "../assets/pieces/black/Knight.svg";
import blackPawn from "../assets/pieces/black/Pawn.svg";

import redKing from "../assets/pieces/red/King.svg";
import redQueen from "../assets/pieces/red/Queen.svg";
import redRook from "../assets/pieces/red/Rook.svg";
import redBishop from "../assets/pieces/red/Bishop.svg";
import redKnight from "../assets/pieces/red/Knight.svg";
import redPawn from "../assets/pieces/red/Pawn.svg";

const PIECE_MAP = {
  WHITE: { KING: whiteKing, QUEEN: whiteQueen, ROOK: whiteRook, BISHOP: whiteBishop, KNIGHT: whiteKnight, PAWN: whitePawn, MAGE: whiteBishop },
  BLACK: { KING: blackKing, QUEEN: blackQueen, ROOK: blackRook, BISHOP: blackBishop, KNIGHT: blackKnight, PAWN: blackPawn, MAGE: blackBishop },
  RED: { KING: redKing, QUEEN: redQueen, ROOK: redRook, BISHOP: redBishop, KNIGHT: redKnight, PAWN: redPawn, MAGE: redBishop },
};

export default function PieceIcon({ piece }) {
  if (!piece) return null;

  const player = piece.player;
  const type = piece.type;

  const pieceSrc = PIECE_MAP?.[player]?.[type];

  if (!pieceSrc) return null;

  // Add a specific class if the piece is a MAGE to trigger the colors in CSS
  const isMage = type === "MAGE";
  const mageClass = isMage ? `mage-piece mage-${player.toLowerCase()}` : "";

  return (
    <div className="piece-wrapper">
      <img
        className={`piece-svg ${mageClass}`}
        src={pieceSrc}
        alt={`${player} ${type}`}
        draggable={false}
      />
    </div>
  );
}