package it.unibo.javapoly.view.api;

import it.unibo.javapoly.controller.api.MenuController;
import javafx.stage.Stage;

/**
 * Interface for the main menu view of the JavaPoly game.
 */
public interface MenuView {

    /**
     * Sets the controller that handles menu actions.
     *
     * @param controller the menu controller.
     */
    void setController(MenuController controller);

    /**
     * Pop-up tha display an error message to the user.
     *
     * @param message the error message to display.
     */
    void showError(String message);

    /**
     * Transitions the application to the Player Setup view.
     */
    void showPlayerSetupView();

    /**
     * Create file picker dialog.
     */
    void showLoadGameView();

    /**
     * Get stage.
     *
     * @return stage.
     */
    Stage getStage();
}
