import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { HexBoard } from '../components/HexBoard';
import { TurnIndicators } from '../components/TurnIndicators';
import { GameInfoPanel } from '../components/GameInfoPanel';
import { GameOverModal } from '../components/GameOverModal';
import { createGame, getPossibleMoves, makeMove, getGameClock, getGameState } from '../api/gameApi';
import { normalizeGameState, getPieceTypeAt, isTileOccupied } from '../model/gameModel';
import { ClockDisplay } from '../components/ClockDisplay';
import { SoundEngine } from '../utils/audioUtils';

export default function ThreePlayerGamePage() {
  const navigate = useNavigate();
  const { gameId: routeGameId } = useParams();
  
  const [gameId, setGameId] = useState(null);
  const [gameState, setGameState] = useState(null);
  const [fromTileId, setFromTileId] = useState(null);
  const [legalMoveTileIds, setLegalMoveTileIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showWinnerModal, setShowWinnerModal] = useState(false);
  const [winnerName, setWinnerName] = useState("");
  const [bonuses, setBonuses] = useState({ WHITE: false, RED: false, BLACK: false });
  const [eliminationOrder, setEliminationOrder] = useState([]);

  const DEFAULT_TIME = 60000; 
  const [timers, setTimers] = useState({ WHITE: DEFAULT_TIME, BLACK: DEFAULT_TIME, RED: DEFAULT_TIME });

  const activePlayer = gameState?.currentPlayer;
  const isPaused = gameState?.status && gameState.status !== 'IN_PROGRESS';

  const timedOutLoser = useMemo(() => {
    if (!gameState) return null;
    if (timers.WHITE <= 0) return 'WHITE';
    if (timers.RED <= 0) return 'RED';
    if (timers.BLACK <= 0) return 'BLACK';
    return null;
  }, [timers, gameState]);

  useEffect(() => {
    if (timers.WHITE <= 0 && !eliminationOrder.includes('WHITE')) setEliminationOrder(prev => [...prev, 'WHITE']);
    if (timers.RED <= 0 && !eliminationOrder.includes('RED')) setEliminationOrder(prev => [...prev, 'RED']);
    if (timers.BLACK <= 0 && !eliminationOrder.includes('BLACK')) setEliminationOrder(prev => [...prev, 'BLACK']);
  }, [timers, eliminationOrder]);

  const marsRoyalty = useMemo(() => {
    if (!winnerName) return null;
    const firstOut = eliminationOrder[0];
    const secondOut = eliminationOrder[1];
    const getColorHex = (name) => {
      if (name === 'WHITE') return '#FFFFFF';
      if (name === 'RED') return '#EF4444';
      if (name === 'BLACK') return '#3B82F6';
      return '#FFFFFF';
    };

    return [
      { name: winnerName, hex: getColorHex(winnerName), type: 'winner', text: 'The Winner, the Champion, Ruler of the Kingdom, Magnetic Soul', emoji: '🏆' },
      { name: secondOut || '...', hex: getColorHex(secondOut), type: 'loser-mid', text: 'managed to survive for few more secs, still WHAT A LOSER!', emoji: '🤏' },
      { name: firstOut || '...', hex: getColorHex(firstOut), type: 'loser-first', text: 'Amateur, got kicked out THE FIRST', emoji: '🤡' }
    ];
  }, [winnerName, eliminationOrder]);

  const resetUiState = () => {
    setLoading(true); 
    setGameState(null);
    setFromTileId(null);
    setLegalMoveTileIds([]);
    setError(null);
    setShowWinnerModal(false);
    setWinnerName("");
    setBonuses({ WHITE: false, RED: false, BLACK: false });
    setEliminationOrder([]);
  };

  const createAndRouteGame = async () => {
    resetUiState();
    try {
      const raw = await createGame();
      const id = raw.gameId;
      navigate(`/three-player-chess/${id}`, { replace: true });
      setGameId(id);
      setGameState(normalizeGameState(raw));
      SoundEngine.playNewGame();
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };

  const loadGameFromRoute = async (id) => {
    resetUiState();
    try {
      const raw = await getGameState(id);
      const normalized = normalizeGameState(raw);
      setGameId(id);
      setGameState(normalized);
      const clockData = await getGameClock(id);
      setTimers({ WHITE: clockData.whiteRemainingMs, BLACK: clockData.blackRemainingMs, RED: clockData.redRemainingMs });
      setGameState(prev => prev ? { ...prev, currentPlayer: clockData.activePlayer, status: clockData.gameStatus } : prev);
      SoundEngine.playNewGame();
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };

  const triggerBonusEffect = (player) => {
    setBonuses(prev => ({ ...prev, [player]: true }));
    setTimeout(() => setBonuses(prev => ({ ...prev, [player]: false })), 4000);
  };

  useEffect(() => {
    if (routeGameId) loadGameFromRoute(routeGameId);
    else createAndRouteGame();
  }, [routeGameId]);

  const handleHexClick = async (tileId) => {
    if (loading || isPaused || !(gameId || routeGameId) || showWinnerModal) return;

    if (!fromTileId) {
      const legal = await getPossibleMoves(gameId || routeGameId, tileId);
      setFromTileId(tileId);
      setLegalMoveTileIds(legal);
    } else {
      if (fromTileId === tileId) {
        setFromTileId(null);
        setLegalMoveTileIds([]);
        return;
      }
      
      const movingPieceType = getPieceTypeAt(gameState.tiles, fromTileId);
      const isCapturing = isTileOccupied(gameState.tiles, tileId);
      
      if (movingPieceType === 'MAGE' && isCapturing && legalMoveTileIds.includes(tileId)) {
        triggerBonusEffect(gameState.currentPlayer);
      }

      setLoading(true);
      try {
        const raw = await makeMove(gameId || routeGameId, fromTileId, tileId, gameState.currentPlayer);
        const nextState = normalizeGameState(raw);
        
        const pieceAfterMove = getPieceTypeAt(nextState.tiles, tileId);
        if (movingPieceType === 'PAWN' && pieceAfterMove === 'QUEEN') {
          SoundEngine.playQueenPromotion();
        } else {
          SoundEngine.playMove();
        }

        setGameState(nextState);
        setFromTileId(null);
        setLegalMoveTileIds([]);
      } catch (e) { setError(e.message); }
      setLoading(false);
    }
  };

  const playersUI = [
    { id: 'WHITE', status: timers.WHITE <= 0 ? 'TIMEOUT' : (gameState?.whiteStatus || 'ACTIVE'), isCurrent: gameState?.currentPlayer === 'WHITE' },
    { id: 'RED',   status: timers.RED <= 0 ? 'TIMEOUT' : (gameState?.redStatus || 'ACTIVE'), isCurrent: gameState?.currentPlayer === 'RED' },
    { id: 'BLACK', status: timers.BLACK <= 0 ? 'TIMEOUT' : (gameState?.blackStatus || 'ACTIVE'), isCurrent: gameState?.currentPlayer === 'BLACK' },
  ];

  const playerStatuses = { WHITE: gameState?.whiteStatus, BLACK: gameState?.blackStatus, RED: gameState?.redStatus };
  
  useEffect(() => {
    if (!gameState) {
      if (!routeGameId) setTimers({ WHITE: DEFAULT_TIME, BLACK: DEFAULT_TIME, RED: DEFAULT_TIME });
      return;
    }
    if (typeof gameState.whiteRemainingMs === 'number') {
      setTimers({ WHITE: gameState.whiteRemainingMs, BLACK: gameState.blackRemainingMs, RED: gameState.redRemainingMs });
    }
  }, [gameState, gameId]);

  useEffect(() => {
    if (isPaused || !activePlayer || showWinnerModal) return;
    const interval = setInterval(() => {
      setTimers((prev) => {
        const remaining = prev[activePlayer];
        if (remaining <= 0) {
          clearInterval(interval);
          SoundEngine.playDevilLaugh(); // Demonic Laugh Trigger
          return prev;
        }
        return { ...prev, [activePlayer]: Math.max(0, remaining - 100) };
      });
    }, 100);
    return () => clearInterval(interval);
  }, [activePlayer, isPaused, showWinnerModal]);
  
  useEffect(() => {
    const currentId = gameId || routeGameId;
    if (!currentId || showWinnerModal) return;

    const syncWithServer = async () => {
      try {
        const clockData = await getGameClock(currentId);
        setTimers({ WHITE: clockData.whiteRemainingMs, BLACK: clockData.blackRemainingMs, RED: clockData.redRemainingMs });
        setGameState(prev => prev ? { ...prev, currentPlayer: clockData.activePlayer, status: clockData.gameStatus } : prev);
        if (clockData.winner && !showWinnerModal) {
          setWinnerName(clockData.winner);
          setShowWinnerModal(true);
          SoundEngine.playGameOver(); 
          SoundEngine.playGameOverVoice(clockData.winner, true);
        }
      } catch (e) { console.error("Sync failed", e); }
    };
    const heartbeat = setInterval(syncWithServer, 3000); 
    return () => clearInterval(heartbeat);
  }, [gameId, routeGameId, showWinnerModal]);
  
  const infoMessage = error ? error : fromTileId ? `Target Identified: ${fromTileId}` : 'Awaiting Orders';

  return (
    <div className="app-root">
      <GameOverModal isOpen={showWinnerModal} winner={winnerName} rankings={marsRoyalty} onClose={createAndRouteGame} />
      <header className="app-header">
        <h1>MARS OVERDRIVE: TRIAD</h1>
        <button className="exit-button" onClick={() => navigate('/')}>Exit to Lobby</button>
      </header>
      {timedOutLoser && !showWinnerModal && (
        <div className="loser-banner">
          <div className="loser-banner-content">
            <span className="loser-highlight">{timedOutLoser}, what a loser!</span>
            <span className="loser-flavor">Your soul was too slow for Planet MARS. Better luck next lifetime!!</span>
          </div>
        </div>
      )}
      <main className="app-main">
        <section className={`board-panel ${showWinnerModal ? 'board-frozen' : ''}`}>
          {loading && !gameState && <div className="board-loading">Loading System...</div>}
          {gameState && <HexBoard tiles={gameState.tiles} fromTileId={fromTileId} legalMoveTileIds={legalMoveTileIds} onTileClick={handleHexClick} currentPlayer={gameState.currentPlayer} />}
        </section>
        <section className="side-panel">
          {gameState && <ClockDisplay timers={timers} activePlayer={gameState.currentPlayer} playerStatuses={playerStatuses} bonuses={bonuses} />}
          <TurnIndicators players={playersUI} />
          <GameInfoPanel status={gameState?.status || 'ACTIVE'} currentPlayer={gameState?.currentPlayer} gameState={gameState} message={infoMessage} onNewGame={createAndRouteGame} gameId={gameId} />
        </section>
      </main>
    </div>
  );
}