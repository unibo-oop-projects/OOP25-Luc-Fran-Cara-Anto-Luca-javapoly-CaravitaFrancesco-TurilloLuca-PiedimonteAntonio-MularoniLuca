package it.unibo.javapoly.model.api.card.payload;

/**
 * The class represents the payload for a move to a specific target position.
 * It contains the target position for the move.
 */
public final class MoveToPayload implements CardPayload {

    private final int targetPosition;

    /**
     * Constructor to create an instance of MoveToPayload.
     *
     * @param targetPosition the target position for the move
     */
    public MoveToPayload(final int targetPosition) {
        this.targetPosition = targetPosition;
    }

    /**
     * Returns the target position associated with this payload.
     * 
     * @return the target position
     */
    public int getTargetPosition() {
        return this.targetPosition;
    }

    /**
     * Returns a string representation of the MoveToPayload object.
     * 
     * @return a string representing the object
     */
    @Override
    public String toString() {
        return "MoveToPayload[target=" + this.targetPosition + "]";
    }
}
