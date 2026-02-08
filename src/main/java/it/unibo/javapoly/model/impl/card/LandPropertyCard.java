package it.unibo.javapoly.model.impl.card;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.PropertyGroup;

/**
 * Representation of a land/property card in the Monopoly-like game.
 *
 * <p>
 * The class stores the rents for different numbers of houses and for the hotel,
 * together with the costs to build houses and hotels.
 */
@JsonRootName("LandPropertyCard")
public class LandPropertyCard extends AbstractPropertyCard {

    /**
     * Multiplier applied when all lands of the same group are owned by a single player.
     */
    private static final int ALL_LAND = 2;

    // FIXME: Valutare se trasformalo in una Map<Integer, Integer> -> <numHouse, numHouseRent>
    private final List<Integer> rents;

    private final int housePrice;
    private final int hotelPrice;

    /**
     * Creates a new {@link LandPropertyCard}.
     *
     * @param id the card identifier
     * @param name the card name
     * @param description the card description
     * @param propertyCost the cost of the property
     * @param color the property color
     * @param baseRent the base rent
     * @param rents the list of rents for houses
     * @param hotelRent the hotel rent
     * @param houseCost the cost to build a house
     * @param hotelCost the cost to build a hotel
     */
    @JsonCreator
    public LandPropertyCard(
            @JsonProperty("id") final String id,
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("propertyCost") final int propertyCost,
            @JsonProperty("color") final PropertyGroup color,
            @JsonProperty("baseRent") final int baseRent,
            @JsonProperty("rents") final List<Integer> rents,
            @JsonProperty("hotelRent") final int hotelRent,
            @JsonProperty("houseCost") final int houseCost,
            @JsonProperty("hotelCost") final int hotelCost) {
        super(id, name, description, propertyCost, color);
        this.rents = new LinkedList<>(rents);
        this.rents.addFirst(baseRent);
        this.rents.addLast(hotelRent);
        this.housePrice = houseCost;
        this.hotelPrice = hotelCost;
    }

    //#region Getter

    /**
     * This method returns the base price (land rent) that a player must pay.
     *
     * @return the base rent.
     */
    public int getBaseRent() {
        if (checkListIsEmpty()) {
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }
        return this.rents.get(0);
    }

    /**
     * This method returns the hotel rent that a player must pay.
     *
     * @return the hotel rent.
     */
    public int getHotelRent() {
        if (checkListIsEmpty()) {
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }
        return this.rents.get(this.rents.size() - 1);
    }

    /**
     * This method returns the cost to build a new house.
     *
     * @return the house cost.
     */
    public int getHouseCost() {
        return this.housePrice;
    }

    /**
     * This method returns the cost to build the hotel.
     *
     * @return the hotel cost.
     */
    public int getHotelCost() {
        return this.hotelPrice;
    }

    /**
     * This method returns the rent based on the number of houses built.
     *
     * @param houseNumber the number of houses built
     * @return the rent for the given number of houses.
     */
    public int getHouseRentByNumber(final int houseNumber) {
        if (checkListIsEmpty()) {
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }
        if (checkIsHotel(houseNumber)) {
            return getHotelRent();
        }
        return this.rents.get(houseNumber);
    }

    /**
     * This method returns all rents based on the number of houses built.
     *
     * @return a copy of the full rent list.
     */
    public List<Integer> getAllRent() {
        if (checkListIsEmpty()) {
            // FIXME: Valutare se restituire un errore o semplicemente restituire una lista vuota
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }
        return new LinkedList<>(this.rents);
    }

    //#endregion

    /**
     * This method calculates the rent based on the number of houses or the presence of a hotel.
     *
     * @param rentContext contains the number of houses built and if i have all house of the same color
     * @return the calculated rent.
     */
    @Override
    public int calculateRent(final RentContext rentContext) {
        if (checkIsHotel(rentContext.getNumberOfHouses())) {
            return applyMultiplier(rentContext, getHotelRent());
        }

        return applyMultiplier(rentContext, getHouseRentByNumber(rentContext.getNumberOfHouses()));
    }

    //#region Private method

    /**
     * This method checks if the passed number is out of the list limits.
     *
     * @param number the index to check
     * @return true if the number is out of bounds, false otherwise.
     */
    private boolean checkNumberHouse(final int number) {
        return number < 0 || number >= this.rents.size();
    }

    /**
     * Applies the group multiplier to the given rent result when all lands of
     * the same group are owned by the same player.
     *
     * @param context the rent calculation context containing ownership flags.
     * @param result the base rent to which the multiplier may be applied.
     * @return the possibly multiplied rent.
     */
    private int applyMultiplier(final RentContext context, final int result) {
        return context.isAllLand() ? result * this.ALL_LAND : result;
    }

    /**
     * This method checks if the passed number represents the hotel.
     *
     * @param number the index to check
     * @return true if the number represents the hotel, false otherwise.
     */
    private boolean checkIsHotel(final int number) {
        if (checkNumberHouse(number)) {
            throw new IndexOutOfBoundsException(ERR_INDEX_OUT_LIMITS + number);
        }
        return number == this.rents.size() - 1;
    }

    /**
     * This method checks if the list of rents is null or empty.
     *
     * @return true if the rent list is empty, false otherwise.
     */
    private boolean checkListIsEmpty() {
        if (this.rents == null) {
            throw new IllegalStateException(ERR_LIST_IS_NULL);
        }
        return this.rents.isEmpty();
    }

    //#endregion

}
