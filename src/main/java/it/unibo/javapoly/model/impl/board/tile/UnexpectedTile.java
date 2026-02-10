package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents an Unexpected tile.
 */
@JsonRootName("UnexpectedTile")
public final class UnexpectedTile extends AbstractTile {

    private final String deckCardID;

    /**
     * Creates an Unexpected tile.
     *
     * @param position the position of the tile on the board
     * @param deckCardID the ID of the deck
     * @param name the tile name
     */
    @JsonCreator
    public UnexpectedTile(
            @JsonProperty("position") final int position,
            @JsonProperty("name") final String name,
            @JsonProperty("deckCardID") final String deckCardID,
            @JsonProperty("description") final String desc) {

        super(position, TileType.UNEXPECTED, name, desc);
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
