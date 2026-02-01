package it.unibo.javapoly.model.player;

/**
 * Represents the state of a {@link Player} when they are in jail.
 * In this state, a player cannot move freely. They must wait for a specific
 * condition to be met
 * (rolling a double or waiting for a maximum number of turns) to be released
 * and transition
 * back to the {@link FreeState}.
 */
public final class JailedState implements PlayerState {

    /**
     * The number of turns the player has currently spent in jail.
     */
    private int turnsInJail;

    /**
     * The maximum number of turns a player must stay in jail before being
     * automatically released.
     */
    private static final int MAX_TURNS = 3;

    /**
     * Constructs a new {@code JailedState} with the turn counter reset to zero.
     */
    public JailedState() {
        this.turnsInJail = 0;
    }

    /**
     * Handles the logic for a player's turn while they are in jail.
     * Increments the turn counter. Checks if the player meets the conditions to be
     * released:
     * <ul>
     * <li>Rolling a "double" (simulated here by a dice result &gt; 10).</li>
     * <li>Reaching the maximum number of turns in jail ({@value #MAX_TURNS}).</li>
     * </ul>
     * If released, the player's state transitions to {@link FreeState}, and they
     * immediately move.
     * Otherwise, they remain in jail.
     *
     * @param player     the player taking the turn.
     * @param diceResult the result of the dice roll for this turn.
     */
    @Override
    public void playTurn(final Player player, final int diceResult) {
        turnsInJail++;
        System.out.println("[Prigione] Turno " + turnsInJail + " di detenzione."); // NOPMD

        final boolean rolledDouble = diceResult > 10; // placeholder per "doppio"

        if (rolledDouble || turnsInJail >= MAX_TURNS) {
            System.out.println(player.getName() + " esce di prigione!"); // NOPMD

            player.setState(FreeState.getInstance());
            player.move(diceResult);
        } else {
            System.out.println("Resta in prigione."); // NOPMD
        }
    }

    /**
     * Indicates whether the player can move freely in this state.
     *
     * @return {@code false}, as jailed players cannot move freely.
     */
    @Override
    public boolean canMove() {
        return false;
    }
}
