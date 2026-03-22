// src/model/gameModel.js

export const DEFAULT_PLAYERS = [
  { id: 'WHITE', status: 'ACTIVE' },
  { id: 'RED', status: 'ACTIVE' },
  { id: 'BLACK', status: 'ACTIVE' },
]

export function normalizeGameState(raw) {
    if (!raw || typeof raw !== 'object') {
      return {
        gameId: 'unknown',
        currentPlayer: 'WHITE',
        status: 'UNKNOWN',
        players: [],
        tiles: [],
        message: 'No game data',
        lastMove: null,
      }
    }
  
    // 1) Basic fields
    const gameId = raw.gameId || raw.id || 'unknown'
    const currentPlayer = raw.currentPlayer || 'WHITE'
    const status = raw.status || 'UNKNOWN'
    const message = raw.message || ''
  
    // 2) Players
    const players = Array.isArray(raw.players)
      ? raw.players.map((p, index) => ({
          id: p.id || p.color || `P${index + 1}`,
          status: p.status || 'ACTIVE',
        }))
      : []
  
    // 3) Tiles
    const sourceTiles = raw.board || raw.tiles || [];
    const tiles = Array.isArray(sourceTiles)
      ? sourceTiles.map((t) => normalizeTile(t))
      : []

    //4) Moves
    const moves = Array.isArray(raw.moves) ? raw.moves : [];
  
    // 5) Last move
    const lastMove = raw.lastMove || null
  
    return {
      gameId,
      currentPlayer,
      status,
      players,
      tiles,
      moves,
      message,
      lastMove,
      whiteRemainingMs: raw.whiteRemainingMs,
      blackRemainingMs: raw.blackRemainingMs,
      redRemainingMs: raw.redRemainingMs,
    }
  }
  
  function normalizeTile(t) {
    if (!t || typeof t !== 'object') {
      return { tileId: 'unknown', q: 0, r: 0, color: 'light', piece: null }
    }
  
    let q = 0, r = 0;
    
    if (t.coordinate && typeof t.coordinate === 'string') {
        const parts = t.coordinate.split(',');
        if (parts.length === 2) {
            q = parseInt(parts[0], 10);
            r = parseInt(parts[1], 10);
        }
    } else {
        // Fallback if already numbers
        q = typeof t.q === 'number' ? t.q : 0;
        r = typeof t.r === 'number' ? t.r : 0;
    }

    // Determine ID
    const tileId = t.tileId || t.coordinate || `T_${q}_${r}`

    const colorIndex = ((q - r) % 3 + 3) % 3;  
    const color = t.color || ['medium', 'light', 'dark'][colorIndex];

    let piece = null
    if (t.pieceType && t.owner) {
        piece = {
            type: t.pieceType,
            player: t.owner
        }
    } else if (t.piece) {
        piece = t.piece;
    }
  
    return {
      tileId,
      q,
      r,
      color,
      piece,
    }
  }

export function getPieceTypeAt(tiles, tileId) {
    const tile = tiles?.find(t => t.tileId === tileId);
    return tile?.piece?.type || null;
}

export function isTileOccupied(tiles, tileId) {
    const tile = tiles?.find(t => t.tileId === tileId);
    return !!tile?.piece;
}