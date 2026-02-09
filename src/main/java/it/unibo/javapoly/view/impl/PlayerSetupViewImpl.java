package it.unibo.javapoly.view.impl;

import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.view.api.PlayerSetupView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static it.unibo.javapoly.view.impl.MenuViewImpl.BG_COLOR;

/**
 * Class that represent the view for the setup of player.
 */
public class PlayerSetupViewImpl implements PlayerSetupView {
    private static final int MIN_PLAYER = 2;
    private static final int MAX_PLAYER = 4;
    private static final int MAX_CHAR_NAME = 20;
    private static final double SPACING = 0.05;
    private static final double HEIGHT_TEXT_FIELDS = 0.05;
    private static final double WIDTH_TEXT_FIELDS = 0.20;

    private final BorderPane root;
    private final VBox playerFields;
    private final List<TextField> playerTextFields;
    private final ChoiceBox<Integer> playerCountChoice;
    private final Button confirmButton;

    private Stage stage;
    private MenuController controller;

    /**
     * Constructor that setup view.
     */
    public PlayerSetupViewImpl() {
        this.root = new BorderPane();
        this.playerFields = new VBox();
        playerFields.setAlignment(Pos.CENTER);
        playerFields.setSpacing(0);
        this.playerTextFields = new ArrayList<>();
        this.playerCountChoice = new ChoiceBox<>();
        this.confirmButton = new Button("Confirm");
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        this.root.setTop(createHeader());
        this.root.setCenter(this.playerFields);
        this.root.setBottom(createBottom());
        this.root.setStyle(BG_COLOR);
    }

    /**
     * Create the header section with player count selection.
     * 
     * @return HBox containing the header elements.
     */
    private HBox createHeader() {
        final HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        final Label label = new Label("Select number of player:");
        initializePlayerCountChoice();
        header.getChildren().addAll(label, this.playerCountChoice);
        return header;
    }

    /**
     * Create the bottom section with player count selection.
     *
     * @return HBox containing the confirm button.
     */
    private HBox createBottom() {
        final HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(this.confirmButton);
        bottom.setSpacing(SPACING);
        return bottom;
    }

    /**
     * Initialize the player count choice box.
     */
    private void initializePlayerCountChoice() {
        for (int i = MIN_PLAYER; i <= MAX_PLAYER; i++) {
            this.playerCountChoice.getItems().add(i);
        }
        this.playerCountChoice.setValue(MIN_PLAYER);
        this.playerCountChoice.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        updatePlayerFields(newVal);
                    }
                });
    }

    /**
     * Updates the player name input fields based on the selected count.
     *
     * @param count the number of player.
     */
    private void updatePlayerFields(final int count) {
        playerFields.getChildren().clear();
        playerTextFields.clear();
        playerFields.spacingProperty().bind(root.heightProperty().multiply(SPACING));
        for (int i = 1; i <= count; i++) {
            final TextField field = new TextField();
            field.setPromptText("Player" + i + "'s name");
            field.maxWidthProperty().bind(this.stage.widthProperty().multiply(WIDTH_TEXT_FIELDS));
            field.prefHeightProperty().bind(this.stage.heightProperty().multiply(HEIGHT_TEXT_FIELDS));
            playerTextFields.add(field);
            playerFields.getChildren().add(field);
        }
    }

    /**
     * Validates all player names.
     *
     * @return true if all names are valid, false otherwise.
     */
    private boolean validateAllPlayerName() {
        final List<String> names = getNameList();
        if (names.stream().anyMatch(String::isEmpty)) {
            showError("All player must have a name");
            return false;
        }
        final Set<String> checkUniqueName = new HashSet<>(names);
        if (checkUniqueName.size() != names.size()) {
            showError("All player must have unique names");
            return false;
        }
        if (names.stream().anyMatch(name -> !name.matches("^[a-zA-z]+$"))) {
            showError("Player name can contain only letters");
            return false;
        }
        if (names.stream().anyMatch(name -> name.length() > MAX_CHAR_NAME)) {
            showError("Player name can contain only 20 letters");
            return false;
        }
        return true;
    }

    private List<String> getNameList() {
        return playerTextFields.stream()
                .map(TextField::getText)
                .map(String::trim)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setController(final MenuController controller) {
        this.controller = controller;
        this.confirmButton.setOnAction(event -> {
            if (!validateAllPlayerName()) {
                return;
            }
            if (this.controller != null) {
                controller.playerSetupConfirmed(getNameList());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStage(final Stage stage) {
        this.stage = stage;
        updatePlayerFields(MIN_PLAYER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BorderPane getRoot() {
        return root;
    }
}
