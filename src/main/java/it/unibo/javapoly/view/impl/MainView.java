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
public class MainView {

    private final BorderPane root;
    private final BoardPanel boardPanel; // panel displaying the game board
    private final CommandPanel commandPanel; // panel with action buttons (throw dice, end turn)
    private final InfoPanel infoPanel; // panel showing player information
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

        this.boardPanel = new BoardPanel(this.matchController.getBoard(), this.matchController.getPlayers()); // shows the game board and player tokens
        this.commandPanel = new CommandPanel(this.matchController); // contains buttons for throwing dice and ending turn
        this.infoPanel = new InfoPanel(this.matchController); // shows current player info (name, money, position)

        this.logContainer = new VBox(5);
        this.logScroll = new ScrollPane(this.logContainer);
        this.logScroll.setFitToWidth(true);
        this.logScroll.setPrefWidth(250);

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
    public final void start(final Stage stage) {
        Objects.requireNonNull(stage);
        final Scene scene = new Scene(this.root, 1200, 800);
        stage.setTitle("JavaPoly - Monopoly Java Edition");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

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
    public void addLog(String msg) {
        Platform.runLater(() -> {
            Text textNode = new Text(msg);
            textNode.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13)); 

            String upperMsg = msg.toUpperCase();

            if (upperMsg.contains("PURCHASED") || upperMsg.contains("EARNED") || upperMsg.contains("COLLECT") || upperMsg.contains("+")) {
                textNode.setFill(Color.DARKGREEN);
                textNode.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            } else if (upperMsg.contains("PAYS") || upperMsg.contains("TAX") || upperMsg.contains("JAIL") || upperMsg.contains("DOUBLE") || upperMsg.contains("DEBT")) {
                textNode.setFill(Color.FIREBRICK);
            } else if (upperMsg.contains("TURN")) {
                textNode.setFill(Color.CORNFLOWERBLUE);
                textNode.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            } else {
                textNode.setFill(Color.BLACK);
            }

            VBox messageBox = new VBox(textNode);
            messageBox.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));
  
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

    public void showCard(final String title, final String description, final boolean isImprevisto){
        Platform.runLater(() -> {
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CHANCE CARD!");
            alert.setHeaderText(title);
            alert.setContentText(description);

            alert.getDialogPane().setStyle("-fx-border-color: #e74c3c; -fx-border-width: 5px;");

            alert.showAndWait();
        });
    }

    public void showWinner(String winnerName){
        Platform.runLater(() -> {
            this.infoPanel.getRoot().setDisable(true);
            this.commandPanel.getRoot().setDisable(true);
            this.boardPanel.getRoot().setDisable(true);

            this.addLog("---------------------------");
            this.addLog("   PLAYER " + winnerName.toUpperCase() + " WON!   ");
            this.addLog("---------------------------");
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    public void clearLog(){
        this.logContainer.getChildren().clear();
    }

    public BorderPane getRoot(){
        return this.root;
    }
}
