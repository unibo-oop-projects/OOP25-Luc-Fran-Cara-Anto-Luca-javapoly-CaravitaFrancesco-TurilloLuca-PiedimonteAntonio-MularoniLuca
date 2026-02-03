package it.unibo.javapoly.model.api.property;

/**
 * Read-only view of a property's dynamic state.
 * Mutations are performed by authorized services (e.g. Bank).
 */
public interface PropertyState {
    //FIXME: Valutare se restituire il riferimento dell'oggetto Player o solo ID
    /**
     * Returns the owner ID of the property if present (empty = owned by bank / unsold).
     *
     * @return optional owner
     */
    String getIdOwner();

    /**
     * Returns number of houses on the property (0..5). 
     * (5 indicate hotel)
     *
     * @return number of houses
     */
    int getHouses();

    /**
     * Convenience: returns the purchase price from the card.
     *
     * @return purchase price
     */
    int getPurchasePrice();
}
