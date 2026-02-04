package it.unibo.javapoly.model.api.economy;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.property.Property;
import java.util.List;

/**
 * Model interface responsible only for property and building operations.
 */
public interface PropertyManager {

    /**
     * Retrieves all properties in the game.
     *
     * @return list of all properties.
     */
    List<Property> getAllProperties();

    /**
     * Assigns a property to a player.
     *
     * @param property property to assign
     * @param newOwner the player who will own the property
     * @return {@code true} if assignment was successful, {@code false} if already owned
     */
    boolean assignProperty(Property property, Player newOwner);

    /**
     * Builds a house on a property.
     *
     * @param property the property to build on.
     * @param owner owner of property.
     * @return {@code true} if te house was built successfully, {@code false} otherwise.
     */
    boolean buildHouse(Property property, Player owner);

    /**
     * Destroys a house on a property.
     *
     * @param property the property to remove a house from.
     * @param owner owner of property.
     * @return {@code true} if te house was built successfully, {@code false} otherwise.
     */
    boolean destroyHouse(Property property, Player owner);

    /**
     * Returns a property to the bank (remove ownership).
     *
     * @param property the property to return.
     */
    void returnPropertyToBank(Property property);

    /**
     * Returns all property to the bank.
     *
     * @param player the player whose properties will be removed.
     */
    void removeAllProperty(Player player);

    /**
     * Retrieves all properties owned by a specific player.
     *
     * @param owner the player whose properties to retrieve.
     * @return list of properties owned by the player.
     */
    List<Property> getPropertiesByOwner(Player owner);

    /**
     * Retrieves all properties owned by a specific player with house built on.
     *
     * @param owner the player whose properties to retrieve.
     * @return list of properties owned by the player with house built on.
     */
    List<Property> getPropertiesWithHouseByOwner(Player owner);

}
