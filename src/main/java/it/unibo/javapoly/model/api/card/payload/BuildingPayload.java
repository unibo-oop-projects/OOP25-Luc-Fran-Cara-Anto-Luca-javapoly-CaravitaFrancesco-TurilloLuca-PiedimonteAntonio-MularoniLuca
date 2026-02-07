package it.unibo.javapoly.model.api.card.payload;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The class represents the payload of a Money object per building.
 * It contains the amount that a player need to pay per house and hotel built.
 */
@JsonRootName("PayloadBuilding")
public class BuildingPayload  implements CardPayload {
    /**
     * Contains the minimum value that the amounts must have.
     */
    private static final int ERR_VALUE = 0;

    /**
     * Contains the String error for the value that the amounts must have.
     */
    private static final String ERR_MSG = "amount >= 0";

    private final int house;
    private final int hotel;

    /**
     * Constructor to create an instance of MoneyPayload.
     *
     * @param house the amount to transfer per house
     * @param hotel the amount to transfer per hotel
     * @throws IllegalArgumentException if the amount is negative
     */
    public BuildingPayload(final int house, final int hotel) {
        if (house < this.ERR_VALUE || hotel < this.ERR_VALUE) {
            throw new IllegalArgumentException(this.ERR_MSG);
        }
        this.house = house;
        this.hotel = hotel;
    }

    /**
     * Returns the amount associated house.
     * 
     * @return the amount
     */
    public int getMoltiplierHouse() {
        return this.house;
    }

    /**
     * Returns the amount associated hotel.
     * 
     * @return the amount
     */
    public int getMoltiplierHotel() {
        return this.hotel;
    }

    /**
     * Returns a string representation of the BuildingPayload object.
     * 
     * @return a string representing the object
     */
    @Override
    public String toString() {
        return "BuildingPayload[house=" + this.house + ", hotel=" + this.hotel + "]";
    }
}
