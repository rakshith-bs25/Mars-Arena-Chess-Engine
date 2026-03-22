package jchess;

/**
 * Minimal shim so we can compile and attach a debugger.
 * We’ll flesh this out in Exercise 3.
 */
public class BoardModel {
    private final Square[][] squares;

    /** For now we just wrap the Chessboard’s squares array. */
    public BoardModel(Square[][] backing) {
        this.squares = backing;
    }

    /** Same contract as Chessboard.squares[x][y] used to have. */
    public Square getSquare(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return squares[x][y];
    }

    /** Optional helper if you temporarily route pixel lookups here. */
    public Square getSquareByPixels(int px, int py, Chessboard board) {
        // Keep behavior identical to existing pixel→square math if you call this.
        return board.getSquare(px, py); // delegate for now
    }

    /** Expose raw array while we migrate gradually. */
    public Square[][] view() {
        return squares;
    }
}