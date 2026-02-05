package it.unibo.javapoly.model.api;

/**
 * Context class for rent operations.
 *
 * <p>
 * Required by a {@code AbstractPropertyCard} to compute the rent
 */
public class RentContext {
    private final int diceTotal;
    private final int ownedUtilities;
    private final int numberHouse;
    private final int numberStations;
    private final boolean allLand;  // true if the player owned all the land of the same group

    /**
     * Base constructor with all parameters.
     *
     * @param diceTotal       is the sum of the two rolled dice (0 if is not applicable)
     * @param ownedUtilities  is the number of utilities owned by player
     * @param numberHouse     is the number of the house built on a land (from {@code 0} - {@code 5} represents a hotel)
     * @param numberStations     is the number of the station owned (from {@code 0} - {@code 4})
     * @param allLand         true if the owner owns all land of the same group
     */
    public RentContext(final int diceTotal, final int ownedUtilities, final int numberHouse, 
                       final int numberStations, final boolean allLand) {
        this.diceTotal = diceTotal;
        this.ownedUtilities = ownedUtilities;
        this.numberStations = numberStations;
        this.numberHouse = numberHouse;
        this.allLand = allLand;
    }

    //#region Getter
    /**
     * Returns the total value of the dice roll.
     *
     * @return the dice total, or {@code 0} if not applicable
     */
    public int getDiceTotal() {
        return this.diceTotal;
    }

    /**
     * Returns the number of utilities owned by the player.
     *
     * @return the number of owned utilities
     */
    public int getOwnedUtilities() {
        return this.ownedUtilities;
    }

    /**
     * Returns the number of houses built on the property.
     *
     * <p>
     * A value of {@code 5} represents the presence of a hotel.
     * </p>
     *
     * @return the number of houses or hotel indicator
     */
    public int getNumberOfHouses() {
        return this.numberHouse;
    }

    /**
     * Returns the number of houses built on the property.
     *
     * <p>
     * A value of {@code 5} represents the presence of a hotel.
     * </p>
     *
     * @return the number of houses or hotel indicator
     */
    public int getNumberOfStation() {
        return this.numberStations;
    }

    /**
     * This method checks if the player owned all Land of the same group.
     *
     * @return {@code true} if the owner owns all properties of the group,
     *         {@code false} otherwise
     */
    public boolean isAllLand() {
        return this.allLand;
    }
    //#endregion

    //#region Static Method for new instance
    /**
     * method for LandProperty. 
     * This method create an instance of RentContext for LandProperty
     *
     * @param numberHouse     is the number of the house built on a land (from {@code 0} - {@code 5} represents a hotel)
     * @param allLand         true if the owner owns all land of the same group
     * 
     * @return an instance of RentContext ready for LandProperty
     */
    public static RentContext forLand(final int numberHouse, final boolean allLand) {
        return new RentContext(0, 0, numberHouse, 0, allLand);
    }

    /**
     * method for StationProperty. 
     * This method create an instance of RentContext for StationProperty
     *
     * @param numberStations     is the number of the station owned built on a land (from {@code 0} - {@code 4})
     * 
     * @return an instance of RentContext ready for StationProperty
     */
    public static RentContext forStation(final int numberStations) {
        return new RentContext(0, 0, 0, numberStations, false);
    }

    /**
     * method for UtilityProperty. 
     * This method create an instance of RentContext for UtilityProperty.
     * 
     * @param diceTotal       is the sum of the two rolled dice (0 if is not applicable)
     * @param ownedUtilities  is the number of utilities owned by player
     * 
     * @return an instance of RentContext ready for UtilityProperty
     */
    public static RentContext forUtilities(final int diceTotal, final int ownedUtilities) {
        return new RentContext(diceTotal, ownedUtilities, 0, 0, false);
    }
    //#endregion

    /**
     * Returns a human-readable representation of this rent context.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "RentContext[diceTotal=" + this.diceTotal + ", ownedUtilities=" + this.ownedUtilities
                + ", numberOfHouses=" + this.numberHouse + ", isMonopoly=" + this.allLand + "]";
    }
}
