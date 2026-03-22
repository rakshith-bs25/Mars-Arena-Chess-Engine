import React, { useMemo } from "react";

// Helper to format ms to mm:ss
function formatTime(ms) {
  if (ms === null || ms === undefined) return "0:00";
  const totalSeconds = Math.floor(ms / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
}

export function GameInfoPanel({
  status,
  message,
  currentPlayer,
  onNewGame,
  gameState, 
  loadingMove = false,
  loadingGame = false,
}) {
  const upperStatus = (status || "UNKNOWN").toUpperCase();
  const isBusy = loadingMove || loadingGame;

  // Use moves from the normalized gameState
  const movesHistory = useMemo(() => gameState?.moves || [], [gameState]);

  return (
    <div className="game-info-card">
      <h2>Game Info</h2>

      <p className="game-info-turn">
        <span className="game-info-label">Status: </span>
        <span className="game-info-value">{upperStatus}</span>
      </p>

      <p className="game-info-turn">
        <span className="game-info-label">Current turn: </span>
        <span className="game-info-value" style={{
                color: currentPlayer,
                backgroundColor: currentPlayer === 'BLACK' ? 'white' : 'transparent',
                padding: currentPlayer === 'BLACK' ? '2px 8px' : '0px',
                borderRadius: '4px',
                fontWeight: 'bold',
                display: 'inline-block'}}>
            {currentPlayer?.charAt(0) + currentPlayer?.slice(1).toLowerCase()}
        </span>
      </p>

      <div className="moves-block">
        <div className="moves-header">
          <div className="moves-title">Moves History</div>
        </div>

        <div className="moves-table-wrap">
          <table className="moves-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Player</th>
                <th>Piece</th>
                <th>From</th>
                <th>To</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              {movesHistory.length === 0 ? (
                <tr>
                  <td colSpan="6" className="moves-empty">No moves yet.</td>
                </tr>
              ) : (
                movesHistory.map((move, idx) => (
                  <tr key={idx}>
                    <td>{move.ply + 1}</td>
                    <td>
                      <span className={`move-dot dot-${move.player.toLowerCase()}`}></span>
                      {move.player}
                    </td>
                    <td style={{ fontSize: '0.75rem' }}>{move.piece}</td>
                    <td>{move.fromTile}</td>
                    <td>{move.toTile}</td>
                    <td>{formatTime(move.timeTakenMs)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <button
        className="new-game-button new-game-button-blue"
        onClick={onNewGame}
        disabled={isBusy}
      >
        {isBusy ? "Starting..." : "New Game"}
      </button>
    </div>
  );
}