package it.unibo.javapoly.controller.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.card.LandPropertyCard;
import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of the PropertyController interface.
 * Manages property ownership, purchases, rent payments, and building construction.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PropertyControllerImpl implements PropertyController {

    private final Map<String, Property> properties;
    private final Map<String, Player> propertyOwners; // propertyId -> Player

    /**
     * Constructs a new PropertyControllerImpl.
     *
     * @param properties map of all properties in the game (propertyId -> Property)
     */
    public PropertyControllerImpl(final Map<String, Property> properties) {
        this.properties = new HashMap<>(properties);
        this.propertyOwners = new HashMap<>();
    }

    /**
     * Constructs a new PropertyControllerImpl.
     *
     * @param properties map of all properties in the game (propertyId -> Property)
     * @param propertyOwners map of all owned property (propertyId -> Player)
     */
    @JsonCreator
    public PropertyControllerImpl(@JsonProperty("properties") final Map<String, Property> properties,
                                  @JsonProperty("propertyOwners") final Map<String, Player> propertyOwners) {
        this.properties = properties == null ? new HashMap<>() : new HashMap<>(properties);
        this.propertyOwners = propertyOwners == null ? new HashMap<>() : new HashMap<>(propertyOwners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean purchaseProperty(final Player player, final String propertyId) {
        final Property property = properties.get(propertyId);

        if (property == null) {
            return false;
        }

        this.properties.get(propertyId).assignOwner(player.getName());
        propertyOwners.put(propertyId, player);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRent(final Player payer, final String propertyId, final int diceRoll) {
        final Property property = properties.get(propertyId);
        final String ownerId = propertyOwners.get(propertyId).getName();

        if (property == null || ownerId == null || ownerId.equals(payer.getName())) {
            return 0;
        }

        final RentContext context = createRentContext(payer, diceRoll, property);

        return property.getRent(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Property> getOwnedProperties(final String playerId) {
        final List<Property> owned = new ArrayList<>();

        for (final Map.Entry<String, Player> entry : propertyOwners.entrySet()) {
            if (entry.getValue().getName().equals(playerId)) {
                owned.add(this.properties.get(entry.getKey()));
            }
        }

        return owned;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean buildHouse(final Player playerId, final String propertyId) {
        final Property property = properties.get(propertyId);

        if (!ownsCompleteGroup(playerId.getName(), property.getPropertyGroup())) {
            return false;
        }

        for (final Property prop : getPropertiesInGroup(property.getPropertyGroup())) {
            if (property.getBuiltHouses() >= prop.getBuiltHouses() + 1) {
                return false;
            }
        }

        return property.buildHouse(playerId.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Property> getPropertiesWithHouseByOwner(final Player owner) {
        Objects.requireNonNull(owner);
        final List<Property> propertyList = getOwnedProperties(owner.getName());
        propertyList.removeIf(p -> p.getState().getHouses() == 0); 
        return propertyList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnPropertyToBank(final Property property) {
        Objects.requireNonNull(property).clearOwner();

        this.propertyOwners.remove(property.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean destroyHouse(final Player player, final String propertyId) {
        return properties.get(propertyId).destroyHouse(player.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPayRent(final Player player, final String propertyId) {
        return this.properties.get(propertyId).isOwnedByPlayer()
        && !this.properties.get(propertyId).playerIsTheOwner(player.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHouseCost(final Property property) {
        if (!(property.getCard() instanceof LandPropertyCard)) {
            return -1;
        }

        final LandPropertyCard card = (LandPropertyCard) property.getCard();
        return card.getHouseCost();
    }

    //#region Private Method

    /**
     * Checks if a player owns all properties in a group (monopoly).
     *
     * @param playerId the player
     * @param group the property group to check
     * @return true if the player owns all properties in the group
     */
    private boolean ownsCompleteGroup(final String playerId, final PropertyGroup group) {
        final List<Property> groupProperties = getPropertiesInGroup(group);

        for (final Property property : groupProperties) {
            if (property.isOwnedByPlayer() && !property.playerIsTheOwner(playerId)) {
                return false;
            }
        }

        return !groupProperties.isEmpty();
    }

    /**
     * Gets all properties belonging to a specific group.
     *
     * @param group the property group
     * @return list of properties in that group
     */
    private List<Property> getPropertiesInGroup(final PropertyGroup group) {
        final List<Property> groupProperties = new ArrayList<>();

        for (final Property property : properties.values()) {
            if (property.getPropertyGroup() == group) {
                groupProperties.add(property);
            }
        }

        return groupProperties;
    }

    private int getNumOwnedPropertyByGroup(final PropertyGroup group, final String playerId) {
        final List<Property> groupProperties = getPropertiesInGroup(group);

        int i = 0;

        for (final Property property : groupProperties) {
            if (property.isOwnedByPlayer() && property.playerIsTheOwner(playerId)) {
                i++;
            }
        }

        return i;
    }

    /**
     * Creates a RentContext for calculating rent.
     *
     * @param owner the ID of the property owner
     * @param diceRoll the current dice roll
     * @param prop the property where we need to take the context
     * @return a RentContext with all necessary information
     */
    private RentContext createRentContext(final Player owner, final int diceRoll, final Property prop) {
        final int numGroup;

        if (prop.getPropertyGroup() == PropertyGroup.UTILITY) {
            numGroup = getNumOwnedPropertyByGroup(PropertyGroup.UTILITY, owner.getName());
            return RentContext.forUtilities(diceRoll, numGroup);
        }

        if (prop.getPropertyGroup() == PropertyGroup.RAILROAD) {
            numGroup = getNumOwnedPropertyByGroup(PropertyGroup.RAILROAD, owner.getName());
            return RentContext.forStation(numGroup);
        }

        return RentContext.forLand(prop.getBuiltHouses(), ownsCompleteGroup(owner.getName(), prop.getPropertyGroup()));
    }

    @Override
    public Player getOwnerByProperty(Property property) {
        return this.propertyOwners.get(property.getId());
    }

    //#endregion
}
