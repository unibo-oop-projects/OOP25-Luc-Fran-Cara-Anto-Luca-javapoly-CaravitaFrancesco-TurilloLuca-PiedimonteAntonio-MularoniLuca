package it.unibo.javapoly.model.api.property;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.impl.card.AbstractPropertyCard;
import it.unibo.javapoly.model.impl.property.PropertyImpl;

/**
 * Contract for a property on the board.
 * 
 * <p>
 * NOTE: This interface exposes operations that the Bank or the Controller can
 * call AFTER performing domain checks (e.g., checking if the player has enough money).
 * The Property does NOT manage the player's money.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PropertyImpl.class, name = "PROPERTYIMPL")
})
public interface Property {

    /**
     * Unique identifier for the property.
     * 
     * @return the unique identifier of the property
     */
    String getId();

    /**
     * Immutable card associated (can be null in test mode).
     * 
     * @return the associated card
     */
    AbstractPropertyCard getCard();

    /**
     * Return the Group type of the property.
     * 
     * @return the propriety group of the proprety
     */
    PropertyGroup getPropertyGroup();

    /**
     * Return the number of houses built.
     * 
     * @return the number of houses built
     */
    int getBuiltHouses();

    /**
     * Return the id of the owner.
     * 
     * @return the id of the owner
     */
    String getIdOwner();

    /**
     * Read-only view of the property's dynamic state.
     * 
     * @return the state of the property
     */
    PropertyState getState();

    /**
     * Position on the board (0-based index).
     * 
     * @return the position of the property on the board
     */
    int getPosition();

    /**
     * Check if an hotel is built on proerty.
     * 
     * @return true if there is, false otherwise
     */
    Boolean hotelIsBuilt();

    /**
     * Assigns the owner via ID.
     *
     * @param ownerId the ID of the player
     * @return true if the owner was successfully assigned, false otherwise
     * @throws IllegalStateException if the property is already owned
     */
    boolean assignOwner(String ownerId);

    /**
     * Removes the owner and resets the state.
     */
    void clearOwner();

    /**
     * Builds a house on the property.
     *
     * @param ownerId the ID of the owner
     * @return true if a new house has been built, false otherwise
     * @throws IllegalStateException if the player is not the owner or the house limit is exceeded
     */
    boolean buildHouse(String ownerId);

    /**
     * Destroys a house on the property.
     *
     * @param ownerId the ID of the owner
     * @return true if a house has been removed, false otherwise
     * @throws IllegalStateException if the player is not the owner or the house limit is exceeded
     */
    boolean destroyHouse(String ownerId);

    /**
     * Calculates the rent to be paid.
     *
     * @param ctx the context of the rent calculation
     * @return the calculated rent
     */
    int getRent(RentContext ctx);

    /**
     * Returns the purchase cost of the property.
     * 
     * @return the purchase price of the property
     */
    int getPurchasePrice();

    /**
     * This method checks if the property has an owner.
     * The owner must be different from the bank.
     * 
     * @return true if there is an owner (!= bank), false otherwise
     */
    boolean isOwnedByPlayer();

    /**
     * This method checks if playerID is the owner.
     * The owner must be different from the bank.
     * 
     * @param playerID the id of the player
     * 
     * @return true if there is the owner (!= bank), false otherwise
     */
    boolean playerIsTheOwner(String playerID);
}
