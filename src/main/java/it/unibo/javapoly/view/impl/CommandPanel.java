package it.unibo.javapoly.view.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import it.unibo.javapoly.utils.JsonUtils;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.JailedState;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;

/**
 * CommandPanel contains the buttons for player actions,
 * such as throwing the dice and ending the turn.
 */
public class CommandPanel {

    private final HBox root;
    private final MatchController matchController;
    private final Button throwDice;
    private final Button endTurnButton;
    private final Button payJailButton;
    private final Button saveButton;
    private final Button buyButton;
    private final Button buildButton;

    /**
     * Constructor: creates the panel and its buttons.
     *
     * @param matchController the controller that handles game logic
     */
    public CommandPanel(final MatchController matchController){
        this.matchController = Objects.requireNonNull(matchController);

        this.root = new HBox(15); 

        this.throwDice = new Button("Lancia dadi");
        this.endTurnButton = new Button("Fine turno");
        this.payJailButton = new Button("Paga 50€");
        this.saveButton = new Button("Save");

        this.buyButton = new Button("Acquista Proprieta'");
        this.buyButton.setStyle("-fx-base: #2ecc71; -fx-text-fill: white;");
        this.buildButton = new Button("Costruisci la casa");
        this.buildButton.setStyle("-fx-base: #f1c40f;");

        this.payJailButton.setStyle("-fx-base: #e74c3c; -fx-text-fill: white;");

        this.throwDice.setOnAction(e -> {
            this.matchController.handleDiceThrow();
            updateState();
        });
        this.payJailButton.setOnAction(e -> {
            this.matchController.payToExitJail();
            updateState();
        });
        this.endTurnButton.setOnAction(e -> {
            this.matchController.nextTurn();
            updateState();
            saveStateGame();
        });
        this.saveButton.setOnAction(e -> {
            saveStateGame();
        });
        this.buyButton.setOnAction(e -> {
            this.matchController.buyCurrentProperty();
            updateState();
        });
        this.buildButton.setOnAction(e -> {
            Player p = matchController.getCurrentPlayer();
            Tile t = matchController.getBoard().getTileAt(p.getCurrentPosition());
            if (t instanceof PropertyTile pt) {
                this.matchController.buildHouseOnProperty(pt.getProperty());
                updateState();
            }
        });
        this.root.getChildren().addAll(
            this.throwDice,
            this.buyButton,
            this.buildButton,
            this.payJailButton,
            this.endTurnButton,
            this.saveButton
        );
    }

    public void updateState() {
        Player current = matchController.getCurrentPlayer();
        boolean hasRolled = !matchController.canCurrentPlayerRoll();

        this.throwDice.setDisable(hasRolled);
        this.endTurnButton.setDisable(!hasRolled);

        Tile currentTile = matchController.getBoard().getTileAt(current.getCurrentPosition());

        this.buyButton.setVisible(false);
        this.buyButton.setManaged(false);
        this.buildButton.setVisible(false);
        this.buildButton.setManaged(false);

        if(currentTile instanceof PropertyTile pt){
            Property prop = pt.getProperty();
            boolean isUnowned = prop.isOwnedByPlayer() == false;
            //qui da cambiare

            if(isUnowned){
                this.buyButton.setVisible(true);
                this.buyButton.setManaged(true);
                this.buyButton.setDisable(!hasRolled);
            }else if(current.getName().equals(prop.getIdOwner())){
                this.buildButton.setVisible(true);
                this.buildButton.setManaged(true);
                this.buildButton.setDisable(!hasRolled);
            }
        }

        boolean isJailed = current.getState() instanceof JailedState;

        this.payJailButton.setVisible(isJailed);
        this.payJailButton.setManaged(isJailed);
        this.payJailButton.setDisable(hasRolled);

        this.root.requestLayout();
    }

    /**
     * Returns the root node of this command panel.
     *
     * @return the {@link HBox} containing the action buttons.
     */
    public HBox getRoot() {
        return this.root;
    }

    /**
     * Method to save the game state on javapoly_save.json file in user directory.
     */
    public void saveStateGame() {
        try {
            final String userHome = System.getProperty("user.home");
            final Path saveDir = Paths.get(userHome);
            final Path saveFile = saveDir.resolve("javapoly_save.json");
            JsonUtils.getInstance().mapper().writeValue(saveFile.toFile(), this.matchController);
        } catch (final IOException ex) {
            System.err.println("Failed to save game " + ex.getMessage());
        }
    }
}
