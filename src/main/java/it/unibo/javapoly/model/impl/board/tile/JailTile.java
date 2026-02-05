package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Jail tile.
 */
public final class JailTile extends AbstractTile {

    /**
     * Creates the Jail tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     */
    public JailTile(final int position, final String name) {
        super(position, TileType.JAIL, name);
    }
}
