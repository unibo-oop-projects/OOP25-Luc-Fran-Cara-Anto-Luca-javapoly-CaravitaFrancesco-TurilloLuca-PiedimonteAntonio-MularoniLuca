package it.unibo.javapoly.model.api.property;

/**
 * Read-only view of a property's dynamic state.
 * Mutations are performed by authorized services (e.g. Bank).
 */
public interface PropertyState {

    /**
     * Returns the owner ID of the property if present (empty = owned by bank / unsold).
     *
     * @return optional owner
     */
    String getOwnerId();

    /**
     * Returns number of houses on the property (0..5). 
     * (5 indicate hotel)
     *
     * @return number of houses
     */
    int getHouses();

    /**
     * returns the purchase price from the card (convenience).
     *
     * @return purchase price
     */
    int getPurchasePrice();

    /**
     * This method checks if the property has an owner.
     * The owner must be different from the bank.
     * 
     * @return true if there is an owner (!= bank), false otherwise
     */
    boolean isOwnedByPlayer();
}
