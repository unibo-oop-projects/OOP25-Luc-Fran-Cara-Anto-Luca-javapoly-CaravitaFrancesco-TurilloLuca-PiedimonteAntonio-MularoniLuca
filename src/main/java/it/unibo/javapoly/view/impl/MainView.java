package it.unibo.javapoly.view.impl;

import java.util.Objects;

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

/**
 * MainFrame is the main window of the JavaPoly game.
 * It contains the game board, player info, action buttons, and a log area.
 */
public final class MainView {

    private static final int LOG_SPACING = 5;
    private static final int LOG_PREF_WIDTH = 250;
    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 800;
    private static final int FONT_SIZE_SMALL = 13;
    private static final int FONT_SIZE_MEDIUM = 14;
    private static final int INSET_VAL = 5;
    private static final int INSET_SIDE_VAL = 10;
    private static final int BORDER_WIDTH_VAL = 5;
    private static final String FONT_FAMILY = "Segoe UI";

    private final BorderPane root;
    private final BoardPanel boardPanel; 
    private final CommandPanel commandPanel; 
    private final InfoPanel infoPanel; 
    private final MatchController matchController;

    private final VBox logContainer;
    private final ScrollPane logScroll;

    /**
     * Constructor: initializes the layout and components.
     *
     * @param matchController the controller that manages the game logic.
     */
    public MainView(final MatchController matchController) {
        this.matchController = Objects.requireNonNull(matchController);
        this.root = new BorderPane();

        this.boardPanel = new BoardPanel(this.matchController.getBoard(), this.matchController.getPlayers()); 
        this.commandPanel = new CommandPanel(this.matchController);
        this.infoPanel = new InfoPanel(this.matchController); 

        this.logContainer = new VBox(LOG_SPACING);
        this.logScroll = new ScrollPane(this.logContainer);
        this.logScroll.setFitToWidth(true);
        this.logScroll.setPrefWidth(LOG_PREF_WIDTH);

        this.root.setCenter(this.boardPanel.getRoot());
        this.root.setBottom(this.commandPanel.getRoot());
        this.root.setRight(this.infoPanel.getRoot()); 
        this.root.setLeft(this.logScroll); 
    }

    /**
     * Displays the main window.
     *
     * @param stage the primary stage provided by JavaFX.
     */
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
    public void addLog(final String msg) {
        Platform.runLater(() -> {
            final Text textNode = new Text(msg);
            textNode.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE_SMALL)); 

           final String upperMsg = msg.toUpperCase();

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
            this.logContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                this.logScroll.setVvalue(1.0);
            });
        });
    }

    /**
     * Shows the liquidation view and disables game controls.
     */
    public void showLiquidation() {
        Platform.runLater(() -> {
            this.infoPanel.showSellAssetView();
            this.commandPanel.getRoot().setDisable(true);
        });
    }

    /**
     * Hides the liquidation view and re-enables game controls.
     */
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
    public void showWinner(final String winnerName) {
        Platform.runLater(() -> {
            this.infoPanel.getRoot().setDisable(true);
            this.commandPanel.getRoot().setDisable(true);
            this.boardPanel.getRoot().setDisable(true);

            this.addLog("---------------------------");
            this.addLog("   PLAYER " + winnerName.toUpperCase() + " WON!   ");
            this.addLog("---------------------------");
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Victory!!");
            alert.setHeaderText("🏆 We have a Winner!");
            alert.setContentText("Congratulations" + winnerName + ", you are the tycoon of JavaPoly!");
            alert.getDialogPane().setStyle("-fx-border-color: #f1c40f; -fx-border-width: 5px;");
            alert.showAndWait();
        });
    }

    /**
     * Get access to InfoPanel for delegated liquidation operations.
     *
     * @return the InfoPanel instance.
     */
    public InfoPanel getInfoPanel() {
        return this.infoPanel;
    }

    /**
     * Clears the log area.
     */
    public void clearLog() {
        this.logContainer.getChildren().clear();
    }

    /**
     * Returns the root node.
     * 
     * @return the borderpane root.
     */
    public BorderPane getRoot() {
        return this.root;
    }
}
