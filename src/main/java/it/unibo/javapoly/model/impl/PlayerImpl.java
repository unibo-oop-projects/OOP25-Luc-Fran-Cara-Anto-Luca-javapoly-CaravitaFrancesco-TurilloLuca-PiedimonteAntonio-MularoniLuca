package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.Token;
import it.unibo.javapoly.model.api.TokenType;

/**
 * Implementation of the {@link Player} interface representing a concrete player
 * in the game.
 * This class manages the player's state, balance, position, and token.
 * It delegates turn logic to the current {@link PlayerState} and handles
 * movement and financial transactions.
 * 
 * @see Player
 * @see PlayerState
 * @see Token
 * @see TokenType
 */
public class PlayerImpl implements Player {

    /**
     * Number of spaces on the game board.
     */
    private static final int SPACES_ON_BOARD = 40;

    private final String name;
    private int balance;
    private final Token token;
    private PlayerState currentState;
    private int currentPosition;

    /**
     * Constructs a new PlayerImpl with a specified name, initial balance, and
     * token type.
     * The player starts in the {@link FreeState} and at position 0.
     *
     * @param name           the name of the player.
     * @param initialBalance the starting balance of the player.
     * @param tokenType      the type of token associated with the player.
     * @see FreeState
     * @see TokenFactory
     */
    public PlayerImpl(final String name, final int initialBalance, final TokenType tokenType) {
        this.name = name;
        this.balance = initialBalance;
        this.token = TokenFactory.createToken(tokenType);
        this.currentState = FreeState.getInstance();
        this.currentPosition = 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see PlayerState
     * @see FreeState
     * @see JailedState
     * @see BankruptState
     */
    @Override
    public void playTurn(final int diceResult, final boolean isDouble) {
        this.currentState.playTurn(this, diceResult, isDouble);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Player
     * @see PlayerState
     * @see FreeState
     * @see JailedState
     * @see BankruptState
     */
    @Override
    public void move(final int steps) {
        final int oldPos = currentPosition;
        currentPosition = (currentPosition + steps) % SPACES_ON_BOARD;

        System.out.println(// NOPMD
                "DEBUG: " + name + " (" + token.getType() + ") si sposta da " + oldPos + " a " + currentPosition);
        // TODO notificare la View (Observer) e il Tabellone
    }

    /**
     * Attempts to pay a specified amount from the player's balance.
     *
     * @param amount the amount of money to pay.
     * @return true if the player has enough balance and the payment was successful;
     *         false otherwise.
     */
    @Override
    public boolean tryToPay(final int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds a specified amount of money to the player's balance.
     *
     * @param amount the amount of money to receive.
     */
    @Override
    public void receiveMoney(final int amount) {
        this.balance += amount;
    }

    // --- Getter e Setter ---

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCurrentPosition() {
        return this.currentPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBalance() {
        return balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken() {
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerState getState() {
        return currentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setState(final PlayerState state) {
        this.currentState = state;
        System.out.println("Stato cambiato in: " + state.getClass().getSimpleName()); // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Player{" + name + ", " + balance + "$, " + currentState.getClass().getSimpleName() + "}";
    }
}
