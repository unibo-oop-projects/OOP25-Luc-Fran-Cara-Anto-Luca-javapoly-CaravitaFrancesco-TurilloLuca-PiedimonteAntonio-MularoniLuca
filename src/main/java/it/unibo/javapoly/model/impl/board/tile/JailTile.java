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
     * @param type the tile type
     * @param name the tile name
     */
    public JailTile(final int position, final TileType type, final String name) {
        super(position, type, name);
    }
}
