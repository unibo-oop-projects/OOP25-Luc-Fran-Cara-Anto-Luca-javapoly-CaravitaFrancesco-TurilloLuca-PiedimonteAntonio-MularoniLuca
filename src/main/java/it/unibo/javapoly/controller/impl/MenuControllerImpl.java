package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.utils.BoardLoader;
import it.unibo.javapoly.utils.MatchControllerDeserializer;
import it.unibo.javapoly.utils.ValidationUtils;
import it.unibo.javapoly.view.api.MenuView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.logging.Logger;

import it.unibo.javapoly.view.impl.MainViewImpl;
import javafx.application.Platform;
import javafx.stage.Stage;

import static it.unibo.javapoly.view.impl.MenuViewImpl.TITLE;

/**
 * Implementation of {@link MenuController}.
 * Coordinates navigation.
 */
public class MenuControllerImpl implements MenuController {
    public static final String PATH_BOARD_JSON = "/Card/BoardTiles.json";
    private static final String NON_NULL = "Player names list cannot be null";
    private static final String JSON_EXTENSION = ".json";
    private final MenuView menuView;
    private final Logger logger = Logger.getLogger(MenuControllerImpl.class.getName());

    /**
     * Creates a new MenuControllerImpl with the specified view.
     *
     * @param view the main menu view.
     */
    public MenuControllerImpl(final MenuView view) {
        this.menuView = Objects.requireNonNull(view, "View cannot be null");
        this.menuView.setController(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playerSetupConfirmed(final List<String> names, final List<TokenType> tokens, final List<String> customPaths) {
        ValidationUtils.requireNonNull(names, NON_NULL);
        ValidationUtils.requireNonNull(tokens, "Player tokens list cannot be null");
        if (names.size() != tokens.size()) {
            menuView.showError("Name/token count mismatch!");
            return;
        }
        final List<Player> players = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            final Player player = new PlayerImpl(names.get(i), tokens.get(i));
            players.add(player);
        }
        showMainView(players);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadGame(final File saveFile) {
        ValidationUtils.requireNonNull(saveFile, "Save file cannot be null");
        if (!saveFile.exists()) {
            this.menuView.showError("No save file exists or file not found.");
            return;
        }
        if (!saveFile.isFile()) {
            this.menuView.showError("Selected path is not a file.");
            return;
        }
        if (!saveFile.getName().toLowerCase().endsWith(JSON_EXTENSION)) {
            this.menuView.showError("Selected file is not a valid save (.json) file.");
            return;
        }
        try {
            final MatchControllerImpl matchController = MatchControllerDeserializer.deserialize(saveFile);
            final MainViewImpl mainView = matchController.getMainViewImpl();
            final Stage stage = this.menuView.getStage();
            stage.getScene().setRoot(mainView.getRoot());
            stage.setTitle(TITLE);
            matchController.startGame();
        } catch (final IOException e) {
            logger.fine("Error loading board from saved file: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitGame() {
        Platform.exit();
    }

    /**
     * Initializes and display the main game view with the given players.
     *
     * @param players the list of initialized players.
     */
    private void showMainView(final List<Player> players) {
        ValidationUtils.requireNonNull(players, NON_NULL);
        try {
            final InputStream is = MatchControllerImpl.class.getResourceAsStream(PATH_BOARD_JSON);
            final BoardImpl board = BoardLoader.loadBoardFromJson(is);
            final Map<String, Property> properties = new HashMap<>();
            for (int i = 0; i < board.size(); i++) {
                if (board.getTileAt(i) instanceof PropertyTile propertyTile) {
                    properties.put(propertyTile.getProperty().getId(), propertyTile.getProperty());
                }
            }
            final MatchController matchController = new MatchControllerImpl(players, board, properties);
            final MainViewImpl mainView = matchController.getMainViewImpl();
            final Stage stage = this.menuView.getStage();
            stage.getScene().setRoot(mainView.getRoot());
            stage.setTitle(TITLE);
            matchController.startGame();
        } catch (final IOException e) {
            logger.fine("Error loading board from file: " + e.getMessage());
        }
    }
}
