package it.unibo.javapoly.view.impl;

import java.util.Locale;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.view.api.MainView;

/**
 * MainFrame is the main window of the JavaPoly game.
 * It contains the game board, player info, action buttons, and a log area.
 */
public final class MainViewImpl implements MainView {

    private static final int LOG_SPACING = 5;
    private static final int LOG_PREF_WIDTH = 250;
    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 800;
    private static final int FONT_SIZE_SMALL = 13;
    private static final int FONT_SIZE_MEDIUM = 14;
    private static final int INSET_VAL = 5;
    private static final int INSET_SIDE_VAL = 10;
    private static final String FONT_FAMILY = "Segoe UI";

    private final BorderPane root;
    private final BoardPanelImpl boardPanel; 
    private final CommandPanelImpl commandPanel; 
    private final InfoPanelImpl infoPanel; 
    private final MatchController matchController;

    private final VBox logContainer;
    private final ScrollPane logScroll;

    /**
     * Constructor: initializes the layout and components.
     *
     * @param matchController the controller that manages the game logic.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "Property is intentionally shared and mutable in game model"
    )
    public MainViewImpl(final MatchController matchController) {
        this.matchController = Objects.requireNonNull(matchController);
        this.root = new BorderPane();

        this.boardPanel = new BoardPanelImpl(this.matchController.getBoard(), this.matchController.getPlayers()); 
        this.commandPanel = new CommandPanelImpl(this.matchController);
        this.infoPanel = new InfoPanelImpl(this.matchController); 

        this.logContainer = new VBox(LOG_SPACING);
        this.logScroll = new ScrollPane(this.logContainer);
        this.logScroll.setFitToWidth(true);
        this.logScroll.setPrefWidth(LOG_PREF_WIDTH);

        this.root.setCenter(this.boardPanel.getRoot());
        this.root.setBottom(this.commandPanel.getRoot());
        this.root.setRight(this.infoPanel.getRoot()); 
        this.root.setLeft(this.logScroll); 

        this.logContainer.heightProperty().addListener((obs, oldVal, newVal) -> 
            this.logScroll.setVvalue(1.0)
        );
    }

    /**
     * Displays the main window.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(final Stage stage) {
        Objects.requireNonNull(stage);
        final Scene scene = new Scene(this.root, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("JavaPoly - Monopoly Java Edition");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Refreshes all UI components.
     */
    @Override
    public void refreshAll() {
        this.boardPanel.update();
        this.infoPanel.updateInfo();
        this.commandPanel.updateState();
    }

    /**
     * Adds a new message to the log area.
     *
     * @param msg The message to append.
     */
    @Override
    public void addLog(final String msg) {
        Platform.runLater(() -> {
            final Text textNode = new Text(msg);
            textNode.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE_SMALL)); 

            final String upperMsg = msg.toUpperCase(Locale.ROOT);

            if (upperMsg.contains("PURCHASED") || upperMsg.contains("EARNED") 
                    || upperMsg.contains("COLLECT") || upperMsg.contains("+")) {
                textNode.setFill(Color.DARKGREEN);
                textNode.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_SMALL));
            } else if (upperMsg.contains("PAYS") || upperMsg.contains("TAX") 
                    || upperMsg.contains("JAIL") || upperMsg.contains("DOUBLE") 
                    || upperMsg.contains("DEBT")) {
                textNode.setFill(Color.FIREBRICK);
            } else if (upperMsg.contains("TURN")) {
                textNode.setFill(Color.CORNFLOWERBLUE);
                textNode.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_MEDIUM));
            } else {
                textNode.setFill(Color.BLACK);
            }

            final VBox messageBox = new VBox(textNode);
            messageBox.setPadding(new javafx.geometry.Insets(INSET_VAL, INSET_SIDE_VAL, INSET_VAL, INSET_SIDE_VAL));
            this.logContainer.getChildren().add(messageBox);
        });
    }

    /**
     * Shows the liquidation view and disables game controls.
     */
    @Override
    public void showLiquidation() {
        Platform.runLater(() -> {
            this.infoPanel.showSellAssetView();
            this.commandPanel.getRoot().setDisable(true);
        });
    }

    /**
     * Hides the liquidation view and re-enables game controls.
     */
    @Override
    public void hideLiquidation() {
        Platform.runLater(() -> {
            this.infoPanel.hideSellAssetView();
            this.commandPanel.getRoot().setDisable(false);
        });
    }

    /**
     * Shows a card alert.
     * 
     * @param title card title.
     * @param description card description.
     */
    @Override
    public void showCard(final String title, final String description) {
        Platform.runLater(() -> {
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CHANCE CARD!");
            alert.setHeaderText(title);
            alert.setContentText(description);
            alert.getDialogPane().setStyle("-fx-border-color: #e74c3c; -fx-border-width: 5px;");
            alert.showAndWait();
        });
    }

    /**
     * Shows bankrupt alert.
     * 
     * @param playerName name of player.
     */
    @Override
    public void showBankruptAlert(final String playerName) {
        Platform.runLater(() -> {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("BANKRUPTCY!");
            alert.setHeaderText("Game Over for " + playerName);
            alert.setContentText(playerName + " has run out of money and assets. " 
            + "All their properties have been returned to the bank.");
            alert.getDialogPane().setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 5px;");
            alert.showAndWait();
        });
    }

    /**
     * Shows the winner of the game.
     * 
     * @param winnerName name of winner.
     */
    @Override
    public void showWinner(final String winnerName) {
        Platform.runLater(() -> {
            this.infoPanel.getRoot().setDisable(true);
            this.commandPanel.getRoot().setDisable(true);
            this.boardPanel.getRoot().setDisable(true);

            this.addLog("---------------------------");
            this.addLog("   PLAYER " + winnerName.toUpperCase(Locale.ROOT) + " WON!   ");
            this.addLog("---------------------------");
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Victory!!");
            alert.setHeaderText("🏆 We have a Winner!");
            alert.setContentText("Congratulations " + winnerName + ", you are the tycoon of JavaPoly!");
            alert.getDialogPane().setStyle("-fx-border-color: #f1c40f; -fx-border-width: 5px;");
            alert.showAndWait();
        });
    }

    /**
     * Get access to InfoPanel for delegated liquidation operations.
     *
     * @return the InfoPanel instance.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Necessary to expose the info panel for liquidation UI"
    )
    public InfoPanelImpl getInfoPanel() {
        return this.infoPanel;
    }

    /**
     * Clears the log area.
     */
    @Override
    public void clearLog() {
        this.logContainer.getChildren().clear();
    }

    /**
     * Returns the root node.
     * 
     * @return the borderpane root.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "JavaFX nodes must be exposed"
    )
    @Override
    public BorderPane getRoot() {
        return this.root;
    }
}
