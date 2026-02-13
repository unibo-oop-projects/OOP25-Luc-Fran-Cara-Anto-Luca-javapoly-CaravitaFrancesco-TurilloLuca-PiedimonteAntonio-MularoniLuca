package it.unibo.javapoly.view.api;

import javafx.scene.layout.HBox;

/**
 * Interface representing the panel containing game command buttons.
 * It manages the availability and visibility of player actions.
 */
public interface CommandPanel {

    /**
     * Updates the state of the buttons based on the current game context.
     * Disables or enables actions like throwing dice, buying properties, 
     * or ending the turn.
     */
    void updateState();

    /**
     * Returns the root node of this command panel.
     *
     * @return the HBox containing the action buttons.
     */
    HBox getRoot();

    /**
     * Saves the current game state to a persistent file.
     */
    void saveStateGame();
}
