package it.unibo.javapoly.view.impl;

import java.util.Objects;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import it.unibo.javapoly.controller.api.MatchController;

/**
 * CommandPanel contains the buttons for player actions,
 * such as throwing the dice and ending the turn.
 */
public class CommandPanel {

    private final HBox root;
    private final MatchController matchController;
    private final Button throwDice;
    private final Button endTurnButton;

    /**
     * Constructor: creates the panel and its buttons.
     *
     * @param matchController the controller that handles game logic
     */
    public CommandPanel(final MatchController matchController){
        this.matchController = Objects.requireNonNull(matchController);

        // Usiamo HBox per disporre i bottoni orizzontalmente
        this.root = new HBox(15); // Spaziatura di 15px tra i bottoni

        this.throwDice = new Button("Lancia dadi");
        this.endTurnButton = new Button("Fine turno");

        // JavaFX usa setOnAction con espressioni lambda
        this.throwDice.setOnAction(e -> 
            this.matchController.handleDiceThrow());
            updateState();
        this.endTurnButton.setOnAction(e -> 
            this.matchController.nextTurn());
            updateState();

        // Aggiunta dei nodi alla radice del pannello
        this.root.getChildren().addAll(this.throwDice, this.endTurnButton);

        updateState();
    }

    public void updateState(){
        boolean canRoll = matchController.canCurrentPlayerRoll();
        this.throwDice.setDisable(!canRoll);
        this.endTurnButton.setDisable(canRoll);
    }

    /**
     * Returns the root node of this command panel.
     *
     * @return the {@link HBox} containing the action buttons.
     */
    public HBox getRoot() {
        return this.root;
    }
}
