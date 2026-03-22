// src/api/gameApi.test.js
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import * as gameApi from '../../api/gameApi'
const MOCK_GAME_ID = 'game-123'
const MOCK_RESPONSE = { gameId: MOCK_GAME_ID, currentPlayer: 'WHITE', status: 'IN_PROGRESS', players: [], tiles: [] }

describe('gameApi', () => {
  let fetchMock

  beforeEach(() => {
    fetchMock = vi.fn()
    global.fetch = fetchMock
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('createGame should call correct endpoint and return JSON', async () => {
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => MOCK_RESPONSE
    })

    const result = await gameApi.createGame()
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/three-player/games'),
      expect.objectContaining({ method: 'POST' })
    )
    expect(result).toEqual(MOCK_RESPONSE)
  })

  it('getGameState should call correct endpoint and return JSON', async () => {
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => MOCK_RESPONSE
    })

    const result = await gameApi.getGameState(MOCK_GAME_ID)
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining(`/three-player/games/${MOCK_GAME_ID}`)
    )
    expect(result).toEqual(MOCK_RESPONSE)
  })

  it('makeMove should call correct endpoint with body and return JSON', async () => {
    const moveData = { from: '0,0', to: '0,1', player: 'WHITE' }
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => MOCK_RESPONSE
    })

    const result = await gameApi.makeMove(MOCK_GAME_ID, moveData.from, moveData.to, moveData.player)
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining(`/three-player/games/${MOCK_GAME_ID}/moves`),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify(moveData),
      })
    )
    expect(result).toEqual(MOCK_RESPONSE)
  })

  it('handleResponse should throw error if not ok', async () => {
    fetchMock.mockResolvedValueOnce({
      ok: false,
      status: 400,
      text: async () => JSON.stringify({ message: 'Invalid move' })
    })

    await expect(gameApi.makeMove(MOCK_GAME_ID, '0,0', '0,1')).rejects.toThrow('Invalid move')
  })

  it('getPossibleMoves should call correct endpoint and return array', async () => {
    const moves = ['0,1', '0,2']
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => moves
    })

    const result = await gameApi.getPossibleMoves(MOCK_GAME_ID, '0,0')
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining(`/three-player/games/${MOCK_GAME_ID}/moves?from=0%2C0`)
    )
    expect(result).toEqual(moves)
  })

  // --- 2 Player APIs ---
  it('createTwoPlayerGame should call correct endpoint and return JSON', async () => {
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => MOCK_RESPONSE
    })
    const result = await gameApi.createTwoPlayerGame()
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/two-player/games'),
      expect.objectContaining({ method: 'POST' })
    )
    expect(result).toEqual(MOCK_RESPONSE)
  })

  it('makeTwoPlayerMove should call correct endpoint with body', async () => {
    const moveData = { fromX: 0, fromY: 0, toX: 0, toY: 1 }
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => MOCK_RESPONSE
    })
    const result = await gameApi.makeTwoPlayerMove(MOCK_GAME_ID, moveData)
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining(`/two-player/games/${MOCK_GAME_ID}/moves`),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify(moveData)
      })
    )
    expect(result).toEqual(MOCK_RESPONSE)
  })

  it('getTwoPlayerPossibleMoves should call correct endpoint and return array', async () => {
    const moves = [{ x: 0, y: 1 }]
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => moves
    })

    const result = await gameApi.getTwoPlayerPossibleMoves(MOCK_GAME_ID, 0, 0)
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining(`/two-player/games/${MOCK_GAME_ID}/moves?x=0&y=0`)
    )
    expect(result).toEqual(moves)
  })
})
