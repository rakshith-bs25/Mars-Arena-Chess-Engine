import { useState, useEffect } from 'react';
import { getGameClock } from '../api/gameApi';

export function useChessClock(gameId, initialData, gameStatus, onTimeout, enableSync = true) {
  const DEFAULT_TIME = 60000; // 1 minute

  const [timers, setTimers] = useState({
    WHITE: DEFAULT_TIME,
    BLACK: DEFAULT_TIME,
    RED: DEFAULT_TIME,
  });

  const activePlayer = initialData?.currentPlayer;
  const isPaused = gameStatus !== 'IN_PROGRESS';

  // Sync timers with initial backend data when game loads or resets
  useEffect(() => {
    if (!initialData) {
      setTimers({
        WHITE: DEFAULT_TIME,
        BLACK: DEFAULT_TIME,
        RED: DEFAULT_TIME,
      });
    } else if (typeof initialData.whiteRemainingMs === 'number') {
      setTimers({
        WHITE: initialData.whiteRemainingMs,
        BLACK: initialData.blackRemainingMs,
        RED: initialData.redRemainingMs || 0,
      });
    }
  }, [initialData, gameId]);

  // Local 100ms Ticker for smooth UI
  useEffect(() => {
    if (isPaused || !activePlayer) return;

    const interval = setInterval(() => {
      setTimers((prev) => {
        const remaining = prev[activePlayer];
        
        if (remaining <= 0) {
          clearInterval(interval);
          if (onTimeout) onTimeout(activePlayer);
          return prev;
        }
        
        return {
          ...prev,
          [activePlayer]: Math.max(0, remaining - 100),
        };
      });
    }, 100);

    return () => clearInterval(interval);
  }, [activePlayer, isPaused, onTimeout]);

  // Periodic Authoritative Sync (Heartbeat) - ONLY if enableSync is true
  useEffect(() => {
    // If sync is disabled or game is paused/not started, do nothing
    if (!enableSync || isPaused || !gameId) return;

    const syncWithServer = async () => {
      try {
        const clockData = await getGameClock(gameId);
        setTimers({
          WHITE: clockData.whiteRemainingMs,
          BLACK: clockData.blackRemainingMs,
          RED: clockData.redRemainingMs || 0,
        });
      } catch (e) {
        console.error("Clock sync failed", e);
      }
    };

    const heartbeat = setInterval(syncWithServer, 3000); 
    return () => clearInterval(heartbeat);
  }, [gameId, isPaused, enableSync]);

  return timers;
}