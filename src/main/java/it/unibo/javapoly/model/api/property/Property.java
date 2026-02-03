package it.unibo.javapoly.model.api.property;

import it.unibo.javapoly.model.impl.card.AbstractPropertyCard;

/**
 * Rappresents a board property in the space.
 * Exposes immutable card data and a read-only view of the dynamic state.
 */
public interface Property {

    /**
     * Returns the unique identifier of this property.
     *
     * @return the id string
     */
    String getId(); 

    /**
     * Returns the immutable economic data for this property.
     *
     * @return the property card
     */
    AbstractPropertyCard getCard();

    /**
     * Returns the read-only state of this property.
     *
     * @return the property state
     */
    PropertyState getState();

    /**
     * Returns the board position of this property.
     *
     * @return position index
     */
    int getPosition();
}
