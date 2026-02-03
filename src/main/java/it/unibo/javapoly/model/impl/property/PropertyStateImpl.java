package it.unibo.javapoly.model.impl.property;

import java.util.Objects;

import it.unibo.javapoly.model.api.property.PropertyState;

public class PropertyStateImpl implements PropertyState{

    final private static String BANK_OWN = "Bank";

    private String ownerID; // "" = owned by bank / unsold
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

    @Override
    public String getIdOwner() {
        if (this.ownerID.isEmpty()){
            return this.BANK_OWN;
        }

        return this.ownerID;
    }

    @Override
    public int getHouses() {
        return this.houses;
    }

    @Override
    public int getPurchasePrice() {
        return this.purchasePrice;
    }
    
}
