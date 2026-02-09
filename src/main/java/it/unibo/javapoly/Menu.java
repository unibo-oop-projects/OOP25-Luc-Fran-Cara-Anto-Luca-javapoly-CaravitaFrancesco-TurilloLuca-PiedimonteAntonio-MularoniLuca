package it.unibo.javapoly;

import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.controller.impl.MenuControllerImpl;
import it.unibo.javapoly.view.api.MenuView;
import it.unibo.javapoly.view.impl.MenuViewImpl;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFx application menu for the "javapoly" game.
 */
public class Menu extends Application {

    @Override
    public void init() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final Stage primaryStage) {
        final MenuView menuView = new MenuViewImpl(primaryStage);
        final MenuController menuController = new MenuControllerImpl(menuView);
        menuView.setController(menuController);
        primaryStage.show();
    }

}
