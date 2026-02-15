package it.unibo.javapoly.model.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.Dice;

/**
 * Test class for DiceImpl and DiceThrow.
 */
public final class DiceTest {

    private static final int MAX_SUM = 12;
    private static final int MIN_SUM = 2;
    private static final int REPETITIONS = 1000;

    private Dice dice1;
    private Dice dice2;
    private DiceThrow diceThrow;

    /**
     * Set up the dice and diceThrow before each test.
     */
    @BeforeEach
    void setUp() {
        dice1 = new DiceImpl();
        dice2 = new DiceImpl();
        diceThrow = new DiceThrow(dice1, dice2);
    }

    /**
     * Tests that the dice sum is always within the correct range.
     */
    @Test
    void testDiceRange() {
        for (int i = 0; i < REPETITIONS; i++) {
            diceThrow.throwAll();
            final int sum = diceThrow.getLastThrow();
            final int expectedSum = dice1.getDicesResult() + dice2.getDicesResult();

            assertEquals(expectedSum, sum, "La somma totale deve corrispondere alla somma dei singoli dadi");
            assertTrue(sum >= MIN_SUM && sum <= MAX_SUM, "La somma di due dadi deve esrere tra 2 e 12");
        }
    }

    /**
     * Tests the double result logic.
     */
    @Test
    void testIsDouble() {
        for (int i = 0; i < REPETITIONS; i++) {
            diceThrow.throwAll();
            final boolean isDouble = dice1.getDicesResult() == dice2.getDicesResult();
            assertEquals(isDouble, diceThrow.isDouble(), "isDouble deve essere vero solo se i due dadi sono uguali");
        }
    }

    /**
     * Tests the initial state of the dice throw.
     */
    @Test
    void testInitialState() {
        assertEquals(0, diceThrow.getLastThrow(), "Il risultato iniziale dovrebbe essere 0");
    }
}
