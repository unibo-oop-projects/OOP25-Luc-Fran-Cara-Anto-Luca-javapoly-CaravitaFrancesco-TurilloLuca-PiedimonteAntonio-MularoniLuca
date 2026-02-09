package it.unibo.javapoly.model.impl.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.PropertyGroup;

/**
 * Representation of a utility card in the Monopoly game. This card computes rent based on
 * how many utilities the owner possesses and the dice total.
 */
public class UtilityPropertyCard extends AbstractPropertyCard {

    private final int oneOwnedMultiplier;
    private final int bothOwnedMultiplier;

    /**
     * Constructor for UtilityPropertyCard.
     *
     * @param id                  the card id.
     * @param name                the card name.
     * @param description         the card description.
     * @param propertyCost        the cost of the property.
     * @param oneOwnedMultiplier  multiplier when one utility is owned.
     * @param bothOwnedMultiplier multiplier when both utilities are owned.
     */
    @JsonCreator
    public UtilityPropertyCard(
            @JsonProperty("id") final String id,
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("propertyCost") final int propertyCost,
            @JsonProperty("oneOwnedMultiplier") final int oneOwnedMultiplier,
            @JsonProperty("bothOwnedMultiplier") final int bothOwnedMultiplier) {
        super(id, name, description, propertyCost, PropertyGroup.UTILITY);
        this.oneOwnedMultiplier = oneOwnedMultiplier;
        this.bothOwnedMultiplier = bothOwnedMultiplier;
    }

    //#region Getter

    /**
     * Returns a list containing the available multipliers.
     *
     * @return a copy of the multipliers list.
     */
    public List<Integer> getAllMultiplier() {
        final List<Integer> result = new ArrayList<>();
        result.add(this.oneOwnedMultiplier);
        result.add(this.bothOwnedMultiplier);
        return result;
    }

    /**
     * Returns the multiplier corresponding to the given number of owned utilities.
     *
     * @param number the number of owned utilities (1 or 2).
     * @return the multiplier for the given number.
     * @throws NoSuchElementException if {@code number} is not valid.
     */
    private int getTheMultiplier(final int number) {
        if (checkNumberUtility(number)) {
            throw new NoSuchElementException(ERR_INDEX_OUT_LIMITS + number);
        }
        return getAllMultiplier().get(number - 1);
    }

    /**
     * Returns the multiplier when one utility is owned.
     *
     * @return multiplier for one owned utility.
     */
    public int getOneOwnedMultiplier() {
        return this.oneOwnedMultiplier;
    }

    /**
     * Returns the multiplier when both utilities are owned.
     *
     * @return multiplier for both owned utilities.
     */
    public int getBothOwnedMultiplier() {
        return this.bothOwnedMultiplier;
    }

    //#endregion

    /**
     * Calculates the rent for this utility property.
     *
     * @param rentContext the context for rent calculation (dice total and owned utilities).
     * @return the calculated rent.
     */
    @Override
    public int calculateRent(final RentContext rentContext) {
        return rentContext.getDiceTotal() * getTheMultiplier(rentContext.getOwnedUtilities());
    }

    /**
     * Checks whether the provided utility number is invalid.
     *
     * @param number to check.
     * @return {@code true} if the number is not valid (less than 1 or greater than 2), {@code false} otherwise.
     */
    private boolean checkNumberUtility(final int number) {
        return number < 1 || number > 2;
    }
}
