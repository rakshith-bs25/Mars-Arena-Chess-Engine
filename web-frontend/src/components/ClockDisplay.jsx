import React from 'react';

function formatTime(ms) {
  if (ms === null || ms === undefined || isNaN(ms)) {
    return "1:00"; 
  }
  const totalSeconds = Math.floor(ms / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
} 

export function ClockDisplay({ timers, activePlayer, playerStatuses, bonuses = {}, isThreePlayer = true }) {
  const colors = isThreePlayer ? ['WHITE', 'RED', 'BLACK'] : ['WHITE', 'BLACK'];

  return (
    <div className="clock-container">
      {colors.map((color) => {
        const isCurrent = activePlayer === color;
        const remaining = timers[color];
        const status = playerStatuses?.[color] || 'ACTIVE';
        const isDisqualified = status === 'TIMEOUT' || status === 'DEFEATED';
        const hasBonus = bonuses[color];

        return (
          <div 
            key={color} 
            className={`clock-box ${isCurrent ? 'active' : ''} ${isDisqualified ? 'disqualified' : ''}`}
          >
            {hasBonus && <div className="bonus-indicator">+5 Seconds</div>}
            
            <div className="clock-label">
              <span className={`dot dot-${color.toLowerCase()}`}></span>
              {color}
            </div>
            <div className={`clock-time ${remaining < 30000 ? 'low-time' : ''}`}>
              {isDisqualified ? status : formatTime(remaining)}
            </div>
          </div>
        );
      })}
    </div>
  );
}