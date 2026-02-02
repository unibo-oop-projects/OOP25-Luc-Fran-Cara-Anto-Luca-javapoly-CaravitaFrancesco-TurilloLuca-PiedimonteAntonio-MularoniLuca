package it.unibo.javapoly.model.impl;

import java.util.Random;

import it.unibo.javapoly.model.api.Dice;

public class DiceImpl implements Dice{
    private int randResult;
    private Random rand = new Random();

    @Override
    public void ThrowDice() {
        randResult = rand.nextInt(6) + 1; 
    }

    public int getDicesResult(){
        return randResult;
    }

}
