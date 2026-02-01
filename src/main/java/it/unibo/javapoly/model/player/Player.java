package it.unibo.javapoly.model.player;

/**
 * Represents a player in the JavaPoly game.
 * This interface defines the core behaviors and properties that a player entity
 * must support,
 * including managing finances, movement, and game state.
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
     * Executes the logic for a player's turn based on a dice roll.
     *
     * @param diceResult the total value obtained from rolling the dice.
     */
    void playTurn(int diceResult);

    /**
     * Moves the player a specified number of steps on the game board.
     *
     * @param steps the number of spaces to move forward.
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
