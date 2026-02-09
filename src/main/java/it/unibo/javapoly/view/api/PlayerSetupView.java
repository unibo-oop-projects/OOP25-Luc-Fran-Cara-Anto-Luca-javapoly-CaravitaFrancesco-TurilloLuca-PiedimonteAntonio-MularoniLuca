package it.unibo.javapoly.view.api;

import it.unibo.javapoly.controller.api.MenuController;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * View interface for configuring player information before starting a new game.
 */
public interface PlayerSetupView {

    /**
     * Sets the controller to handle user actions.
     *
     * @param controller the player setup controller
     */
    void setController(MenuController controller);

    /**
     * Pop-up tha display an error message to the user.
     *
     * @param message the error message to display.
     */
    void showError(String message);

    /**
     * Returns the root layout container used to display this view in a scene.
     *
     * @return the root {@link BorderPane} containing all UI components.
     */
    BorderPane getRoot();

    /**
     * Sets the primary stage.
     *
     * @param stage the primary application stage.
     */
    void setStage(Stage stage);
}
