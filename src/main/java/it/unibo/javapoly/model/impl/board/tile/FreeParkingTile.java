package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Free Parking tile.
 */
@JsonRootName("FreeParkingTile")
public final class FreeParkingTile extends AbstractTile {

    /**
     * Creates a Free Parking tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     */
    @JsonCreator
    public FreeParkingTile(@JsonProperty("position") final int position,
                           @JsonProperty("name") final String name,
                           @JsonProperty("description") final String desc) {
        super(position, TileType.FREE_PARKING, name, desc);
    }

}
