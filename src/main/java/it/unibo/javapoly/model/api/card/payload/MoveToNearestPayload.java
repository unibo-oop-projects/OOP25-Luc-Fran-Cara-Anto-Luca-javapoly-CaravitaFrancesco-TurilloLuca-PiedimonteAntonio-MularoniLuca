package it.unibo.javapoly.model.api.card.payload;

import java.util.Objects;

import it.unibo.javapoly.model.api.property.PropertyGroup;

/**
 * The class represents the payload for a move to the nearest category operation.
 * It contains the destination category for the move.
 */
public final class MoveToNearestPayload implements CardPayload {

    private final PropertyGroup category;

    /**
     * Constructor to create an instance of MoveToNearestPayload.
     * 
     * @param category the destination category for the move
     * @throws NullPointerException if the category is null
     */
    public MoveToNearestPayload(final PropertyGroup category) {
        this.category = Objects.requireNonNull(category, "category must not be null");
    }

    /**
     * Returns the category associated with this payload.
     * 
     * @return the destination category
     */
    public PropertyGroup getCategory() {
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
