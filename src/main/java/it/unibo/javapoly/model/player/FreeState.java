package it.unibo.javapoly.model.player;

/**
 * Represents the state of a player when they are free to move.
 * This class implements the {@link PlayerState} interface and follows the
 * Singleton pattern,
 * ensuring that only one instance of the free state exists.
 * In this state, the player is allowed to move normally based on the dice roll
 * result.
 */
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
     * The player moves by the number of steps indicated by the dice result.
     * A message is logged to the standard output indicating the movement.
     *
     * @param player     the player performing the turn.
     * @param diceResult the total result of the dice roll.
     */
    @Override
    public void playTurn(final Player player, final int diceResult) {
        System.out.println("[Stato Libero] Il giocatore si muove di " + diceResult); // NOPMD
        player.move(diceResult);
    }

    /**
     * Checks if the player is allowed to move.
     *
     * @return {@code true} as the player is in a free state and can always move.
     */
    @Override
    public boolean canMove() {
        return true;
    }
}
