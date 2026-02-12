package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.utils.ValidationUtils;

/**
 * Represents the state of a {@link Player} when they are in jail.
 * In this state, a player cannot move freely. They must wait for a specific
 * condition to be met (rolling a double or waiting for a maximum number of
 * turns) to be released and transition back to the {@link FreeState}.
 * 
 * @see FreeState
 * @see PlayerState
 */
public final class JailedState implements PlayerState {

    /**
     * The maximum number of turns a player must stay in jail before being
     * automatically released.
     */
    private static final int MAX_TURNS = 3;

    /**
     * The number of turns the player has currently spent in jail.
     */
    private int turnsInJail;

    /**
     * Constructs a new {@link JailedState} with the turn counter reset to zero.
     */
    public JailedState() {
        this.turnsInJail = 0;
    }

    /**
     * Constructs a new {@link JailedState} with the turn counter equal turnsInJail.
     * 
     * <p>
     * This method is intended to be used for JSON serialization/deserialization to
     * restore the number of turns a player has already spent in jail when loading a
     * saved game from a JSON file.
     * </p>
     * 
     * @param turnsInJail number of turns in jail.
     */
    public JailedState(final Integer turnsInJail) {
        this.turnsInJail = turnsInJail;
    }

    /**
     * Handles the logic for a player's turn while they are in jail.
     * Increments the turn counter. Checks if the player meets the conditions to be
     * released:
     * <ul>
     * <li>Rolling a double.</li>
     * <li>Reaching the maximum number of turns in jail ({@value #MAX_TURNS}).</li>
     * </ul>
     * If released, the player's state transitions to {@link FreeState}, and they
     * immediately move.
     * Otherwise, they remain in jail.
     *
     * @param player               the player currently in this state.
     * @param potentialDestination the potential new position of the player based on
     *                             the dice roll.
     * @param isDouble             indicates if the dice roll was a double.
     * @throws NullPointerException     if the player is null.
     * @throws IllegalArgumentException if the potential destination is negative.
     * @see FreeState
     */
    @Override
    public void playTurn(final Player player, final int potentialDestination, final boolean isDouble) {
        ValidationUtils.requireNonNull(player, "The player cannot be null");
        ValidationUtils.requireNonNegative(potentialDestination, "Potential destination cannot be negative");

        turnsInJail++;
        System.out.println("[Prigione] Turno " + turnsInJail + " di detenzione."); // NOPMD

        if (isDouble || turnsInJail >= MAX_TURNS) {
            System.out.println(player.getName() + " esce di prigione!"); // NOPMD

            player.setState(FreeState.getInstance());
            player.move(potentialDestination);
        } else {
            System.out.println("Resta in prigione."); // NOPMD
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false}, as jailed players cannot move freely.
     */
    @Override
    public boolean canMove() {
        return false;
    }

    /**
     * Gets the number of turns spent in jail.
     * 
     * <p>
     * This getter is intended to be used for JSON serialization/deserialization to
     * save and restore the number of turns a player has already spent in jail when
     * loading a saved game from a JSON file.
     * </p>
     *
     * @return the number of turns spent in jail.
     */
    public int getTurnsInJail() {
        return this.turnsInJail;
    }
}
