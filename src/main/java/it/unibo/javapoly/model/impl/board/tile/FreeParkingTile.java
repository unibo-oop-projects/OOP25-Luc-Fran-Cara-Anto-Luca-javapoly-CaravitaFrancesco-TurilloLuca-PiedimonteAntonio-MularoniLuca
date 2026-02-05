package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Free Parking tile.
 */
public final class FreeParkingTile extends AbstractTile {

    /**
     * Creates a Free Parking tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     */
    public FreeParkingTile(final int position, final String name) {
        super(position, TileType.FREE_PARKING, name);
    }

}
