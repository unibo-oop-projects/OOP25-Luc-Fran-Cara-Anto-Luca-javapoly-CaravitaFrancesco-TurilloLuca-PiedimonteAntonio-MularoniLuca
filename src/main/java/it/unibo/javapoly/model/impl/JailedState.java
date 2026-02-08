package it.unibo.javapoly.model.impl;

import java.util.Objects;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;

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
        Objects.requireNonNull(player, "The player cannot be null");
        if (potentialDestination < 0) {
            throw new IllegalArgumentException("Potential destination cannot be negative: " + potentialDestination);
        }
        player.move(potentialDestination);

        turnsInJail++;
        System.out.println("[Prigione] Turno " + turnsInJail + " di detenzione."); // NOPMD

        if (isDouble || turnsInJail >= MAX_TURNS) {
            System.out.println(player.getName() + " esce di prigione!"); // NOPMD

            player.setState(FreeState.getInstance());
            player.move(potentialDestination);
        } else {
            System.out.println("Resta in prigione."); // NOPMD
        }

        // TODO understand if there are other release conditions, such as paying a
        // fine or using a "Get Out of Jail Free" card.
        // Understand if I need to handle this, especially for the "Get Out of Jail
        // Free" card, or if it is the player who decides when to use it, and in that
        // case how do I communicate it to them, since they do not manage their state,
        // but the state manages them. Or if it should be handled by who manages all
        // the cards, that is Francesco.
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
}
