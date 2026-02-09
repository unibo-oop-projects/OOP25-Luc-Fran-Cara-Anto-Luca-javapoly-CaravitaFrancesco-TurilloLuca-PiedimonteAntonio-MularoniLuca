package it.unibo.javapoly.controller.api;

import java.io.File;
import java.util.List;

/**
 * Controller interface for handling user interaction from the main menu view.
 */
public interface MenuController {

    /**
     * Initiates a new game session using the provided validated player name.
     *
     * @param names list of player names.
     */
    void playerSetupConfirmed(List<String> names);

    /**
     * Loads a saved game from the specified file path.
     *
     * @param filePath the path to the JSON save file.
     */
    void loadGame(File filePath);

    /**
     * Exits the application.
     */
    void exitGame();

}
