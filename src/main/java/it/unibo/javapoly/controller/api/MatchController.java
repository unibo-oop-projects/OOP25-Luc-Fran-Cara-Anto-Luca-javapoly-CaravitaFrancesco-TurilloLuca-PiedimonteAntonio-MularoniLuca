package it.unibo.javapoly.controller.api;

import java.util.List;
import java.util.Map;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerObserver;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.view.impl.MainView;

/**
 * Interface representing the main controller for a Monopoly match.
 * It manages the game loop, player actions, and turn logic.
 */
public interface MatchController extends PlayerObserver {
    /**
     * Starts the game, initializing the board and the first player's turn.
     */
    void startGame();

    /**
     * Switches the turn to the next player in the rotation.
     */
    void nextTurn();

    /**
     * Returns the player who is currently taking their turn.
     * @return the current {@link Player}
     */
    Player getCurrentPlayer();

    /**
     * Handles the dice throwing logic, including doubles and consecutive doubles rules.
     */
    void handleDiceThrow();

    /**
     * Returns the game board.
     * * @return the game board.
     */
    Board getBoard();

    /**
     * Checks if the current player can roll the dice.
     * * @return true if the current player can roll the dice.
     */
    boolean canCurrentPlayerRoll();

    /**
     * Returns the main view.
     * * @return the main view/GUI.
     */
    MainView getMainView();

    /**
     * This method update the list of player in bankruptState.
     */
    void updatePlayerBankrupt();

    /**
     * Returns the list of players.
     * * @return list of all players.
     */
    List<Player> getPlayers();

    /** Allows the current player to pay to exit jail. */
    void payToExitJail();

    /**
     * Returns the current player index.
     * * @return the index of the current player.
     */
    int getCurrentPlayerIndex();

    /**
     * Returns the number of doubles.
     * * @return the number of consecutive doubles rolled.
     */
    int getConsecutiveDoubles();

    /**
     * Sets the current player index.
     * * @param indx the player index.
     */
    void setCurrentPlayerIndex(int indx);

    /**
     * Sets the consecutive doubles count.
     * * @param d the number of doubles.
     */
    void setConsecutiveDoubles(int d);

    /**
     * Sets whether the current player has rolled.
     * * @param b true if rolled.
     */
    void setHasRolled(boolean b);

    /**
     * Restores the jail turn counter from saved data.
     * * @param map the jail data map.
     * @param players the list of players.
     */
    void restoreJailTurnCounter(final Map<String, Integer> map, final List<Player> players);

    /**
     * Returns the economy controller.
     * * @return the economy controller.
     */
    EconomyController getEconomyController();

    /**
     * Returns the property controller.
     * * @return the property controller.
     */
    PropertyController getPropertyController();

    /** Acquista la proprietà sulla casella attuale. */
    void buyCurrentProperty();

    /**
     * Costruisce una casa sulla proprietà specificata.
     * * @param property the property to build on.
     */
    void buildHouseOnProperty(Property property);

    /**
     * Finalizes the liquidation process for a player.
     * * @param p the player being liquidated.
     */
    void finalizeLiquidation(Player p);
}
