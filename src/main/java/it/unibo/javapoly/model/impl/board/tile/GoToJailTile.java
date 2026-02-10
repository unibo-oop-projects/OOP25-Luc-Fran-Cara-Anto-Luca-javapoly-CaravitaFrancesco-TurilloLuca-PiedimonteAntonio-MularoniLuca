package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Go To Jail tile.
 */
@JsonRootName("GoToJailTile")
public final class GoToJailTile extends AbstractTile {

    private final int jailPos;

    /**
     * Creates a Go To Jail tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     * @param jailPos the position of the jail tile
     */
    @JsonCreator
    public GoToJailTile(
            @JsonProperty("position") final int position,
            @JsonProperty("name") final String name,
            @JsonProperty("jailPos") final int jailPos,
            @JsonProperty("description") final String desc) {
        super(position, TileType.GO_TO_JAIL, name, desc);
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
