package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents the Start tile of the board.
 */
@JsonRootName("StartTile")
public final class StartTile extends AbstractTile {

    private final int reward;

    /**
     * Creates the Start tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     * @param amount the reward gained when passing the tile
     */
    @JsonCreator
    public StartTile(
            @JsonProperty("position") int position,
            @JsonProperty("name") String name,
            @JsonProperty("reward") int reward) {
        super(position, TileType.START, name);
        this.reward = reward;
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
