// @vitest-environment jsdom

import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import * as matchers from '@testing-library/jest-dom/matchers';

// 1. Extend Vitest with DOM matchers
expect.extend(matchers);

// 2. Import Components
import TwoPlayerGamePage from '../../pages/TwoPlayerGamePage';
import * as gameApi from '../../api/gameApi';

// 3. Define the mock globally so vi.mock can see it
const mockNavigate = vi.fn();

// 4. Mock react-router-dom
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

// 5. Mock the API
vi.mock('../../api/gameApi', () => ({
  createTwoPlayerGame: vi.fn(),
  makeTwoPlayerMove: vi.fn(),
  getTwoPlayerPossibleMoves: vi.fn(),
}));

describe('TwoPlayerGamePage', () => {
  const mockGameData = {
    gameId: 'game-2p-123',
    currentPlayer: 'WHITE',
    status: 'IN_PROGRESS',
    board: [{ x: 0, y: 0, pieceType: 'ROOK', owner: 'WHITE' }],
  };

  beforeEach(() => {
    mockNavigate.mockClear();
    gameApi.createTwoPlayerGame.mockResolvedValue(mockGameData);
    gameApi.getTwoPlayerPossibleMoves.mockResolvedValue([]);
    gameApi.makeTwoPlayerMove.mockResolvedValue(mockGameData);
  });

  it('renders loading initially and then the game board', async () => {
    render(
      <MemoryRouter>
        <TwoPlayerGamePage />
      </MemoryRouter>
    );

    // Check loading state
    expect(screen.getByText(/loading/i)).toBeInTheDocument();

    // Wait for API call
    await waitFor(() => expect(gameApi.createTwoPlayerGame).toHaveBeenCalled());

    // Check board rendered (using regex to be safe on casing)
    expect(screen.getByText(/2-player/i)).toBeInTheDocument();
  });

  it('calls navigate when exit button is clicked', async () => {
    render(
      <MemoryRouter>
        <TwoPlayerGamePage />
      </MemoryRouter>
    );

    await waitFor(() => expect(gameApi.createTwoPlayerGame).toHaveBeenCalled());

    // Fix: Handle multiple exit buttons (e.g. mobile/desktop versions)
    const exitButtons = screen.getAllByRole('button', { name: /exit to lobby/i });
    
    // Click the first available button
    await userEvent.click(exitButtons[0]);

    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});