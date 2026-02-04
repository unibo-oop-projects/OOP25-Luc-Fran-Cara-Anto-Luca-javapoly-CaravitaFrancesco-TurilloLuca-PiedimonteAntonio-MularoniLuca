package it.unibo.javapoly.view.impl;

import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;

/**
 * InfoPanel displays information about the current player,
 * such as name, balance, and board position.
 */
public class InfoPanel {

    private final VBox root;
    private final MatchController matchController;
    private final Label nameLabel; //shows player name
    private final Label balanceLabel; //shows player balance
    private final Label positionLabel; //shows player position on the board
    
    /**
     * Constructor: creates labels and adds them to the panel.
     *
     * @param matchController reference to the game controller
     */
    public InfoPanel(final MatchController matchController){
        this.matchController = Objects.requireNonNull(matchController);

        // In JavaFX usiamo i nodi specifici, non estendiamo JPanel
        this.root = new VBox(10); // Spaziatura verticale di 10px

        this.nameLabel = new Label();
        this.balanceLabel = new Label();
        this.positionLabel = new Label();

        // Aggiunta dei figli al contenitore radice
        this.root.getChildren().addAll(this.nameLabel, this.balanceLabel, this.positionLabel);

        this.updateInfo();
    }

    /**
     * Updates the labels to show current player's info.
     */
    public void updateInfo(){
        final Player p = this.matchController.getCurrentPlayer();

        this.nameLabel.setText("Giocatore: " + p.getName()); 
        this.balanceLabel.setText("Saldo: "); //balance del giocatore(da aggiungere)
        this.positionLabel.setText("Posizione: "); //posizione del giocatore (da aggiungere)
    }

    /**
     * Returns the root node of this panel to be added to a Scene.
     *
     * @return the {@link VBox} containing the labels.
     */
    public VBox getRoot() {
        return this.root;
    }
}
