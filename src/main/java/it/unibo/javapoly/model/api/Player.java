package it.unibo.javapoly.model.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.unibo.javapoly.model.impl.PlayerImpl;

/**
 * Represents a player in the Javapoly game.
 * This interface defines the core behaviors and properties of a player,
 * acting as the main entity that interacts with the game board.
 * 
 * <p>
 * A player is characterized by:
 * <ul>
 * <li><strong>Identity:</strong> A unique name and a specific {@link Token}
 * representing them on the board.</li>
 * <li><strong>Financial Status:</strong> A balance of money that changes
 * through transactions (paying rent, receiving salary, etc.).</li>
 * <li><strong>Position:</strong> A current location index on the game
 * board.</li>
 * <li><strong>State:</strong> A {@link PlayerState} object (e.g., Free, Jailed)
 * that dictates behavior during turns.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The player delegates the specific logic of playing a turn to its current
 * state via the State pattern.
 * It also implements the Observer pattern, allowing external components (like
 * the UI or Game Controller) to react to changes in the player's position,
 * balance, or state.
 * </p>
 * 
 * @see PlayerState
 * @see Token
 * @see TokenType
 * @see PlayerObserver
 */
@JsonDeserialize(as = PlayerImpl.class)
public interface Player {

    /**
     * Executes the player's turn by delegating the logic to the current
     * {@link PlayerState}.
     * The specific action taken depends on the player's current state (e.g., moving
     * normally if free, checking release conditions if in jail).
     * 
     * <p>
     * This method ensures the destination is valid before delegating the turn
     * handling to the active state object, to determine if and how the player
     * moves.
     * </p>
     * 
     * @param potentialDestination the potential new position of the player based on
     *                             the dice roll.
     * @param isDouble             indicates if the dice roll was a double.
     * @throws IllegalArgumentException if the potential destination is negative.
     * @see PlayerState
     */
    void playTurn(int potentialDestination, boolean isDouble);

    /**
     * Updates the player's current position on the board.
     * 
     * <p>
     * This method sets the internal position of the player to the specified value
     * and notifies all registered observers about the movement.
     * It ensures that the new position is valid (non-negative).
     * </p>
     * 
     * <p>
     * <strong>Important:</strong> The {@link #move(int)} method should not be
     * called directly from outside the player implementation, as it would break the
     * state management logic.
     * Use {@link #playTurn(int, boolean)} instead to properly execute a player's
     * turn.
     * </p>
     * 
     * <p>
     * <strong>Exception:</strong> This method can be called directly only when a
     * card instructs the player to move without rolling the dice.
     * In such cases, the caller should verify that the player can move by checking
     * their current state using {@link #getState()} before invoking this method.
     * </p>
     * 
     * @param newPosition the new position index to set.
     * @throws IllegalArgumentException if the new position is negative.
     */
    void move(int newPosition);

    /**
     * Attempts to pay a specified amount from the player's balance.
     * 
     * <p>
     * If the player has sufficient funds, the balance is decreased, registered
     * observers are notified about the balance change, and the method
     * returns true.
     * Otherwise, the balance remains unchanged and it returns false.
     * </p>
     *
     * @param amount the amount of money to pay.
     * @return {@code true} if the player has enough balance and the payment was
     *         successful;
     *         {@code false} otherwise.
     * @throws IllegalArgumentException if the amount is negative.
     */
    boolean tryToPay(int amount);

    /**
     * Adds a specified amount of money to the player's balance.
     * 
     * <p>
     * This method increases the player's funds by the given amount and notifies
     * registered observers about the updated balance.
     * </p>
     *
     * @param amount the amount of money to receive.
     * @throws IllegalArgumentException if the amount is negative.
     */
    void receiveMoney(int amount);

    /**
     * Registers an observer to monitor the player's activities and status changes.
     * The observer will be notified when the player moves to a new position,
     * their financial balance changes, or their game state transitions.
     * 
     * @param observer the {@link PlayerObserver} to be added.
     * @throws NullPointerException if the observer is null.
     * @see PlayerObserver
     */
    void addObserver(PlayerObserver observer);

    /**
     * Unregisters an observer, stopping it from receiving notifications about
     * the player's movement, financial balance, or game state changes.
     * 
     * @param observer the {@link PlayerObserver} to be removed.
     * @throws NullPointerException if the observer is null.
     * @see PlayerObserver
     */
    void removeObserver(PlayerObserver observer);

    /**
     * Updates the current state of the player.
     * 
     * <p>
     * This method transitions the player to a new {@link PlayerState}.
     * If the new state is different from the current one (based on class equality),
     * all registered {@link PlayerObserver}s are notified of the state change.
     * </p>
     *
     * @param state the new {@link PlayerState} to set.
     * @throws NullPointerException if the provided state is null.
     * @see PlayerState
     * @see PlayerObserver#onStateChanged(Player, PlayerState, PlayerState)
     */
    void setState(PlayerState state);

    /**
     * Retrieves the current state of the player.
     *
     * @return the current {@link PlayerState} of the player.
     * @see PlayerState
     */
    PlayerState getState();

    /**
     * Retrieves the name of the player.
     *
     * @return the name of the player as a {@link String}.
     */
    String getName();

    /**
     * Retrieves the current financial balance of the player.
     *
     * @return the amount of money the player currently has.
     */
    int getBalance();

    /**
     * Retrieves the token associated with the player.
     *
     * @return the {@link Token} object representing the player on the board.
     * @see Token
     */
    Token getToken();

    /**
     * Retrieves the current position of the player on the game board.
     *
     * @return the index of the cell where the player is currently located.
     */
    int getCurrentPosition();

    /**
     * Set the position of the player.
     * 
     * <p>
     * This method is intended to be used for JSON serialization/deserialization to
     * restore the player's position when loading a saved game from a JSON file.
     * </p>
     *
     * @param position in the board of the player
     */
    void setPosition(int position);

    /**
     * Get the token type chose by the player.
     * 
     * <p>
     * This method is intended to be used for JSON serialization/deserialization to
     * identify the specific token type associated with the player when loading a
     * saved game from a JSON file.
     * </p>
     *
     * @return token type chose by the player
     */
    TokenType getTokenType();

    /**
     * Retrieves the file path associated with a custom token.
     * 
     * <p>
     * This is used for the choice of the custom token, specifically
     * when the player selects {@link TokenType#CUSTOM}. It provides the location of
     * the image file representing the player's custom piece.
     * </p>
     * 
     * @return the file path of the custom token image.
     */
    String getCustomTokenPath();

    /**
     * Sets the file path for the custom token.
     * 
     * <p>
     * This method allows updating the choice of the custom token by
     * specifying the path to the desired image.
     * </p>
     * 
     * @param path the file path of the custom token image.
     */
    void setCustomTokenPath(String path);
}
