package it.unibo.javapoly.model.api.card.payload;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * The class represents the payload for a move to the nearest category(TileType) operation.
 * It contains the destination category for the move.
 */
@JsonRootName("PayLoadMoveToNearest")
public final class MoveToNearestPayload implements CardPayload {

    private final TileType category;

    /**
     * Constructor to create an instance of MoveToNearestPayload.
     * 
     * @param category the destination category for the move
     * @throws NullPointerException if the category is null
     */
    @JsonCreator
    public MoveToNearestPayload(@JsonProperty("category") final TileType category) {
        this.category = Objects.requireNonNull(category, "category must not be null");
    }

    /**
     * Returns the category associated with this payload.
     * 
     * @return the destination category
     */
    public TileType getCategory() {
        return this.category;
    }

    /**
     * Returns a string representation of the MoveToNearestPayload object.
     * 
     * @return a string representing the object
     */
    @Override
    public String toString() {
        return "MoveToNearestPayload[cat=" + this.category + "]";
    }
}
