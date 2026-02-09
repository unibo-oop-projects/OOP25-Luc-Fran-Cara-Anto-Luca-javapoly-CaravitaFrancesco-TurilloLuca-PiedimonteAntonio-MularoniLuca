package it.unibo.javapoly.model.api.card.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The class represents the payload of a relative move operation.
 * It contains the delta of the move.
 */
@JsonRootName("PayLoadMoveRelative")
public final class MoveRelativePayload implements CardPayload {

    private final int delta;

    /**
     * Constructor to create an instance of MoveRelativePayload.
     *
     * @param delta the delta of the move
     */
    @JsonCreator
    public MoveRelativePayload(@JsonProperty("delta") final int delta) {
        this.delta = delta;
    }

    /**
     * Returns the delta associated with this payload.
     * 
     * @return the delta of the move
     */
    public int getDelta() {
        return this.delta;
    }

    /**
     * Returns a string representation of the MoveRelativePayload object.
     * 
     * @return a string representing the object
     */
    @Override
    public String toString() {
        return "MoveRelativePayload[delta=" + this.delta + "]";
    }
}
