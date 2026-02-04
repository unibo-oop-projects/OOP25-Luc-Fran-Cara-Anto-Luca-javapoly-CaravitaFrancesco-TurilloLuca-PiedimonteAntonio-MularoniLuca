package it.unibo.javapoly.model.impl;

import java.util.Random;

import it.unibo.javapoly.model.api.Dice;

public class DiceImpl implements Dice{
    private static final int NUM_FACE = 6;

    private int randResult;
    private final Random rand = new Random();

    @Override
    public void throwDice() {
        randResult = rand.nextInt(NUM_FACE) + 1; 
    }

    @Override
    public int getDicesResult() {
        return randResult;
    }
}
