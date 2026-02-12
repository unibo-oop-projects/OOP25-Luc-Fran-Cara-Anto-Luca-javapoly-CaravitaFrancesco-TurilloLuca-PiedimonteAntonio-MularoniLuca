package it.unibo.javapoly.view.impl;

import java.util.Objects;

import it.unibo.javapoly.view.api.SellAssetView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;

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

            if (msg.contains("guadagna") || msg.contains("VIA") || msg.contains("+")) {
                textNode.setFill(Color.DARKGREEN);
                textNode.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            } else if (msg.contains("paga") || msg.contains("tassa") || msg.contains("prigione") || msg.contains("DOPPIO")) {
                textNode.setFill(Color.FIREBRICK);
            } else if (msg.contains("turno di")) {
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

    public void showLiquidation(Player p, int amount){
        SellAssetViewImpl liquidationView = new SellAssetViewImpl(this.matchController);
        liquidationView.show(p, amount);

        this.root.setCenter(liquidationView.getRoot());

        Button backButton = new Button("Torna al gioco");
        backButton.setStyle("-fx-base: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            if(this.matchController instanceof MatchController impl){
                impl.finalizeLiquidation(p);
            }
        });
        liquidationView.getRoot().setBottom(backButton);
    }

    /**
     * Get info panel.
     *
     * @return info panel.
     */
    public SellAssetView getSellAssetView() {
        return this.infoPanel.getSellAssetView();
    }

    public void clearLog(){
        this.logContainer.getChildren().clear();
    }

    public BorderPane getRoot(){
        return this.root;
    }
}
