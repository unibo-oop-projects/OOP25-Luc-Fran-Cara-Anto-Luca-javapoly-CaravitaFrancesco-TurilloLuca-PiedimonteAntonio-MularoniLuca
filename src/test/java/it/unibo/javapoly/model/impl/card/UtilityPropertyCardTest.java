package it.unibo.javapoly.model.impl.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.PropertyGroup;

class UtilityPropertyCardTest {

    private static final String ID = "U1";
    private static final String NAME = "Compagnia Elettrica";
    private static final String DESCRIPTION = "Forniamo corrente";
    private static final PropertyGroup GROUP = PropertyGroup.UTILITY;

    private static final int PROPERTY_COST = 150;

    private static final int ONE_MULTIPLIER = 4;
    private static final int TWO_MULTIPLIER = 10;

    private static final int DICE_TOTAL_ONE = 7;
    private static final int DICE_TOTAL_TWO = 8;
    private static final int DICE_TOTAL_ZERO = 6;

    @Test
    void rentWithOneUtilityUsesOneMultiplier() {
        final UtilityPropertyCard card = new UtilityPropertyCard(
            ID,
            NAME,
            DESCRIPTION,
            PROPERTY_COST,
            GROUP,
            ONE_MULTIPLIER,
            TWO_MULTIPLIER
        );

        final RentContext ctx = RentContext.forUtilities(DICE_TOTAL_ONE, 1);
        assertEquals(
            DICE_TOTAL_ONE * ONE_MULTIPLIER,
            card.calculateRent(ctx),
            "One utility must use the first multiplier"
        );
    }

    @Test
    void rentWithTwoUtilitiesUsesTwoMultiplier() {
        final UtilityPropertyCard card = new UtilityPropertyCard(
            ID,
            NAME,
            DESCRIPTION,
            PROPERTY_COST,
            GROUP,
            ONE_MULTIPLIER,
            TWO_MULTIPLIER
        );

        final RentContext ctx = RentContext.forUtilities(DICE_TOTAL_TWO, 2);
        assertEquals(
            DICE_TOTAL_TWO * TWO_MULTIPLIER,
            card.calculateRent(ctx),
            "Two utilities must use the second multiplier"
        );
    }

    @Test
    void rentWithZeroUtilitiesFallsBackToOneMultiplier() {
        final UtilityPropertyCard card = new UtilityPropertyCard(
            ID,
            NAME,
            DESCRIPTION,
            PROPERTY_COST,
            GROUP,
            ONE_MULTIPLIER,
            TWO_MULTIPLIER
        );

        final RentContext ctx = RentContext.forUtilities(DICE_TOTAL_ZERO, 0);
        assertThrows(
            NoSuchElementException.class,
            () -> card.calculateRent(ctx)
        );
    }
}
