package it.unibo.javapoly.model.impl.property;

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
public final class PropertyImpl implements Property {

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
    public PropertyImpl(final String id, final int position, final AbstractPropertyCard card) {
        this.id = Objects.requireNonNull(id);
        this.position = position;
        this.card = Objects.requireNonNull(card);
        this.state = new PropertyStateImpl(card.getPropertyCost());
    }

    /**
     * Constructor to create a copy of a passed instance.
     *
     * @param property nstance from which to create a copy
     */
    public PropertyImpl(final Property property) {
        this(property.getId(), property.getPosition(), property.getCard());
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
     * Gets the card associated with this property.
     * 
     * @return the associated card
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
     * Gets the position of the property on the board.
     * 
     * @return the position of the property
     */
    @Override
    public int getPosition() {
        return this.position;
    }

    /**
     * Calculates the rent to be paid for this property, given the rent context.
     * 
     * @param ctx the context of the rent calculation
     * @return the calculated rent
     */
    @Override
    public int getRent(final RentContext ctx) {
        if (this.card != null) {
            return this.card.calculateRent(ctx);
        }
        return 0;
    }

    /**
     * Gets the purchase price of this property.
     * 
     * @return the purchase price of the property
     */
    @Override
    public int getPurchasePrice() {
        return this.state.getPurchasePrice();
    }

    //#endregion

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

        if (!this.state.isOwnedByPlayer() || !this.state.getOwnerId().equals(ownerID)) {
            throw new IllegalStateException("player is not the owner");
        }

        return this.state.addHouse();
    }

    /**
     * Destroy one house on this property for the provided owner.
     * 
     * @param ownerID the owner who wants to remove a house
     * @return true if a house/hotel has been removed, false otherwise
     * @throws IllegalStateException if owner is not the property's owner
     */
    @Override
    public boolean destroyHouse(final String ownerID) {
        if (!isPropertyLand()) {
            return false;
        }

        Objects.requireNonNull(ownerID);

        if (!this.state.isOwnedByPlayer() || !this.state.getOwnerId().equals(ownerID)) {
            throw new IllegalStateException("player is not the owner");
        }

        return this.state.removeHouse();
    }

    /**
     * This method sells this property to {@code buyerID}.
     * Sets {@code buyerID} as new owner only if the bank owns it.
     *
     * @param buyerID the player trying to buy
     * @return true if the new owner is a player, false otherwise
     */
    @Override
    public boolean assignOwner(final String buyerID) {
        if (this.state.isOwnedByPlayer()) {
            return false;
        }

        this.state.setNewOwnerID(Objects.requireNonNull(buyerID));
        return true;
    }

    /**
     * Clears the owner of this property and sets the bank as the new owner.
     */
    @Override
    public void clearOwner() {
        this.state.bankIsNewOwnerID();
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
