package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Dice;

public class DiceThrow {
    private final Dice dice1;
    private final Dice dice2;

    public DiceThrow(Dice dice1, Dice dice2) {
        this.dice1 = dice1;
        this.dice2 = dice2;
    }

    public int throwAll() {
        dice1.throwDice();
        dice2.throwDice();
        return dice1.getDicesResult() + dice2.getDicesResult();
    }

    public boolean isDouble() {
        return dice1.getDicesResult() == dice2.getDicesResult();
    }
}
