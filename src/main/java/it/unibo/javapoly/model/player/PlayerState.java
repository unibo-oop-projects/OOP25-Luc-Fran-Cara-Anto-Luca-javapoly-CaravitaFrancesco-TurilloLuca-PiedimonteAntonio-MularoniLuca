package it.unibo.javapoly.model.player;

/**
 * Represents the state of a player in the game.
 * This interface defines the behavior of a player based on their current status
 * It follows the State design pattern.
 */
public interface PlayerState {

    /**
     * Executes the logic for the player's turn based on the dice roll.
     *
     * @param player     the player performing the turn.
     * @param diceResult the total value obtained from rolling the dice.
     */
    void playTurn(Player player, int diceResult);

    /**
     * checks if the player is allowed to move from their current position.
     *
     * @return true if the player can move, false otherwise.
     */
    boolean canMove();

}
