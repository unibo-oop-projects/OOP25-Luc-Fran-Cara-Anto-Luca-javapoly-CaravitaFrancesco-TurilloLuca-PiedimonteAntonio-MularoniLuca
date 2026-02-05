package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;

/**
 * Abstract base implementation of a board tile.
 * 
 * <p>
 * This class provides common state and behavior for all tile implementations.
 */
public abstract class AbstractTile implements Tile {

    private final int position;
    private final TileType type;
    private final String name;

    /**
     * Creates a tile with the given position, type and name.
     *
     * @param position the position of the tile on the board
     * @param type the tile type
     * @param name the tile name
     */
    protected AbstractTile(final int position, final TileType type, final String name) {
        this.position = position;
        this.type = type;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
}
