package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents an Unexpected tile.
 */
public final class UnexpectedTile extends AbstractTile {

    private final String deckCardID;

    /**
     * Creates an Unexpected tile.
     *
     * @param position the position of the tile on the board
     * @param deckCardID the ID of the deck
     * @param name the tile name
     */
    public UnexpectedTile(final int position,
                          final String name,
                          final String deckCardID) {

        super(position, TileType.UNEXPECTED, name);
        this.deckCardID = deckCardID;
    }

    /**
     * Returns ID of the deck where it should be draw the card.
     *
     * @return the ID of the deck
     */
    public String getDeckID() {
        return this.deckCardID;
    }
}
