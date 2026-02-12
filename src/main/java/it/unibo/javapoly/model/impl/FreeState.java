package it.unibo.javapoly.model.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.utils.ValidationUtils;

/**
 * Represents the state of a player when they are free to move.
 * This class implements the {@link PlayerState} interface and follows the
 * Singleton pattern, ensuring that only one instance of the free state exists.
 * In this state, the player is allowed to move normally based on the dice roll
 * result.
 * 
 * @see PlayerState
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("FreeState")
public final class FreeState implements PlayerState {

    /**
     * The single instance of the FreeState class.
     */
    private static final FreeState INSTANCE = new FreeState();

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private FreeState() {
    }

    /**
     * Returns the singleton instance of the FreeState.
     *
     * @return the single instance of {@link FreeState}.
     */
    public static FreeState getInstance() {
        return INSTANCE;
    }

    /**
     * Executes the standard turn logic for a free player.
     * The player moves to the new position indicated by the potential destination.
     *
     * @param player               the player currently in this state.
     * @param potentialDestination the potential new position of the player based on
     *                             the dice roll.
     * @param isDouble             indicates if the dice roll was a double.
     * @throws NullPointerException     if the player is null.
     * @throws IllegalArgumentException if the potential destination is negative.
     */
    @Override
    public void playTurn(final Player player, final int potentialDestination, final boolean isDouble) {
        ValidationUtils.requireNonNull(player, "The player cannot be null");
        ValidationUtils.requireNonNegative(potentialDestination, "Potential destination cannot be negative");
        player.move(potentialDestination);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} as the player is in a free state and can always move.
     */
    @Override
    public boolean canMove() {
        return true;
    }
}
