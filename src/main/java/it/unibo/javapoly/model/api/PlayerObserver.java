package it.unibo.javapoly.model.api;

/**
 * Observer interface for monitoring changes in a player's state during the
 * game.
 * 
 * <p>
 * This interface implements the Observer pattern to decouple the player model
 * from the components that need to react to player state changes. By using this
 * observer pattern, the game maintains a clean separation of concerns where the
 * Player class doesn't need to know about the specific implementations that
 * respond to its changes.
 * </p>
 * 
 * <p>
 * The primary purposes of this interface are:
 * <ul>
 * <li>To enable reactive updates in the UI or other game components when player
 * properties change, without creating tight coupling between the model and
 * view</li>
 * <li>To facilitate the tracking of player movements, financial transactions,
 * and state transitions throughout the game</li>
 * <li>To allow multiple observers to monitor the same player simultaneously,
 * enabling different parts of the application to respond independently to
 * player changes</li>
 * <li>To maintain consistency across the game by ensuring all interested
 * components are notified when critical player events occur</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This design allows for flexible extension of game functionality, as new
 * observers can be added without modifying the existing Player class. It also
 * supports features like logging, game statistics tracking, UI updates, and
 * game rule enforcement in a modular and maintainable way.
 * </p>
 * 
 * @see Player
 * @see PlayerState
 */
public interface PlayerObserver {
    /**
     * Called when a player moves to a new position on the game board.
     * 
     * @param player      the player who moved
     * @param oldPosition the player's previous position
     * @param newPosition the player's new position
     * @see Player
     */
    void onPlayerMoved(Player player, int oldPosition, int newPosition);

    /**
     * Called when a player's balance changes.
     * 
     * @param player     the player whose balance changed
     * @param newBalance the player's new balance
     * @see Player
     */
    void onBalanceChanged(Player player, int newBalance);

    /**
     * Called when a player's state changes.
     * 
     * @param player   the player whose state changed
     * @param oldState the player's previous state
     * @param newState the player's new state
     * @see Player
     * @see PlayerState
     */
    void onStateChanged(Player player, PlayerState oldState, PlayerState newState);

}
