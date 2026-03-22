// @vitest-environment jsdom

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import * as matchers from '@testing-library/jest-dom/matchers';

expect.extend(matchers);

import { TurnIndicators } from '../../components/TurnIndicators';

describe('TurnIndicators Component', () => {
  const playersMock = [
    { id: 'WHITE', status: 'ACTIVE', isCurrent: true },
    { id: 'RED', status: 'CHECKED', isCurrent: false },
  ];

  it('renders all player names', () => {
    render(<TurnIndicators players={playersMock} />);
    playersMock.forEach((p) => {
      // FIX: Use getAllByText and check the first instance
      const elements = screen.getAllByText(new RegExp(p.id, 'i'));
      expect(elements[0]).toBeInTheDocument();
    });
  });

  it('renders the current player label', () => {
    render(<TurnIndicators players={playersMock} />);
    // FIX: Use getAllByText
    const currentPlayers = screen.getAllByText(/WHITE/i);
    expect(currentPlayers[0]).toHaveTextContent('(current)');
  });

  it('renders status indicators correctly', () => {
    render(<TurnIndicators players={playersMock} />);
    // FIX: Use getAllByText
    const redPlayers = screen.getAllByText(/RED/i);
    expect(redPlayers[0]).toHaveTextContent('· X'); 
  });

  it('renders the colored dot', () => {
    render(<TurnIndicators players={playersMock} />);
    const listItems = screen.getAllByRole('listitem');
    expect(listItems[0].querySelector('span')).toHaveStyle('background-color: #4ade80');
  });
});