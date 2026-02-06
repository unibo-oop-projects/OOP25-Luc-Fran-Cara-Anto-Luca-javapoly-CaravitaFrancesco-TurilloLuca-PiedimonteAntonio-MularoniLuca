package it.unibo.javapoly.model.impl.economy;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.economy.PropertyManager;
import it.unibo.javapoly.model.api.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the PropertyManager interface.
 */
public final class PropertyManagerImpl implements PropertyManager {

    private final List<Property> allProperties;

    /**
     * Create a PropertyManager with the given list of properties.
     *
     * @param allProperties the list of all properties in the game.
     */
    public PropertyManagerImpl(final List<Property> allProperties) {
        Objects.requireNonNull(allProperties);
        this.allProperties = new ArrayList<>(allProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Property> getAllProperties() {
        return new ArrayList<>(this.allProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean buildHouse(final Property property, final Player owner) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(owner);
        return property.buildHouse(owner.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean destroyHouse(final Property property, final Player owner) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(owner);
        return property.destroyHouse(owner.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Property> getPropertiesByOwner(final Player owner) {
        Objects.requireNonNull(owner);
        final List<Property> properties = new ArrayList<>();
        for (final Property property : allProperties) {
            if (property.getState().getOwnerId().equals(owner.getName())) {
                properties.add(property);
            }
        }
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Property> getPropertiesWithHouseByOwner(final Player owner) {
        Objects.requireNonNull(owner);
        final List<Property> properties = getPropertiesByOwner(owner);
        for (final Property property : properties) {
            if (property.getState().getHouses() == 0) {
                properties.remove(property);
            }
        }
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnPropertyToBank(final Property property) {
        Objects.requireNonNull(property);
        property.clearOwner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllProperty(final Player owner) {
        Objects.requireNonNull(owner);
        final List<Property> properties = getPropertiesByOwner(owner);
        for (final Property property : properties) {
            property.clearOwner();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean assignProperty(final Property property, final Player newOwner) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(newOwner);
        return property.assignOwner(newOwner.getName());
    }
}
