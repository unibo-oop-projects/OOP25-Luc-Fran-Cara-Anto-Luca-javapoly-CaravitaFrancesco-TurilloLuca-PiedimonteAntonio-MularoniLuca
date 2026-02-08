package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Jail tile.
 */
@JsonRootName("JailTile")
public final class JailTile extends AbstractTile {

    /**
     * Creates the Jail tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     */
    @JsonCreator
    public JailTile(@JsonProperty("position") final int position,
                    @JsonProperty("name") final String name) {
        super(position, TileType.JAIL, name);
    }
}
