package it.unibo.javapoly.model.api.board;

/**
 * Represents a tile on the board.
 */
public interface Tile {

    /**
     * Returns the position of the tile on the board.
     *
     * @return the tile position
     */
    int getPosition();

    /**
     * Returns the type of the tile.
     *
     * @return the tile type
     */
    TileType getType();

    /**
     * Returns the name of the tile.
     *
     * @return the tile name
     */
    String getName();
}
