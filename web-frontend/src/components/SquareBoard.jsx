import React from 'react';
import PieceIcon from './PieceIcon';
import './SquareBoard.css';

export function SquareBoard({ boardData, onTileClick, selectedTile, legalMoves }) {
    
    const getPiece = (x, y) => {
        if (!boardData) return null;
        return boardData.find(p => p.x === x && p.y === y);
    };

    // Helper to check if x,y is a legal move
    const isLegal = (x, y) => {
        return legalMoves && legalMoves.includes(`${x},${y}`);
    };

    const renderSquares = () => {
        const squares = [];
        for (let y = 7; y >= 0; y--) {
            for (let x = 0; x < 8; x++) {
                const isBlackSquare = (x + y) % 2 !== 0; 
                const piece = getPiece(x, y);
                
                const isSelected = selectedTile && selectedTile.x === x && selectedTile.y === y;
                const isTarget = isLegal(x, y);

                squares.push(
                    <div 
                        key={`${x}-${y}`}
                        className={`square ${isBlackSquare ? 'dark' : 'light'} ${isSelected ? 'selected' : ''} ${isTarget ? 'legal-target' : ''}`}
                        onClick={() => onTileClick(x, y)}
                    >
                        {piece && (
                            <div className="piece-container">
                                <PieceIcon piece={{ type: piece.pieceType, player: piece.owner }} />
                            </div>
                        )}
                        {/* Little dot for legal moves on empty squares */}
                        {isTarget && !piece && <div className="legal-dot"></div>}
                        {/* Ring for legal captures */}
                        {isTarget && piece && <div className="capture-ring"></div>}
                    </div>
                );
            }
        }
        return squares;
    };

    return (
        <div className="square-board-wrapper">
             <div className="ranks">
                {[8,7,6,5,4,3,2,1].map(r => <div key={r} className="rank">{r}</div>)}
            </div>
            <div className="board-area">
                <div className="square-board-grid">
                    {renderSquares()}
                </div>
                <div className="files">
                    {['A','B','C','D','E','F','G','H'].map(f => <div key={f} className="file">{f}</div>)}
                </div>
            </div>
        </div>
    );
}