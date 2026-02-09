package it.unibo.javapoly.controller.api;

import it.unibo.javapoly.model.api.player.Player;
import it.unibo.javapoly.model.api.property.Property;

import java.util.List;

/**
 * Controller responsible for managing property transactions and rent calculations.
 */
public interface PropertyController {

    /**
     * Handles a player attempting to purchase a property.
     * 
     * @param player the player that is attempting to purchase
     * @param propertyId the ID of the property to purchase
     * @return true if the purchase was successful, false otherwise
     */
    boolean purchaseProperty(Player player, String propertyId);

    /**
     * Calculates and processes the rent payment when a player lands on an owned property.
     * 
     * @param payer the ID of the player who must pay rent
     * @param propertyId the ID of the property landed on
     * @param diceRoll the current dice roll (needed for utilities)
     * @return the amount of rent paid
     */
    int getRent(Player payer, String propertyId, int diceRoll);

    /**
     * Gets all properties owned by a specific player.
     * 
     * @param playerID the ID of the player
     * @return a list of properties owned by the player
     */
    List<Property> getOwnedProperties(String playerID);

    /**
     * Builds a house on a property.
     * 
     * @param player the ID of the player building
     * @param propertyId the ID of the property
     * @return true if the house was successfully built
     */
    boolean buildHouse(Player player, String propertyId);

    /**
     * Destroy a house on a property.
     * 
     * @param player the player building
     * @param propertyId the ID of the property
     * @return true if the house was successfully built
     */
    boolean destroyHouse(Player player, String propertyId);

    /**
     * Is invoked by BoardController and check if he need to pay the rent.
     * 
     * <p>
     * This method check the player whose land on this property 
     * have to pay the rent of the property
     * 
     * @param player the player building
     * @param propertyId the ID of the property
     * @return true if the house was successfully built
     */ 
    boolean checkPayRent(Player player, String propertyId); //FIXME: cambiare il nome del metodo

    /**
     * Returns a property to the bank (remove ownership).
     *
     * @param property the property to return.
     */
    void returnPropertyToBank(Property property);

    /**
     * Retrieves all properties owned by a specific player with house built on.
     *
     * @param owner the player whose properties to retrieve.
     * @return list of properties owned by the player with house built on.
     */
    List<Property> getPropertiesWithHouseByOwner(Player owner);

    /**
     * This method return the cost of a property to build a house.
     * 
     * @param property where we need to take the cost
     * @return the cost to buil a house
     */
    int getHouseCost(Property property);

}
