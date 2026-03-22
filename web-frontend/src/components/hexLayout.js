// src/components/hexLayout.js
// Generic hex-board generator using axial coordinates (q, r).

/**
 * Generate a full hex grid of given radius.
 * q + r + s = 0 for cube coords; here we store q, r and derive s when needed.
 */
export function generateHexBoard(radius = 6) {
    const tiles = []
  
    for (let q = -radius; q <= radius; q++) {
      for (let r = -radius; r <= radius; r++) {
        const s = -q - r
        if (Math.abs(s) > radius) continue 
  
        const isLight = (q + r + radius) % 2 === 0
        const color = isLight ? 'light' : 'dark'
  
        const tileId = `T_${q}_${r}`
  
        tiles.push({
          tileId,
          q,
          r,
          color,
          piece: null, 
        })
      }
    }
  
    return tiles
  }
  
export const fullHexTiles = generateHexBoard(6)
  