package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Dice;

public class DiceThrow {
    private Dice dice1;
    private Dice dice2;

    public DiceThrow(Dice dice1, Dice dice2) {
        this.dice1 = dice1;
        this.dice2 = dice2;
    }

    public int throwAll() {
        dice1.ThrowDice();
        dice2.ThrowDice();
        return dice1.getDicesResult() + dice2.getDicesResult();
    }

    public boolean isDouble() {
        return dice1.getDicesResult() == dice2.getDicesResult();
    }
}
