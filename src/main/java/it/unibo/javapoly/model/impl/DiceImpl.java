package it.unibo.javapoly.model.impl;

import java.util.Random;

import it.unibo.javapoly.model.api.Dice;

public class DiceImpl implements Dice {
    private int randResult;
    private final Random rand = new Random();

    @Override
    public void throwDice() {
        randResult = rand.nextInt(6) + 1;
    }

    @Override
    public int getDicesResult() {
        return randResult;
    }
}
