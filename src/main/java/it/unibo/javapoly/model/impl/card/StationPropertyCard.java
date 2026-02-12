package it.unibo.javapoly.model.impl.card;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.PropertyGroup;

/**
 * Representation of a station card in the Monopoly game.
 * The class stores the rents for different numbers of station.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StationPropertyCard extends AbstractPropertyCard {

    // Indicates the rents that a player needs to pay based on the number of stations owned.
    private final List<Integer> rentNumberSation;

    /**
     * Creates a new {@link StationPropertyCard}.
     *
     * @param id the card identifier.
     * @param name the card name.
     * @param description the card description.
     * @param propetyCost the cost of the property.
     * @param rents the list of rents for 1..n stations (ordered).
     */
    @JsonCreator
    public StationPropertyCard(
            @JsonProperty("id") final String id,
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("propertyCost") final int propetyCost,
            @JsonProperty("rents") final List<Integer> rents) {
        super(id, name, description, propetyCost, PropertyGroup.RAILROAD);
        this.rentNumberSation = new ArrayList<>(rents);
    }

    //#region Getter

    /**
     * Returns all station rents.
     *
     * <p>
     * The returned list is a direct reference to the internal rents list.
     *
     * @return the list of rents for each number of stations.
     */
    @JsonIgnore
    public List<Integer> getAllRent() {
        if (checkListIsEmpty()) {
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }

        return new ArrayList<>(this.rentNumberSation);
    }

    /**
     * This method returns the rent based on the number of station owned.
     *
     * @param stationNumber the number of station owned.
     *
     * @return the rent for the given number of station.
     */
    public int getStationRentByNumber(final int stationNumber) {
        if (checkListIsEmpty()) {
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }

        if (checkNumberStation(stationNumber)) {
            throw new IndexOutOfBoundsException(ERR_INDEX_OUT_LIMITS + stationNumber);
        }
        return this.rentNumberSation.get(stationNumber - 1);
    }

    //#endregion

    //#region Private method

    /**
     * This method checks if the passed number is out of the list limits.
     *
     * @param number the index to check.
     * @return true if the number is out of bounds, false otherwise.
     */
    private boolean checkNumberStation(final int number) {
        return number < 1 || number > this.rentNumberSation.size();
    }

    /**
     * This method checks if the list of rents is null or empty.
     *
     * @return true if the rent list is empty, false otherwise.
     */
    private boolean checkListIsEmpty() {
        if (this.rentNumberSation == null) {
            throw new IllegalStateException(ERR_LIST_IS_NULL);
        }
        return this.rentNumberSation.isEmpty();
    }
    //#endregion

    /**
     * Calculates the rent for this station card according to the provided rent context.
     *
     * <p>
     * Contract: this method expects {@code RentContext#getNumberOfStation()} to be in the
     * valid range (1..n). It will throw {@link IndexOutOfBoundsException} if the requested
     * station index is invalid.
     *
     * @param rentContext the rent calculation context (dice total is ignored for stations).
     * @return the calculated rent for the provided context.
     */
    @Override
    public int calculateRent(final RentContext rentContext) {
        return getStationRentByNumber(rentContext.getNumberOfStation());
    }

}
