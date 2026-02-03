package it.unibo.javapoly.model.impl.property;

import java.util.Objects;
import it.unibo.javapoly.model.api.property.PropertyState;

/**
 * Represents the state of a property in the game.
 * It keeps track of the owner and number of houses (or hotel) on the property.
 */
public final class PropertyStateImpl implements PropertyState {

    /**
     * The identifier representing the bank as the owner of a property.
     */
    private static final String BANK_OWN = "Bank";

    /**
     * The error message used when the number of houses is less than 0.
     */
    private static final String ERR_HOUSE = "The number of houses should be >= 0";

    /**
     * The maximum number of hotels that can be built on a property.
     */
    private static final int NUM_HOTEL = 5;

    /**
     * The default number of houses on a property when it is initialized.
     */
    private static final int HOUSE_DEF = 0;

    private String ownerID; // "" = owned by bank ("" = unsold)
    private int houses;
    private final int purchasePrice;

    /**
     * Create a new state for a property.
     *
     * @param purchasePrice the purchase price taken from the card
     */
    public PropertyStateImpl(final int purchasePrice) {
        this.purchasePrice = Objects.requireNonNull(purchasePrice);
        this.ownerID = "";
        this.houses = 0;
    }

    /**
     * Get the ID of the owner of the property.
     *
     * @return the owner's ID, or "Bank" if the property is owned by the bank.
     */
    @Override
    public String getOwnerId() {
        if (this.ownerID.isEmpty()) {
            return this.BANK_OWN;
        }
        return this.ownerID;
    }

    /**
     * Get the number of houses on the property.
     *
     * @return the number of houses (or hotels if more than 5).
     */
    @Override
    public int getHouses() {
        return this.houses;
    }

    /**
     * Get the purchase price of the property.
     *
     * @return the purchase price.
     */
    @Override
    public int getPurchasePrice() {
        return this.purchasePrice;
    }

    /* These are intentionally package-private: only PropertyImpl (same package)
       can call them. Bank or controllers should call PropertyImpl public methods
       such as sellTo(...) or buildHouse(...). */
    /**
     * Set the owner ID of the property.
     *
     * @param id the owner ID to set.
     */
    void setNewOwnerID(final String id) {
        this.ownerID = Objects.requireNonNull(id);
    }

    /**
     * Set the property to be owned by the bank.
     */
    void bankIsNewOwnerID() {
        this.setNewOwnerID("");
    }

    /**
     * Set the number of houses on the property.
     * If the number of houses exceeds 5, it will be considered as a hotel.
     *
     * @param numHouses the number of houses to set (cannot be negative).
     */
    void setHouse(final int numHouses) {
        if (numHouses < 0) {
            throw new IllegalArgumentException(this.ERR_HOUSE);
        }
        this.houses = (numHouses >= this.NUM_HOTEL) ? this.NUM_HOTEL : numHouses;
    }

    /**
     * Add one house to the property.
     * If the property has already reached the hotel, no more houses will be added.
     * 
     * @return true if a new house is added, false otherwise
     */
    boolean addHouse() {
        if (this.houses < this.NUM_HOTEL) {
            setHouse(this.houses + 1);
            return true;
        }
        return false;
    }

    /**
     * Remove one house to the property.
     * If the property has already reached 0 houses, no more houses will be removed.
     * 
     * @return true if a house was removed, false otherwise
     */
    boolean removeHouse() {
        if (this.houses > this.HOUSE_DEF) {
            setHouse(this.houses - 1);
            return true;
        }
        return false;
    }

    /**
     * This method checks if the property has an owner.
     * The owner must be different from the bank.
     * 
     * @return true if there is an owner (!= bank), false otherwise
     */
    boolean isOwnedByPlayer() {
        return !this.getOwnerId().contains(this.BANK_OWN);
    }
}
