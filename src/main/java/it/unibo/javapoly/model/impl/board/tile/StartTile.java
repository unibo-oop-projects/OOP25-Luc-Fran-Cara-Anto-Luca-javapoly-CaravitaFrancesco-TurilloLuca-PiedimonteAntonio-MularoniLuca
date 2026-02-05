package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Start tile of the board.
 */
public final class StartTile extends AbstractTile {

    private final int reward;

    /**
     * Creates the Start tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     * @param amount the reward gained when passing the tile
     */
    public StartTile(final int position,
                     final String name,
                     final int amount) {
        super(position, TileType.START, name);
        this.reward = amount;
    }

    /**
     * Returns the reward obtained when passing the Start tile.
     *
     * @return the pass reward
     */
    public int getPassReward() {
        return this.reward;
    }
}
