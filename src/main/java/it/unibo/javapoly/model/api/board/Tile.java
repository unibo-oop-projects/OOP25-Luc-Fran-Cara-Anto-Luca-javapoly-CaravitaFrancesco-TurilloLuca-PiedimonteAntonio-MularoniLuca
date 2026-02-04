package it.unibo.javapoly.model.api.board;

/**
 * Represents a tile on the board.
 */
public interface Tile {

    int getPosition();

    TileType getType();
    
    String getName();
}

