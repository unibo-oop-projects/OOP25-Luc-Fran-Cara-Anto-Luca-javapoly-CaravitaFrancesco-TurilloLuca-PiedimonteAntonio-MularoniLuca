package it.unibo.javapoly.view.api;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Interface representing the main view of the game.
 * It coordinates the different UI panels and displays game messages.
 */
public interface MainView {

    /**
     * Displays the main window.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    void start(Stage stage);

    /**
     * Refreshes all UI components to match the current model state.
     */
    void refreshAll();

    /**
     * Adds a new message to the log area.
     *
     * @param msg the message to append.
     */
    void addLog(String msg);

    /**
     * Shows the liquidation view and disables game controls.
     */
    void showLiquidation();

    /**
     * Hides the liquidation view and re-enables game controls.
     */
    void hideLiquidation();

    /**
     * Shows a card alert with the given title and description.
     *
     * @param title       card title.
     * @param description card description.
     */
    void showCard(String title, String description);

    /**
     * Shows an alert informing that a player has gone bankrupt.
     *
     * @param playerName name of the bankrupt player.
     */
    void showBankruptAlert(String playerName);

    /**
     * Shows the winner of the game and disables all controls.
     *
     * @param winnerName name of the winner.
     */
    void showWinner(String winnerName);

    /**
     * Clears all messages from the log area.
     */
    void clearLog();

    /**
     * Returns the root node of the view.
     *
     * @return the root Pane.
     */
    Pane getRoot();
}
