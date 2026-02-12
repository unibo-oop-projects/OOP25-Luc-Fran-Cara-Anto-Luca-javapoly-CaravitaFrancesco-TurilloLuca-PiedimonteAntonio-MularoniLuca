package it.unibo.javapoly.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerObserver;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.Token;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.utils.ValidationUtils;

/**
 * Concrete implementation of the {@link Player} interface.
 * 
 * <p>
 * This class encapsulates the complete status of a player within the game,
 * including their identity, financial resources (balance), board position, and
 * the game piece (Token) they use.
 * </p>
 * 
 * <p>
 * <strong>Behavioral Logic:</strong>
 * The class employs the <em>State Pattern</em> regarding turn execution.
 * The {@link #playTurn(int, boolean)} method delegates logic to the current
 * {@link PlayerState}, allowing the player's capabilities to
 * change dynamically (e.g., when moving normally vs. when in jail).
 * </p>
 * 
 * <p>
 * <strong>Event Notification:</strong>
 * Implementing the <em>Observer Pattern</em>, this class notifies registered
 * {@link PlayerObserver}s whenever critical properties change (position
 * updates, balance transactions, or state transitions), facilitating loose
 * coupling with the UI and Game Controller.
 * </p>
 * 
 * @see Player
 * @see PlayerState
 * @see Token
 * @see TokenType
 * @see PlayerObserver
 */
public class PlayerImpl implements Player {

    private static final int DEFAULT_STARTING_BALANCE = 1500;

    private final String name;
    private int balance;
    private final Token token;
    private final TokenType tokenType;
    private PlayerState currentState;
    private int currentPosition;
    private String customTokenPath;
    private final List<PlayerObserver> observers = new ArrayList<>();

    /**
     * Constructs a new {@link PlayerImpl} with a specified name and token type.
     * This constructor initializes the player with the default starting balance of
     * {@value #DEFAULT_STARTING_BALANCE}.
     * 
     * <p>
     * This constructor is intended to be used by controllers and views, hiding
     * rule implementation details (such as the specific starting balance) from
     * components that do not need to know them, effectively applying standard
     * game rules.
     * </p>
     *
     * @param name      the name of the player.
     * @param tokenType the type of token associated with the player.
     * @throws NullPointerException     if {@code name} or {@code tokenType} is
     *                                  null.
     * @throws IllegalArgumentException if {@code name} is blank.
     */
    public PlayerImpl(final String name, final TokenType tokenType) {
        this(name, DEFAULT_STARTING_BALANCE, tokenType, null);
    }

    /**
     * Constructs a new {@link PlayerImpl} with a specified name, token type, and a
     * path for a custom token image.
     * 
     * <p>
     * This constructor is intended for use when the player selects the
     * {@link TokenType#CUSTOM} token type and uploads or selects a specific file
     * to represent their token in the game. It initializes the player with the
     * default starting balance.
     * </p>
     *
     * @param name            the name of the player.
     * @param tokenType       the type of token associated with the player.
     * @param customTokenPath the file path to the custom token image.
     */
    public PlayerImpl(final String name, final TokenType tokenType, final String customTokenPath) {
        this(name, DEFAULT_STARTING_BALANCE, tokenType, customTokenPath);
    }

    /**
     * Constructs a new {@link PlayerImpl} with a specified name, initial balance,
     * and token type.
     * 
     * <p>
     * The player starts in the {@link FreeState} and at position 0.
     * The parameters passed to this constructor are used for saving and loading
     * the player's state from a JSON file.
     * </p>
     * 
     * <p>
     * <strong>Usage Note:</strong>
     * Unlike the primary constructor which uses default game values, this
     * constructor allows full customization of the initial balance. Ideally, it
     * should be used for:
     * <ul>
     * <li><strong>Unit Testing:</strong> To create scenarios with specific balance
     * conditions.</li>
     * <li><strong>Serialization/Persistence:</strong> To load a player's state from
     * a saved game (e.g., via JSON) preserving their exact balance.</li>
     * <li><strong>Custom Variations:</strong> To support house rules or specific
     * configurations where starting money differs from the standard rules.</li>
     * </ul>
     * </p>
     *
     * @param name            the name of the player (JSON property "name").
     * @param initialBalance  the starting balance of the player (JSON property
     *                        "balance").
     * @param tokenType       the type of token associated with the player (JSON
     *                        property "tokenType").
     * @param customTokenPath the file path for a custom token image (JSON property
     *                        "customTokenPath"), used only if {@code tokenType} is
     *                        {@link TokenType#CUSTOM}.
     * @throws NullPointerException     if {@code name} or {@code tokenType} is
     *                                  null.
     * @throws IllegalArgumentException if {@code name} is blank or
     *                                  {@code initialBalance} is negative.
     * @see FreeState
     * @see TokenFactory
     */
    @JsonCreator
    public PlayerImpl(@JsonProperty("name") final String name,
            @JsonProperty("balance") final int initialBalance,
            @JsonProperty("tokenType") final TokenType tokenType,
            @JsonProperty("customTokenPath") final String customTokenPath) {

        ValidationUtils.requireNonNull(name, "Name cannot be null");
        ValidationUtils.requireNonNull(tokenType, "Token type cannot be null");
        ValidationUtils.requireNonBlank(name, "Name cannot be blank");
        ValidationUtils.requireNonNegative(initialBalance, "Initial balance cannot be negative");

        this.name = name;
        this.balance = initialBalance;
        this.tokenType = tokenType;
        this.token = TokenFactory.createToken(tokenType);
        this.currentState = FreeState.getInstance();
        this.currentPosition = 0;
        this.customTokenPath = customTokenPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playTurn(final int potentialDestination, final boolean isDouble) {
        ValidationUtils.requireNonNegative(potentialDestination, "Potential destination cannot be negative");
        this.currentState.playTurn(this, potentialDestination, isDouble);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(final int newPosition) {
        ValidationUtils.requireNonNegative(newPosition, "The position cannot be negative");

        final int oldPos = this.currentPosition;
        this.currentPosition = newPosition;

        notifyMoved(oldPos, this.currentPosition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryToPay(final int amount) {
        ValidationUtils.requireNonNegative(amount, "You cannot pay a negative amount");
        if (this.balance >= amount) {
            this.balance -= amount;
            notifyBalanceChanged(this.balance);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveMoney(final int amount) {
        ValidationUtils.requireNonNegative(amount, "You cannot receive a negative amount");

        this.balance += amount;
        notifyBalanceChanged(this.balance);
    }

    // --- Observer Pattern Methods ---

    /**
     * {@inheritDoc}
     */
    @Override
    public void addObserver(final PlayerObserver observer) {
        this.observers.add(ValidationUtils.requireNonNull(observer, "Observer cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObserver(final PlayerObserver observer) {
        this.observers.remove(ValidationUtils.requireNonNull(observer, "Observer cannot be null"));
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

    // --- Getter and Setter ---

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonProperty
    public int getCurrentPosition() {
        return this.currentPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonProperty
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonProperty
    public int getBalance() {
        return this.balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonProperty
    public TokenType getTokenType() {
        return this.tokenType;
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
    @JsonProperty
    public PlayerState getState() {
        return this.currentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonProperty
    public String getCustomTokenPath() {
        return customTokenPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomTokenPath(final String path) {
        this.customTokenPath = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setState(final PlayerState state) {
        final PlayerState oldState = this.currentState;
        this.currentState = ValidationUtils.requireNonNull(state, "State cannot be null");
        if (!oldState.getClass().equals(state.getClass())) {
            notifyStateChanged(oldState, state);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(final int position) {
        this.currentPosition = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Player{" + this.name + ", " + this.balance + "$, " + this.currentState.getClass().getSimpleName() + "}";
    }
}
