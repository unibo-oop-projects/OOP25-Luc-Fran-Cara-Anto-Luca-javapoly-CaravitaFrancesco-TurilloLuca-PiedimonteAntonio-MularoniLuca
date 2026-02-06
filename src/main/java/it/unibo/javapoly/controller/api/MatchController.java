package it.unibo.javapoly.controller.api;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.impl.PlayerImpl;

/**
 * Interface representing the main controller for a Monopoly match.
 * It manages the game loop, player actions, and turn logic.
 */
public interface MatchController {
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
     * * @return the current {@link Player}
     */
    PlayerImpl getCurrentPlayer();

    /**
     * Handles the dice throwing logic, including doubles and consecutive doubles rules.
     */
    void handleDiceThrow();

    /**
     * Moves the current player on the board.
     * * @param steps the number of spaces the player should move
     */
    void handleMove(int steps);

    /**
     * Manages the logic for when a player is sent to or is currently in prison.
     */
    void handlePrison();

    /**
     * Handles the events triggered when a player lands on a property space.
     */
    void handlePropertyLanding();

    /** @return the game board. */
    Board getBoard();

    //descrizione da inserire
    boolean canCurrentPlayerRoll();
}
