package it.unibo.javapoly;

import javafx.application.Application;

/**
 * JavaFx application for the "javapoly" game.
 */
public final class JavaPolyApp {

    /**
     * Private constructor.
     */
    private JavaPolyApp() {

    }

    /**
     * Main method to lunch the JavaFx application.
     *
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        Application.launch(Menu.class);
    }
}
