package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;

/**
 * Represents the state of a player who has gone bankrupt.
 * This class implements the {@link PlayerState} interface using the Singleton
 * pattern,
 * as the behavior of a bankrupt player is stateless and identical for all
 * instances.
 * A player in this state cannot move and performs no actions during their turn.
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
     * @return the single instance of {@code BankruptState}.
     */
    public static BankruptState getInstance() {
        return INSTANCE;
    }

    /**
     * Handles the logic for the player's turn when they are bankrupt.
     * In this state, the method simply logs that the player is out of the game
     * and performs no other actions.
     *
     * @param player     the player currently in this state.
     * @param diceResult the result of the dice roll (ignored in this state).
     * @param isDouble   indicates if the dice roll was a double.
     */
    @Override
    public void playTurn(final Player player, final int diceResult, final boolean isDouble) {
        System.out.println("[Bancarotta] Il giocatore " + player.getName() + " è fuori dal gioco."); // NOPMD
    }

    /**
     * Determines if the player is allowed to move in this state.
     *
     * @return {@code false} as a bankrupt player cannot move.
     */
    @Override
    public boolean canMove() {
        return false;
    }
}
