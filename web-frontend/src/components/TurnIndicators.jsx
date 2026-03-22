// src/components/TurnIndicators.jsx

import React from 'react'

export function TurnIndicators({ players }) {
  return (
    <div className="turn-indicator-card">
      <h2>Planetary Status</h2>
      <ul>
        {players.map((p) => {
          // Check if player is reaped (Timeout or Defeated)
          const isReaped = p.status === 'TIMEOUT' || p.status === 'DEFEATED';
          
          return (
            <li key={p.id} className={isReaped ? 'status-reaped' : ''}>
              <span
                style={{
                  display: 'inline-block',
                  width: '0.6rem',
                  height: '0.6rem',
                  borderRadius: '999px',
                  marginRight: '0.6rem',
                  backgroundColor:
                    p.status === 'DEFEATED' || p.status === 'TIMEOUT'
                      ? '#555'
                      : p.isCurrent
                      ? '#4ade80' // Green LED
                      : '#888',
                  boxShadow: p.isCurrent && p.status !== 'TIMEOUT' ? '0 0 10px #4ade80' : 'none'
                }}
              />
              <span className="player-id-text">
                {p.id} {isReaped && "💀"}
              </span>
              <span className="player-status-tag">
                {p.isCurrent && !isReaped && " [ACTIVE]"}
                {p.status === 'TIMEOUT' && " [EXTINGUISHED]"}
                {p.status === 'DEFEATED' && " [ELIMINATED]"}
              </span>
            </li>
          );
        })}
      </ul>
    </div>
  )
}