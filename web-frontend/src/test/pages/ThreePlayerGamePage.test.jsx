// @vitest-environment jsdom

import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import * as matchers from '@testing-library/jest-dom/matchers';

// 1. Extend Vitest with DOM matchers
expect.extend(matchers);

// 2. Import Components
import ThreePlayerGamePage from '../../pages/ThreePlayerGamePage';
import * as gameApi from '../../api/gameApi';

// 3. Define the mock globally
const mockNavigate = vi.fn();

// 4. Mock react-router-dom
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

// 5. Mock the API
vi.mock('../../api/gameApi', () => ({
  createGame: vi.fn(),
  getGameState: vi.fn(),
  getPossibleMoves: vi.fn(),
  makeMove: vi.fn(),
}));

describe('ThreePlayerGamePage', () => {
  const mockGameData = {
    gameId: 'game-123',
    currentPlayer: 'WHITE',
    status: 'IN_PROGRESS',
    players: [
      { id: 'WHITE', status: 'ACTIVE' },
      { id: 'RED', status: 'ACTIVE' },
      { id: 'BLACK', status: 'ACTIVE' },
    ],
    tiles: [],
  };

  beforeEach(() => {
    mockNavigate.mockClear();
    gameApi.createGame.mockResolvedValue(mockGameData);
    gameApi.getPossibleMoves.mockResolvedValue([]);
    gameApi.makeMove.mockResolvedValue(mockGameData);
  });

  it('renders loading initially and then the game board', async () => {
    render(
      <MemoryRouter>
        <ThreePlayerGamePage />
      </MemoryRouter>
    );

    // Check loading state
    expect(screen.getByText(/loading/i)).toBeInTheDocument();

    // Wait for API call
    await waitFor(() => expect(gameApi.createGame).toHaveBeenCalled());

    // Check board rendered
    expect(screen.getByText(/3-player/i)).toBeInTheDocument();
  });

  it('calls navigate when exit button is clicked', async () => {
    render(
      <MemoryRouter>
        <ThreePlayerGamePage />
      </MemoryRouter>
    );

    await waitFor(() => expect(gameApi.createGame).toHaveBeenCalled());

    // Fix: Handle multiple exit buttons
    const exitButtons = screen.getAllByRole('button', { name: /exit to lobby/i });
    
    // Click the first available button
    await userEvent.click(exitButtons[0]);

    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});