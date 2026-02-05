package it.unibo.javapoly.model.api.board;

/**
 * Represents the game board.
 */
public interface Board {

    /**
     * Returns the number of tiles on the board.
     *
     * @return the board size
     */
    int size();

    /**
     * Returns the tile at the given position.
     *
     * @param position the position of the tile
     * @return the tile at the specified position
     */
    Tile getTileAt(int position);

    /**
     * Normalizes a position so that it wraps around the board size.
     *
     * @param position the raw position
     * @return the normalized position within board bounds
     */
    int normalizePosition(int position);

}
