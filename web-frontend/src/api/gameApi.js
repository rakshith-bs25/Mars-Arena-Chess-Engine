// src/api/gameApi.js

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  import.meta.env.VITE_BACKEND_URL ||
  'http://localhost:8080'

async function handleResponse(res) {
  if (!res.ok) {
    const text = await res.text()
    let message = `HTTP ${res.status}`
    try {
      const json = JSON.parse(text)
      if (json.message) message = json.message
    } catch {
      // ignore parse error
    }
    throw new Error(message)
  }
  if (res.status === 204) return null
  return res.json()
}

// --- 3 Player API ---
export async function createGame() {
  const res = await fetch(`${API_BASE_URL}/three-player/games`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  })
  return handleResponse(res)
}

export async function getGameState(gameId) {
  const res = await fetch(`${API_BASE_URL}/three-player/games/${encodeURIComponent(gameId)}`)
  return handleResponse(res)
}

export async function makeMove(gameId, fromTileId, toTileId, player) {
  const res = await fetch(
    `${API_BASE_URL}/three-player/games/${encodeURIComponent(gameId)}/move`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ from: fromTileId, to: toTileId, player }),
    }
  )
  return handleResponse(res)
}

export async function getPossibleMoves(gameId, fromTileId) {
  const url = `${API_BASE_URL}/three-player/games/${gameId}/possible-moves?from=${encodeURIComponent(fromTileId)}`;
  const res = await fetch(url);
  return handleResponse(res) || [];
}

// --- 2 Player API ---

export async function createTwoPlayerGame() {
    const res = await fetch(`${API_BASE_URL}/two-player/games`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    })
    return handleResponse(res)
}

export async function makeTwoPlayerMove(gameId, moveData) {
    // moveData: { fromX, fromY, toX, toY }
    const res = await fetch(`${API_BASE_URL}/two-player/games/${gameId}/move`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(moveData),
    })
    return handleResponse(res)
}

export async function getTwoPlayerPossibleMoves(gameId, x, y) {
    const url = `${API_BASE_URL}/two-player/games/${gameId}/possible-moves?x=${x}&y=${y}`;
    const res = await fetch(url);
    return handleResponse(res) || [];
}

export async function getGameClock(gameId) {
  const res = await fetch(`${API_BASE_URL}/three-player/games/${encodeURIComponent(gameId)}/clock`);
  return handleResponse(res);
}

// --- 2 Player API Extensions ---

export async function getTwoPlayerGameState(gameId) {
  const res = await fetch(`${API_BASE_URL}/two-player/games/${encodeURIComponent(gameId)}`);
  return handleResponse(res);
}

export async function getTwoPlayerGameClock(gameId) {
  const res = await fetch(`${API_BASE_URL}/two-player/games/${encodeURIComponent(gameId)}/clock`);
  return handleResponse(res);
}