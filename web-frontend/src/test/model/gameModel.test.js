import { describe, it, expect } from 'vitest'
import { normalizeGameState, DEFAULT_PLAYERS } from '../../model/gameModel'

describe('gameModel', () => {

  describe('DEFAULT_PLAYERS', () => {
    it('contains three active players', () => {
      expect(DEFAULT_PLAYERS).toHaveLength(3)
      expect(DEFAULT_PLAYERS.map(p => p.id)).toEqual(['WHITE', 'RED', 'BLACK'])
    })
  })

  describe('normalizeGameState', () => {

    it('returns safe defaults when input is null', () => {
      const result = normalizeGameState(null)

      expect(result.gameId).toBe('unknown')
      expect(result.currentPlayer).toBe('WHITE')
      expect(result.status).toBe('UNKNOWN')
      expect(result.players).toEqual([])
      expect(result.tiles).toEqual([])
      expect(result.lastMove).toBeNull()
    })

    it('maps basic game fields correctly', () => {
      const raw = {
        gameId: 'g-123',
        currentPlayer: 'RED',
        status: 'IN_PROGRESS',
        message: 'Test'
      }

      const result = normalizeGameState(raw)

      expect(result.gameId).toBe('g-123')
      expect(result.currentPlayer).toBe('RED')
      expect(result.status).toBe('IN_PROGRESS')
      expect(result.message).toBe('Test')
    })

    it('normalizes players list', () => {
      const raw = {
        players: [
          { id: 'WHITE', status: 'ACTIVE' },
          { id: 'RED', status: 'CHECKED' }
        ]
      }

      const result = normalizeGameState(raw)

      expect(result.players).toEqual([
        { id: 'WHITE', status: 'ACTIVE' },
        { id: 'RED', status: 'CHECKED' }
      ])
    })

    it('normalizes tiles from backend DTO format', () => {
      const raw = {
        tiles: [
          {
            coordinate: '1,-2',
            pieceType: 'ROOK',
            owner: 'WHITE'
          }
        ]
      }

      const result = normalizeGameState(raw)
      const tile = result.tiles[0]

      expect(tile.q).toBe(1)
      expect(tile.r).toBe(-2)
      expect(tile.tileId).toBe('1,-2')
      expect(tile.piece).toEqual({
        type: 'ROOK',
        player: 'WHITE'
      })
    })

    it('calculates hex tile color when backend does not send one', () => {
      const raw = {
        tiles: [
          { q: 2, r: 1 }
        ]
      }

      const tile = normalizeGameState(raw).tiles[0]

      expect(['light', 'medium', 'dark']).toContain(tile.color)
    })

    it('preserves piece object if already normalized', () => {
      const raw = {
        tiles: [
          {
            q: 0,
            r: 0,
            piece: { type: 'KING', player: 'RED' }
          }
        ]
      }

      const tile = normalizeGameState(raw).tiles[0]

      expect(tile.piece).toEqual({
        type: 'KING',
        player: 'RED'
      })
    })

    it('handles board field as tile source', () => {
      const raw = {
        board: [
          { q: 0, r: 0 }
        ]
      }

      const result = normalizeGameState(raw)
      expect(result.tiles).toHaveLength(1)
    })

  })
})
