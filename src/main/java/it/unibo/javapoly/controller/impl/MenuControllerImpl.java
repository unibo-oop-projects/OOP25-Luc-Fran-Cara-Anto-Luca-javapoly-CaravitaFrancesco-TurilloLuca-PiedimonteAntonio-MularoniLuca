package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.view.api.MenuView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
        if (saveFile == null || !saveFile.exists()) {
            this.menuView.showError("No save file exists or selected");
            return;
        }
        if (!saveFile.getName().toLowerCase().endsWith(JSON_EXTENSION)) {
            this.menuView.showError("No save file exists or selected");
            return;
        }

        /*
        try {
            final Board board = createDefaultBoard();
            final MatchControllerImp controller = MatchSerializationUtils.loadMatch(saveFile,board);
            this.menuView.getStage().getScene().setRoot(controller.getMainView().getRoot());
            controller.startGame();
        } catch (final IOException e) {
            this.menuView.showError("Failed to load game:" + e.getMessage());
        }*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitGame() {
        Platform.exit();
    }

    private void showMainView(final List<Player> players) {
        final MatchController matchController = new MatchControllerImpl(players);
        final MainView mainView = matchController.getMainView();
        final Stage stage = this.menuView.getStage();
        stage.getScene().setRoot(mainView.getRoot());
        stage.setTitle(TITLE);
        matchController.startGame();
    }
}
