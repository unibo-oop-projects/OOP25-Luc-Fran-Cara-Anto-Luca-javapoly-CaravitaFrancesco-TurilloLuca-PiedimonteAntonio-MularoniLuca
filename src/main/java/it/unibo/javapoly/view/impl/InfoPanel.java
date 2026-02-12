package it.unibo.javapoly.view.impl;

import java.util.Objects;

import it.unibo.javapoly.view.api.SellAssetView;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;

/**
 * InfoPanel displays information about the current player,
 * such as name, balance, and board position.
 */
public class InfoPanel {

    private final VBox root;
    private final MatchController matchController;
    private final SellAssetViewImpl sellAssetView;
    private final VBox liquidation;
    
    /**
     * Constructor: creates labels and adds them to the panel.
     *
     * @param matchController reference to the game controller
     */
    public InfoPanel(final MatchController matchController){
        this.matchController = Objects.requireNonNull(matchController);

        this.root = new VBox(15);
        this.root.setPadding(new Insets(20));
        this.root.setPrefWidth(280); 
        this.root.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #CCCCCC; -fx-border-width: 0 0 0 1;");
        this.sellAssetView = new SellAssetViewImpl(this.matchController);
        this.liquidation = new VBox();
        this.liquidation.getChildren().add(this.sellAssetView.getRoot());
        this.sellAssetView.getRoot().setVisible(false);
        this.sellAssetView.getRoot().setManaged(false);
        this.root.getChildren().add(this.liquidation);
        this.updateInfo();
    }

    /**
     * Updates the labels to show current player's info.
     */
    public void updateInfo(){
        this.root.getChildren().clear();
        Label title = new Label("GIOCATORI");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setPadding(new Insets(0, 0, 10, 0));
        this.root.getChildren().add(title);
        
        for (Player p : this.matchController.getPlayers()) {
            this.root.getChildren().add(createPlayerCard(p));
        }

        this.root.getChildren().add(this.liquidation);
    }

    private VBox createPlayerCard(Player p){
        VBox card = new VBox(5);
        card.setPadding(new Insets(12));

        Label name = new Label(p.getName());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label balance = new Label("Saldo: " + p.getBalance() + "€");
        balance.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));

        Label position = new Label("Posizione: " + p.getCurrentPosition());
        position.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 11));

        card.getChildren().addAll(name, balance, position);

        String style = "-fx-background-radius: 10; -fx-background-color: white; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);";

        if (p.equals(this.matchController.getCurrentPlayer())) {
            style += "-fx-border-color: #4CAF50; -fx-border-width: 2.5; -fx-background-color: #F1F8E9;";
            name.setText("▶ " + p.getName()); 
        } else {
            style += "-fx-border-color: #D3D3D3; -fx-border-width: 1;";
        }

        card.setStyle(style);
        return card;
    }
    
    /**
     * Returns the root node of this panel to be added to a Scene.
     *
     * @return the {@link VBox} containing the labels.
     */
    public VBox getRoot() {
        return this.root;
    }

    /**
     * Get sell asset view.
     *
     * @return sell asset view.
     */
    public SellAssetView getSellAssetView() {
        return this.sellAssetView;
    }
}
