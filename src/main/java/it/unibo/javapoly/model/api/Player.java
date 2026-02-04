package it.unibo.javapoly.model.api;

/**
 * Represents a player in the Javapoly game.
 * This interface defines the core behaviors and properties of a player,
 * including their identity, financial status, position on the board, and state
 * management.
 * Players can move around the board, manage their money, and interact with the
 * game through their turns.
 * 
 */
public interface Player {

    /**
     * Retrieves the name of the player.
     *
     * @return the name of the player as a String.
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
     */
    Token getToken();

    /**
     * Retrieves the current position of the player on the game board.
     *
     * @return the index of the cell where the player is currently located.
     */
    int getCurrentPosition();

    /**
     * Manages the turn logic based on the player's current state:
     * in jail, free, or bankrupt.
     * The method handle the player's actions, including moving the player and
     * updating their state.
     * The logic for the turn is delegated to the current {@link PlayerState} of the
     * player, ensuring that the correct behavior is executed based on the player's
     * situation.
     * 
     * @param diceResult the total value obtained from rolling the dice.
     * @param isDouble   indicates if the dice roll was a double.
     * @see PlayerState
     */
    void playTurn(int diceResult, boolean isDouble);

    /**
     * Moves the player a specified number of steps on the game board.
     * 
     * <strong>Important:</strong> The {@link #move(int)} method should not be
     * called directly from outside the player implementation, as it would break the
     * state management logic.
     * Use {@link #playTurn(int, boolean)} instead to properly execute a player's
     * turn.
     * 
     * <strong>Exception:</strong> This method can be called directly only when a
     * card instructs the player to move without
     * rolling the dice.
     * 
     * @param steps the number of spaces to move forward.
     * @see PlayerState
     */
    void move(int steps);

    /**
     * Attempts to pay a specified amount from the player's balance.
     * If the player has sufficient funds, the balance is decreased and the method
     * returns true.
     * Otherwise, the balance remains unchanged (or handling logic is applied) and
     * it returns false.
     *
     * @param amount the amount of money to pay.
     * @return true if the payment was successful, false otherwise.
     */
    boolean tryToPay(int amount);

    /**
     * Adds a specified amount of money to the player's balance.
     *
     * @param amount the amount of money to receive.
     */
    void receiveMoney(int amount);

    /**
     * Updates the current state of the player.
     *
     * @param state the new {@link PlayerState} to set.
     */
    void setState(PlayerState state);

    /**
     * Retrieves the current state of the player.
     *
     * @return the current {@link PlayerState} of the player.
     */
    PlayerState getState();
}
