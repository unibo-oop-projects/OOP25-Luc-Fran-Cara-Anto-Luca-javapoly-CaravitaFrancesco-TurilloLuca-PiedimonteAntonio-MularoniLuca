package it.unibo.javapoly.view.api;

import javafx.scene.layout.Pane;

/**
 * Interface representing the visual board of the game.
 * It handles the rendering of tiles, properties, and player tokens.
 */
public interface BoardPanel {

    /**
     * Returns the visual root node of the board.
     *
     * @return the Pane containing the board grid.
     */
    Pane getRoot();

    /**
     * Re-renders the board and updates the position of all tokens 
     * and property states based on the current model state.
     */
    void update();
}
