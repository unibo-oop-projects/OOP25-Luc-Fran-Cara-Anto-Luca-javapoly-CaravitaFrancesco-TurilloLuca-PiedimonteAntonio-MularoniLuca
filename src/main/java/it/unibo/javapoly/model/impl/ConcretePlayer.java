package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.Token;
import it.unibo.javapoly.model.api.TokenType;

/**
 * Implementation of the {@link Player} interface representing a concrete player
 * in the game.
 * This class manages the player's state, balance, position, and token.
 */
public class ConcretePlayer implements Player {

    private static final int SPACES_ON_BOARD = 40;
    private final String name;
    private int balance;
    private final Token token;
    private PlayerState currentState;

    // Placeholder per la posizione (da 0 a 39)
    private int currentPosition;

    /**
     * Constructs a new ConcretePlayer with a specified name, initial balance, and
     * token type.
     * The player starts in the {@link FreeState} and at position 0.
     *
     * @param name           the name of the player.
     * @param initialBalance the starting balance of the player.
     * @param tokenType      the type of token associated with the player.
     */
    public ConcretePlayer(final String name, final int initialBalance, final TokenType tokenType) {
        this.name = name;
        this.balance = initialBalance;
        this.token = TokenFactory.createToken(tokenType);
        this.currentState = FreeState.getInstance();
        this.currentPosition = 0;
    }

    /**
     * Executes the player's turn based on the dice result.
     * The logic is delegated to the current {@link PlayerState}.
     *
     * @param diceResult the total value rolled on the dice.
     */
    @Override
    public void playTurn(final int diceResult) {
        this.currentState.playTurn(this, diceResult);
    }

    /**
     * Moves the player a specific number of steps on the board.
     * Limits the position to the board size (0-39) using circular movement logic.
     *
     * @param steps the number of spaces to move.
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
        // TODO gestire la bancarotta o ipoteca
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
     * Gets the name of the player.
     *
     * @return the player's name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the current balance of the player.
     *
     * @return the player's balance.
     */
    @Override
    public int getBalance() {
        return balance;
    }

    /**
     * Gets the token associated with the player.
     *
     * @return the player's token.
     */
    @Override
    public Token getToken() {
        return token;
    }

    /**
     * Gets the current state of the player.
     *
     * @return the current {@link PlayerState}.
     */
    @Override
    public PlayerState getState() {
        return currentState;
    }

    /**
     * Sets the state of the player.
     *
     * @param state the new {@link PlayerState} to set.
     */
    @Override
    public void setState(final PlayerState state) {
        this.currentState = state;
        System.out.println("Stato cambiato in: " + state.getClass().getSimpleName()); // NOPMD
    }

    /**
     * Returns a string representation of the player.
     *
     * @return a string containing the player's name, balance, and current state.
     */
    @Override
    public String toString() {
        return "Player{" + name + ", " + balance + "$, " + currentState.getClass().getSimpleName() + "}";
    }
}
