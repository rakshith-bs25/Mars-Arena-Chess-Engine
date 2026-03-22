import React from 'react';

export function GameOverModal({ isOpen, winner, rankings, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content mars-modal">
        <div className="modal-icon">👑</div>
        <h2 className="modal-title">MARS RECKONING COMPLETE</h2>
        
        <div className="royalty-announcement">
          WE FOUND THE ROYALTY OF PLANET MARS
        </div>

        <div className="rankings-list">
          {rankings && rankings.map((rank, index) => (
            <div key={index} className={`rank-item rank-${rank.type}`}>
              {}
              <span className="rank-color" style={{ color: rank.hex }}>{rank.name}:</span>
              <span className="rank-desc">{rank.text} {rank.emoji}</span>
            </div>
          ))}
        </div>

        {!rankings && (
           <p className="modal-text">
            <span className="winner-name">{winner}</span> wins the match!
           </p>
        )}

        <button className="modal-close-button" onClick={onClose}>
          START NEXT BLITZ
        </button>
      </div>
    </div>
  );
}