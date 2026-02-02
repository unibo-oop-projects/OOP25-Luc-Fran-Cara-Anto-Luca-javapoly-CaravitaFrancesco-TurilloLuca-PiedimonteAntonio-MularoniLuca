package it.unibo.javapoly.model.impl.card;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonRootName;

// TODO: add all the JavaDoc comment

/**
 * Representation of a land/property card in the Monopoly-like game.
 * 
 * <p>
 * The class stores the rents for different numbers of houses and for the hotel,
 * together with the costs to build houses and hotels.
 */
@JsonRootName("LandPropertyCard")
public class LandPropertyCard extends AbstractPropertyCard {

    private static final String ERR_LIST_IS_EMPTY = "The rent list is empty";
    private static final String ERR_LIST_IS_NULL = "The rent list is null";
    private static final String ERR_INDEX_OUT_LIMITS = "The given index is out of size";

    // FIXME: riguardare il nome perche non è totalmente in inglese
    // TODO: Valutare se trasformalo in una Map<Integer, Integer> -> <numHouse, numHouseRent>
    private final List<Integer> multiProroprietyRent; 

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
     * @param multiProroprietyRent the list of rents for houses
     * @param hotelRent the hotel rent
     * @param houseCost the cost to build a house
     * @param hotelCost the cost to build a hotel
     */
    public LandPropertyCard(final String id,
                             final String name,
                             final String description,
                             final int propertyCost,
                             final String color,
                             final int baseRent,
                             final List<Integer> multiProroprietyRent,
                             final int hotelRent,
                             final int houseCost,
                             final int hotelCost) {
        super(id, name, description, propertyCost, color);
        this.multiProroprietyRent = new LinkedList<>(multiProroprietyRent);
        this.multiProroprietyRent.addFirst(baseRent);
        this.multiProroprietyRent.addLast(hotelRent);
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
        return this.multiProroprietyRent.get(0);
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
        return this.multiProroprietyRent.get(this.multiProroprietyRent.size() - 1);
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
    public int getNumberHouseRent(final int houseNumber) {
        if (checkListIsEmpty()) {
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY);
        }
        if (checkIsHotel(houseNumber)) {
            return getHotelRent();
        }
        return this.multiProroprietyRent.get(houseNumber);
    }

    /**
     * This method returns all rents based on the number of houses built.
     *
     * @return a copy of the full rent list.
     */
    public List<Integer> getMultiHouseRent() {
        if (checkListIsEmpty()) {
            // TODO: Valutare se restituire un errore o semplicemente restituire una lista vuota
            throw new NoSuchElementException(ERR_LIST_IS_EMPTY); 
        }
        return new LinkedList<>(this.multiProroprietyRent);
    }

    //#endregion

    /**
     * This method calculates the rent based on the number of houses or the presence of a hotel.
     *
     * @param houseNumber the number of houses built
     * @return the calculated rent.
     */
    @Override
    public int calculateRent(final int houseNumber) {
        return checkIsHotel(houseNumber) ? getHotelRent() : getNumberHouseRent(houseNumber);
    }

    /**
     * This method checks if the passed number is out of the list limits.
     *
     * @param number the index to check
     * @return true if the number is out of bounds, false otherwise.
     */
    private boolean checkNumberHouse(final int number) {
        return number < 0 || number >= this.multiProroprietyRent.size();
    }

    /**
     * This method checks if the passed number represents the hotel.
     *
     * @param number the index to check
     * @return true if the number represents the hotel, false otherwise.
     */
    private boolean checkIsHotel(final int number) {
        if (checkNumberHouse(number)) {
            throw new IndexOutOfBoundsException(ERR_INDEX_OUT_LIMITS);
        }
        return number == this.multiProroprietyRent.size() - 1;
    }

    /**
     * @return true if the rent list is empty, false otherwise.
     */
    private boolean checkListIsEmpty() {
        if (this.multiProroprietyRent == null) {
            throw new IllegalStateException(ERR_LIST_IS_NULL);
        }
        return this.multiProroprietyRent.isEmpty();
    }

    // FIXME: Capire bene cosa fare con l'indice per la lista. 
    // Perche bisogna vedere se vogliamo fare indice-1 o lasciare indice. 
    // Quindi bisogna vedere se vogliamo mettere baseRent nella posizione 0 
    // del arraylist e hotelRent nell'ultima posizione
}
