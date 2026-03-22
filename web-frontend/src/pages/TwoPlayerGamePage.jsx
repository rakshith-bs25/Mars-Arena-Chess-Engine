import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { SquareBoard } from '../components/SquareBoard';
import { TurnIndicators } from '../components/TurnIndicators';
import { GameInfoPanel } from '../components/GameInfoPanel';
import { ClockDisplay } from '../components/ClockDisplay';
import { GameOverModal } from '../components/GameOverModal';
import { useChessClock } from '../hooks/useChessClock';
import { SoundEngine } from '../utils/audioUtils';
import { 
  createTwoPlayerGame, 
  makeTwoPlayerMove, 
  getTwoPlayerPossibleMoves 
} from '../api/gameApi';

export default function TwoPlayerGamePage() {
  const navigate = useNavigate();
  const [gameId, setGameId] = useState(null);
  const [gameState, setGameState] = useState(null);
  const [selectedSquare, setSelectedSquare] = useState(null);
  const [twoPlayerLegalMoves, setTwoPlayerLegalMoves] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showTimeoutModal, setShowTimeoutModal] = useState(false);
  const [winnerName, setWinnerName] = useState("");

  const initGame = async () => {
    setLoading(true);
    setShowTimeoutModal(false);
    setGameState(null); 
    setSelectedSquare(null);
    setTwoPlayerLegalMoves([]);
    setError(null);
    try {
      const raw = await createTwoPlayerGame();
      setGameId(raw.gameId);
      setGameState(raw);
      SoundEngine.playNewGame();
    } catch (e) {
      setError(e.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    initGame();
  }, []);

  const handleLocalTimeout = useCallback((playerWhoTimedOut) => {
    setShowTimeoutModal(prev => {
        if (prev) return prev;
        const winner = playerWhoTimedOut === 'WHITE' ? 'BLACK' : 'WHITE';
        setWinnerName(winner);
        SoundEngine.playDevilLaugh(); // Demonic Laugh Trigger
        SoundEngine.playGameOverVoice(winner, false);
        return true;
    });
  }, []);

  const timers = useChessClock(gameId, gameState, gameState?.status, handleLocalTimeout, false);

  const handleSquareClick = async (x, y) => {
    const status = gameState?.status || 'IN_PROGRESS';
    if (!gameState || loading || status !== 'IN_PROGRESS' || showTimeoutModal) return;

    if (selectedSquare && selectedSquare.x === x && selectedSquare.y === y) {
      setSelectedSquare(null);
      setTwoPlayerLegalMoves([]);
      return;
    }

    if (selectedSquare && twoPlayerLegalMoves.includes(`${x},${y}`)) {
      const pieceBeforeMove = gameState.board.find(p => p.x === selectedSquare.x && p.y === selectedSquare.y)?.pieceType;
      
      setLoading(true);
      try {
        const move = { fromX: selectedSquare.x, fromY: selectedSquare.y, toX: x, toY: y };
        const newState = await makeTwoPlayerMove(gameId, move);
        
        const pieceAfterMove = newState.board.find(p => p.x === x && p.y === y)?.pieceType;
        if (pieceBeforeMove === 'PAWN' && pieceAfterMove === 'QUEEN') {
          SoundEngine.playQueenPromotion();
        } else {
          SoundEngine.playMove();
        }

        setGameState(newState);
        setSelectedSquare(null);
        setTwoPlayerLegalMoves([]);
        
        if ((newState.status === 'TIMEOUT' || newState.status === 'CHECKMATE') && !showTimeoutModal) {
          const winner = newState.currentPlayer === 'WHITE' ? 'BLACK' : 'WHITE';
          setWinnerName(winner);
          setShowTimeoutModal(true);
          SoundEngine.playGameOver();
          SoundEngine.playGameOverVoice(winner, false);
        }
      } catch (e) {
        console.error(e);
      }
      setLoading(false);
      return;
    }

    const piece = gameState.board.find((p) => p.x === x && p.y === y);
    if (piece && piece.owner === gameState.currentPlayer) {
      setSelectedSquare({ x, y });
      const moves = await getTwoPlayerPossibleMoves(gameId, x, y);
      setTwoPlayerLegalMoves(moves);
    } else {
      setSelectedSquare(null);
      setTwoPlayerLegalMoves([]);
    }
  };

  const playersUI = [
    { id: 'WHITE', status: 'ACTIVE', isCurrent: gameState?.currentPlayer === 'WHITE' },
    { id: 'BLACK', status: 'ACTIVE', isCurrent: gameState?.currentPlayer === 'BLACK' },
  ];

  const infoMessage = error 
    ? error 
    : gameState?.status === 'CHECKMATE' 
      ? `Game Over! ${gameState.currentPlayer} Lost.` 
      : gameState?.status === 'STALEMATE' 
        ? 'Game Over! Stalemate.' 
        : gameState?.status === 'TIMEOUT'
          ? 'Game Over! Timeout.'
          : selectedSquare ? `Selected: ${selectedSquare.x},${selectedSquare.y}` : 'Your Turn';

  return (
    <div className="app-root">
      <GameOverModal 
        isOpen={showTimeoutModal} 
        winner={winnerName} 
        reason="Match Concluded" 
        onClose={initGame} 
      />

      <header className="app-header">
        <h1>MARS OVERDRIVE: Duel</h1>
        <button className="exit-button" onClick={() => navigate('/')}>Exit to Lobby</button>
      </header>
      <main className="app-main">
        <section className={`board-panel ${showTimeoutModal ? 'board-frozen' : ''}`}>
          {loading && !gameState && <div className="board-loading">Loading...</div>}
          {gameState && (
            <SquareBoard
              boardData={gameState.board}
              onTileClick={handleSquareClick}
              selectedTile={selectedSquare}
              legalMoves={twoPlayerLegalMoves}
            />
          )}
        </section>
        <section className="side-panel">
          {gameState && (
            <ClockDisplay 
              timers={timers} 
              activePlayer={gameState.currentPlayer} 
              playerStatuses={{
                WHITE: gameState.status === 'TIMEOUT' && gameState.currentPlayer === 'WHITE' ? 'TIMEOUT' : 'ACTIVE',
                BLACK: gameState.status === 'TIMEOUT' && gameState.currentPlayer === 'BLACK' ? 'TIMEOUT' : 'ACTIVE'
              }}
              isThreePlayer={false} 
            />
          )}
          <TurnIndicators players={playersUI} />
          <GameInfoPanel
            status={gameState?.status || 'ACTIVE'}
            currentPlayer={gameState?.currentPlayer}
            gameState={gameState}
            message={infoMessage}
            onNewGame={initGame}
            gameId={gameId}
          />
        </section>
      </main>
    </div>
  );
}