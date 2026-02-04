package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Go To Jail tile.
 */
public final class GoToJailTile extends AbstractTile {

    private final int jailPos;

    /**
     * Creates a Go To Jail tile.
     *
     * @param position the position of the tile on the board
     * @param type the tile type
     * @param name the tile name
     * @param jailPos the position of the jail tile
     */
    public GoToJailTile(final int position,
                        final TileType type,
                        final String name,
                        final int jailPos) {
        super(position, type, name);
        this.jailPos = jailPos;
    }

    /**
     * Returns the position of the jail tile.
     *
     * @return the jail tile position
     */
    public int getJailPosition() {
        return this.jailPos;
    }
}
