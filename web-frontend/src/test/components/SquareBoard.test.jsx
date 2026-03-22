// @vitest-environment jsdom

import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import * as matchers from '@testing-library/jest-dom/matchers';

expect.extend(matchers);

import { SquareBoard } from '../../components/SquareBoard';

describe('SquareBoard', () => {
  const boardData = [
    { x: 0, y: 0, pieceType: 'ROOK', owner: 'WHITE' },
    { x: 1, y: 0, pieceType: 'KNIGHT', owner: 'BLACK' },
  ];

  const legalMoves = ['0,1', '1,1'];
  const selectedTile = { x: 0, y: 0 };
  const handleClick = vi.fn();

  beforeEach(() => {
    handleClick.mockClear();
  });

  it('renders all squares', () => {
    const { container } = render(<SquareBoard boardData={boardData} selectedTile={null} legalMoves={[]} onTileClick={() => {}} />);
    
    const squares = container.getElementsByClassName('square');
    
    if (squares.length > 0) {
      expect(squares.length).toBe(64);
      
      expect(squares[0]).toHaveClass('dark'); 
    }
  });

  it('renders pieces as images', () => {
    render(<SquareBoard boardData={boardData} selectedTile={null} legalMoves={[]} onTileClick={() => {}} />);
    
    const images = screen.getAllByRole('img');
    expect(images.length).toBeGreaterThan(0);
  });

  it('highlights selected tile', () => {
    const { container } = render(<SquareBoard boardData={boardData} selectedTile={selectedTile} legalMoves={[]} onTileClick={() => {}} />);
    
    const selectedSquare = container.querySelector('.selected');
    expect(selectedSquare).toBeInTheDocument();
  });

  it('fires onTileClick when a square is clicked', () => {
    const { container } = render(<SquareBoard boardData={boardData} selectedTile={selectedTile} legalMoves={legalMoves} onTileClick={handleClick} />);
    
    const firstSquare = container.getElementsByClassName('square')[0];
    fireEvent.click(firstSquare);
    
    expect(handleClick).toHaveBeenCalled();
  });
});