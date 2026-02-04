package it.unibo.javapoly.view.impl;

import java.util.Objects;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import it.unibo.javapoly.controller.api.MatchController;

/**
 * MainFrame is the main window of the JavaPoly game.
 * It contains the game board, player info, action buttons, and a log area.
 */
public class MainView {

    private final BorderPane root;
    private final BoardPanel boardPanel; // panel displaying the game board
    private final CommandPanel commandPanel; // panel with action buttons (throw dice, end turn)
    private final InfoPanel infoPanel; // panel showing player information
    private final TextArea logArea; // text area for game messages/log
    private final MatchController matchController;

    /**
     * Constructor: initializes the layout and components.
     *
     * @param matchController the controller that manages the game logic.
     */
    public MainView(final MatchController matchController) {
        this.matchController = Objects.requireNonNull(matchController);
        this.root = new BorderPane();

        this.boardPanel = new BoardPanel(this.matchController.getBoard()); // shows the game board and player tokens
        this.commandPanel = new CommandPanel(this.matchController); // contains buttons for throwing dice and ending turn
        this.infoPanel = new InfoPanel(this.matchController); // shows current player info (name, money, position)

        this.logArea = new TextArea(); // create text area for logs
        this.logArea.setEditable(false); // set not editable the log area
        final ScrollPane logScroll = new ScrollPane(this.logArea); // make log area scrollable
        logScroll.setFitToWidth(true);
        logScroll.setPrefHeight(100);

        // Posizionamento nel BorderPane (Equivalente di BorderLayout)
        this.root.setCenter(this.boardPanel.getRoot());
        this.root.setBottom(this.commandPanel.getRoot());
        this.root.setRight(this.infoPanel.getRoot()); // EAST -> RIGHT
        this.root.setTop(logScroll); // NORTH -> TOP
    }

    /**
     * Displays the main window.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    public final void start(final Stage stage) {
        Objects.requireNonNull(stage);
        final Scene scene = new Scene(this.root, 1024, 768);
        stage.setTitle("JavaPoly");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Redraws the BoardPanel to reflect current player positions.
     */
    public void updateBoard() {
        this.boardPanel.update(); // In JavaFX dovrai implementare update()
    }

    /**
     * Updates InfoPanel to reflect current player info (money, position, etc.).
     */
    public void updateInfo() {
        this.infoPanel.updateInfo();
    }

    /**
     * Adds a new message to the log area.
     *
     * @param msg The message to append.
     */
    public void addLog(String msg) {
        this.logArea.appendText(msg + "\n");
    }
}
