package it.unibo.javapoly.model.impl.property;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Objects;
import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.api.property.PropertyState;
import it.unibo.javapoly.model.impl.card.AbstractPropertyCard;

/**
 * Represents a property on the board, including its state and card information.
 * This class manages the property's ownership, rent calculation, and house construction.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PropertyImpl implements Property {

    private static final int IS_HOTEL = 5;

    private final String id;
    private final int position;
    private final AbstractPropertyCard card;
    private final PropertyStateImpl state;

    /**
     * Create a property tile.
     *
     * @param id unique id
     * @param position board index
     * @param card associated card
     */
    @JsonCreator
    public PropertyImpl(
            @JsonProperty("id") final String id, 
            @JsonProperty("position") final int position, 
            @JsonProperty("card") final AbstractPropertyCard card) {
        this.id = Objects.requireNonNull(id);
        this.position = position;
        this.card = Objects.requireNonNull(card);
        this.state = new PropertyStateImpl(card.getPropertyCost());
    }

    /**
     * Constructor to create a copy of a passed instance.
     *
     * @param property instance from which to create a copy
     */
    public PropertyImpl(final Property property) {
        this(property.getId(), property.getPosition(), property.getCard());

        if (!property.isOwnedByPlayer()) {
            return;
        }

        this.state.setNewOwnerID(property.getState().getOwnerId());
        this.state.setHouse(property.getState().getHouses());
    }

    //#region Getter

    /**
     * Gets the unique identifier for this property.
     * 
     * @return the unique identifier of the property
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractPropertyCard getCard() {
        return this.card;
    }

    /**
     * Gets the current state of the property.
     * 
     * @return the state of the property
     */
    @Override
    public PropertyState getState() {
        return new PropertyStateImpl(this.state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return this.position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRent(final RentContext ctx) {
        if (this.card != null) {
            return this.card.calculateRent(ctx);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPurchasePrice() {
        return this.state.getPurchasePrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyGroup getPropertyGroup() {
        return this.card.getGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdOwner() {
        return this.state.getOwnerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBuiltHouses() {
        return this.state.getHouses();
    }

    //#endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOwnedByPlayer() {
        return !this.state.bankIsTheOwner();
    }

    /**
     * Build one house on this property for the provided owner.
     * 
     * @param ownerID the owner who wants to build
     * @return true if a new house/hotel has been built, false otherwise
     * @throws IllegalStateException if owner is not the property's owner
     */
    @Override
    public boolean buildHouse(final String ownerID) {
        if (!isPropertyLand()) {
            return false;
        }

        Objects.requireNonNull(ownerID);

        if (!this.isOwnedByPlayer() || !playerIsTheOwner(ownerID)) {
            throw new IllegalStateException("player is not the owner");
        }

        return this.state.addHouse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean destroyHouse(final String ownerID) {
        if (!isPropertyLand()) {
            return false;
        }

        Objects.requireNonNull(ownerID);

        if (!this.isOwnedByPlayer() || !playerIsTheOwner(ownerID)) {
            throw new IllegalStateException("player is not the owner");
        }

        return this.state.removeHouse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean assignOwner(final String buyerID) {
        if (isOwnedByPlayer()) {
            return false;
        }

        this.state.setNewOwnerID(Objects.requireNonNull(buyerID));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearOwner() {
        this.state.bankIsNewOwnerID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerIsTheOwner(final String playerID) {
        return this.state.getOwnerId().equals(playerID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean hotelIsBuilt() {
        return this.getBuiltHouses() == this.IS_HOTEL;
    }

    /** 
     * Determines if this property is a land property or a special type of property like a utility or railroad.
     * 
     * @return true if the property is not a land property, false otherwise
     */
    private boolean isPropertyLand() {
        return !(PropertyGroup.RAILROAD == this.card.getGroup() || PropertyGroup.UTILITY == this.card.getGroup());
    }

}
