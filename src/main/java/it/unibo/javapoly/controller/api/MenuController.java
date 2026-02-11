package it.unibo.javapoly.controller.api;

import java.io.File;
import java.util.List;

import it.unibo.javapoly.model.api.TokenType;

/**
 * Controller interface for handling user interaction from the main menu view.
 */
public interface MenuController {

    /**
     * Initiates a new game session using the provided validated player name, token
     * type lists and custom token paths.
     * The method is called after the user confirms their player setup in the menu,
     * and it receives the necessary information to create player instances and
     * start the game.
     *
     * @param names       list of player names.
     * @param tokens      list of player token types.
     * @param customPaths list of custom token image paths.
     */
    void playerSetupConfirmed(List<String> names, List<TokenType> tokens, List<String> customPaths);

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
