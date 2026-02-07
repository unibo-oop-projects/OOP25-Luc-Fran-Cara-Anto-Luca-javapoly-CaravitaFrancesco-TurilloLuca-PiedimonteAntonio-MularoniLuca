package it.unibo.javapoly.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerObserver;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.Token;
import it.unibo.javapoly.model.api.TokenType;

/**
 * Implementation of the {@link Player} interface representing a concrete player
 * in the game.
 * This class manages the player's state, balance, position, and token.
 * It delegates turn logic to the current {@link PlayerState} and handles
 * movement and financial transactions.
 * It also implements the Observer pattern to notify registered observers
 * about state changes.
 * 
 * @see Player
 * @see PlayerState
 * @see Token
 * @see TokenType
 * @see PlayerObserver
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

    private final List<PlayerObserver> observers = new ArrayList<>();

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
     * <p>
     * Notifies observers about the movement.
     * </p>
     * 
     * @see Player
     * @see PlayerState
     * @see FreeState
     * @see JailedState
     * @see BankruptState
     */
    @Override
    public void move(final int steps) {
        final int oldPos = this.currentPosition;
        this.currentPosition = (this.currentPosition + steps) % SPACES_ON_BOARD;

        notifyMoved(oldPos, this.currentPosition);

        System.out.println(// NOPMD
                "DEBUG: " + this.name + " (" + this.token.getType() + ") si sposta da " + oldPos + " a "
                        + this.currentPosition);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Notifies observers about the balance change if the payment is successful.
     * </p>
     */
    @Override
    public boolean tryToPay(final int amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            notifyBalanceChanged(this.balance);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Notifies observers about the balance change.
     * </p>
     */
    @Override
    public void receiveMoney(final int amount) {
        this.balance += amount;
        notifyBalanceChanged(this.balance);
    }

    // --- Observer Pattern Methods ---

    /**
     * {@inheritDoc}
     */
    @Override
    public void addObserver(final PlayerObserver observer) {
        this.observers.add(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObserver(final PlayerObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Notifies all registered observers about the player's movement.
     * 
     * @param oldPos the previous position of the player.
     * @param newPos the new position of the player.
     */
    private void notifyMoved(final int oldPos, final int newPos) {
        for (final PlayerObserver obs : this.observers) {
            obs.onPlayerMoved(this, oldPos, newPos);
        }
    }

    /**
     * Notifies all registered observers about the player's balance change.
     * 
     * @param newBalance the new balance of the player.
     */
    private void notifyBalanceChanged(final int newBalance) {
        for (final PlayerObserver obs : this.observers) {
            obs.onBalanceChanged(this, newBalance);
        }
    }

    /**
     * Notifies all registered observers about the player's state change.
     * 
     * @param oldState the previous state of the player.
     * @param newState the new state of the player.
     */
    private void notifyStateChanged(final PlayerState oldState, final PlayerState newState) {
        for (final PlayerObserver obs : this.observers) {
            obs.onStateChanged(this, oldState, newState);
        }
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
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBalance() {
        return this.balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken() {
        return this.token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerState getState() {
        return this.currentState;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Notifies observers about the state change if the new state is different from
     * the old state.
     * </p>
     */
    @Override
    public void setState(final PlayerState state) {
        final PlayerState oldState = this.currentState;
        this.currentState = state;
        if (!oldState.getClass().equals(state.getClass())) {
            notifyStateChanged(oldState, state);
        }
        System.out.println("Stato cambiato in: " + state.getClass().getSimpleName()); // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Player{" + this.name + ", " + this.balance + "$, " + this.currentState.getClass().getSimpleName() + "}";
    }
}
