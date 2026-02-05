package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents an Unexpected tile.
 */
public final class UnexpectedTile extends AbstractTile {

    /**
     * Creates an Unexpected tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     */
    public UnexpectedTile(final int position,
                          final String name) {
        super(position, TileType.UNEXPECTED, name);
    }
}
