package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;

/**
 * Represents the state of a player who has gone bankrupt.
 * This class implements the {@link PlayerState} interface using the Singleton
 * pattern, as the behavior of a bankrupt player is stateless and identical for
 * all instances.
 * A player in this state cannot move and performs no actions during their turn.
 * 
 * @see PlayerState
 */
public final class BankruptState implements PlayerState {

    /**
     * The single instance of the BankruptState.
     */
    private static final BankruptState INSTANCE = new BankruptState();

    /**
     * Private constructor to prevent external instantiation.
     */
    private BankruptState() {
    }

    /**
     * Returns the singleton instance of the BankruptState.
     *
     * @return the single instance of {@link BankruptState}.
     */
    public static BankruptState getInstance() {
        return INSTANCE;
    }

    /**
     * Handles the logic for the player's turn when they are bankrupt.
     * In this state, the method performs no actions.
     *
     * @param player               the player currently in this state.
     * @param potentialDestination the potential new position of the player based on
     *                             the dice roll.
     * @param isDouble             indicates if the dice roll was a double.
     */
    @Override
    public void playTurn(final Player player, final int potentialDestination, final boolean isDouble) {
        // no actions are performed for a bankrupt player during their turn.
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false} as a bankrupt player cannot move.
     */
    @Override
    public boolean canMove() {
        return false;
    }
}
