package it.unibo.javapoly.model.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unibo.javapoly.model.api.Dice;

/**
 * Class that manages the throwing of two dice and stores the result.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class DiceThrow {
    private final Dice dice1;
    private final Dice dice2;
    private int lastDiceResult;

    /**
     * Constructor for DiceThrow.
     *
     * @param dice1 the first die.
     * @param dice2 the second die.
     */
    @JsonCreator
    public DiceThrow(@JsonProperty("dice1") final Dice dice1,
                     @JsonProperty("dice2") final Dice dice2) {
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.lastDiceResult = 0;
    }

    public Dice getDice1() {
        return this.dice1;
    }

    public Dice getDice2() {
        return this.dice2;
    }

    /**
     * Throws both dice and updates the total result.
     */
    public void throwAll() {
        this.dice1.throwDice();
        this.dice2.throwDice();
        this.lastDiceResult = dice1.getDicesResult() + dice2.getDicesResult();
    }

    /**
     * Returns the result of the last throw.
     *
     * @return the sum of the two dice.
     */
    public int getLastThrow() {
        return this.lastDiceResult;
    }

    /**
     * Checks if the last throw was a double (both dice have the same value).
     *
     * @return true if it's a double, false otherwise.
     */
    @JsonIgnore
    public boolean isDouble() {
        return this.dice1.getDicesResult() == this.dice2.getDicesResult();
    }
}
