package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.utils.BoardLoader;
import it.unibo.javapoly.utils.ValidationUtils;
import it.unibo.javapoly.view.api.MenuView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import it.unibo.javapoly.view.impl.MainView;
import javafx.application.Platform;
import javafx.stage.Stage;

import static it.unibo.javapoly.view.impl.MenuViewImpl.TITLE;

/**
 * Implementation of {@link MenuController}.
 * Coordinates navigation.
 */
public class MenuControllerImpl implements MenuController {
    public static final String PATH_BOARD_JSON = "src/main/resources/Card/BoardTiles.json";
    private static final String NON_NULL = "Player names list cannot be null";
    private static final String JSON_EXTENSION = ".json";
    private final MenuView menuView;

    /**
     * Creates a new MenuControllerImpl with the specified view.
     *
     * @param view the main menu view.
     */
    public MenuControllerImpl(final MenuView view) {
        this.menuView = Objects.requireNonNull(view);
        this.menuView.setController(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playerSetupConfirmed(final List<String> names, final List<TokenType> tokens) {
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
            final MatchController matchController = MatchSnapshotter.loadMatch(saveFile);
            final MainView mainView = matchController.getMainView();
            final Stage stage = this.menuView.getStage();
            stage.getScene().setRoot(mainView.getRoot());
            stage.setTitle(TITLE);
            matchController.startGame();
        } catch (final IOException e) {
            System.err.println("Error loading board from saved file: " + e.getMessage());
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
            final BoardImpl board = BoardLoader.loadBoardFromJson(PATH_BOARD_JSON);
            final Map<String, Property> properties = BoardLoader.loadPropertiesFromJson(PATH_BOARD_JSON);
            final MatchController matchController = new MatchControllerImpl(players, board, properties);
            final MainView mainView = matchController.getMainView();
            final Stage stage = this.menuView.getStage();
            stage.getScene().setRoot(mainView.getRoot());
            stage.setTitle(TITLE);
            matchController.startGame();
        } catch (final IOException e) {
            System.err.println("Error loading board from file: " + e.getMessage());
        }
    }
}
