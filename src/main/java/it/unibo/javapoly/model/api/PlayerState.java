package it.unibo.javapoly.model.api;

/**
 * Represents the state of a player in the game.
 * This interface defines the behavior of a player based on their current status
 * It follows the State design pattern.
 * The player's state can be one of the following:
 * <ul>
 * <li><strong>In Jail:</strong> The player is currently in jail and has
 * specific rules governing their actions, such as attempting to roll doubles to
 * get out or reaching the maximum number of turns in jail.</li>
 * <li><strong>Free:</strong> The player is not in jail and can move freely
 * around the board, manage their properties, and interact with other
 * players.</li>
 * <li><strong>Bankrupt:</strong> The player has lost all their money and is out
 * of the game. They cannot take any actions and are effectively removed from
 * play.</li>
 * </ul>
 * The {@link PlayerState} interface defines the methods that govern the
 * player's actions during their turn, ensuring that the correct behavior is
 * executed based on their current state.
 */
public interface PlayerState {

    /**
     * Executes the turn logic specific to the player's current state.
     * This method determines if and how the player interacts with the game board
     * during their turn.
     * 
     * <p>
     * Implementation behavior:
     * <ul>
     * <li>{@link it.unibo.javapoly.model.impl.FreeState}: The player moves to the
     * specified {@code potentialDestination}.</li>
     * <li>{@link it.unibo.javapoly.model.impl.JailedState}: The method handles
     * detention turns, checks for release conditions
     * (such as rolling doubles or reaching the maximum number of turns in jail),
     * and transitions the player to a free state if applicable.</li>
     * <li>{@link it.unibo.javapoly.model.impl.BankruptState}: The player is out of
     * the game and performs no actions.</li>
     * </ul>
     * </p>
     *
     * @param player               the player performing the turn.
     * @param potentialDestination the potential new position of the player based on
     *                             the dice roll.
     * @param isDouble             indicates if the dice roll was a double, which
     *                             can influence state transitions (e.g., leaving
     *                             jail).
     */
    void playTurn(Player player, int potentialDestination, boolean isDouble);

    /**
     * Checks if the player is allowed to move from their current position.
     *
     * @return {@code true} if the player can move, {@code false} otherwise.
     */
    boolean canMove();

}
