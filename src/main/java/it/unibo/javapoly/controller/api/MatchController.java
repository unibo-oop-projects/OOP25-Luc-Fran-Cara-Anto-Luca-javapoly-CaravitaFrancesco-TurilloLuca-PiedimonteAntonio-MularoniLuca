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
     * Handles the end of the current player's turn.
     */
    void handleEndTurn();

    /** @return the game board. */
    Board getBoard();

    /** @return true if the current player can roll the dice. */
    boolean canCurrentPlayerRoll();

    /** @return the main view/GUI. */
    MainView getMainView();

    /** @return list of all players. */
    List<Player> getPlayers();

    /** Allows the current player to pay to exit jail. */
    void payToExitJail();

    /** @return the index of the current player. */
    int getCurrentPlayerIndex();

    /** @return the number of consecutive doubles rolled. */
    int getConsecutiveDoubles();

    /** Sets the current player index. */
    void setCurrentPlayerIndex(int indx);

    /** Sets the consecutive doubles count. */
    void setConsecutiveDoubles(int d);

    /** Sets whether the current player has rolled. */
    void setHasRolled(boolean b);

    /** Restores the jail turn counter from saved data. */
    void restoreJailTurnCounter(final Map<String, Integer> map, final List<Player> players);

    /** @return the economy controller. */
    EconomyController getEconomyController();

    /** @return the property controller. */
    PropertyController getPropertyController();

    /** Acquista la proprietà sulla casella attuale. */
    void buyCurrentProperty();

    /** Costruisce una casa sulla proprietà specificata. */
    void buildHouseOnProperty(Property property);

    /** Finalizes the liquidation process for a player. */
    void finalizeLiquidation(Player p);
}
