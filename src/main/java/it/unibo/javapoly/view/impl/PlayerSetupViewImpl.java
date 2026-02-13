package it.unibo.javapoly.view.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;
import java.util.Objects;

import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.view.api.PlayerSetupView;
import static it.unibo.javapoly.view.impl.MenuViewImpl.BG_COLOR;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Class that represent the view for the setup of player.
 */
public class PlayerSetupViewImpl implements PlayerSetupView {
    private static final int MIN_PLAYER = 2;
    private static final int MAX_PLAYER = 4;
    private static final int MAX_CHAR_NAME = 12;
    private static final double SPACING = 0.05;
    private static final double HEIGHT_TEXT_FIELDS = 0.05;
    private static final double WIDTH_TEXT_FIELDS = 0.20;

    private final BorderPane root;
    private final VBox playerFields;
    private final List<TextField> playerTextFields;
    private final ChoiceBox<Integer> playerCountChoice;
    private final Button confirmButton;
    private final List<ComboBox<TokenType>> playerTokenSelectors;
    private final List<String> customTokenPaths = new ArrayList<>();

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
        this.playerTokenSelectors = new ArrayList<>();
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
        playerTokenSelectors.clear();
        customTokenPaths.clear();
        for (int k = 0; k < count; k++) {
            customTokenPaths.add(null);
        }

        playerFields.spacingProperty().bind(root.heightProperty().multiply(SPACING));

        final TokenType[] availableTokens = TokenType.values();

        for (int i = 1; i <= count; i++) {
            final int playerIndex = i - 1;

            final HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER);

            final TextField field = new TextField();
            field.setPromptText("Player" + i + "'s name");
            field.maxWidthProperty().bind(this.stage.widthProperty().multiply(WIDTH_TEXT_FIELDS));
            field.prefHeightProperty().bind(this.stage.heightProperty().multiply(HEIGHT_TEXT_FIELDS));

            final ComboBox<TokenType> tokenBox = new ComboBox<>();
            tokenBox.getItems().addAll(availableTokens);
            tokenBox.setPromptText("Token");
            tokenBox.prefHeightProperty().bind(this.stage.heightProperty().multiply(HEIGHT_TEXT_FIELDS));

            // --- smart default logic ---
            final int tokenIndex = i - 1;
            if (tokenIndex < availableTokens.length) {
                final TokenType candidate = availableTokens[tokenIndex];
                if (candidate != TokenType.CUSTOM) {
                    tokenBox.setValue(candidate);
                } else {
                    tokenBox.setValue(TokenType.BOAT);
                }
            } else {
                // fallback if there are more players than available tokens excluding CUSTOM.
                tokenBox.setValue(TokenType.BOAT);
            }

            // --- listener for custom selection ---
            tokenBox.setOnAction(e -> {
                if (tokenBox.getValue() == TokenType.CUSTOM) {
                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose token image");
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

                    final File file = fileChooser.showOpenDialog(this.stage);

                    if (file != null) {
                        // save the absolute path as URI
                        customTokenPaths.set(playerIndex, file.toURI().toString());
                        // visual feedback to indicate a custom token has been selected
                        tokenBox.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                    } else {
                        // if canceled, reset to a safe value
                        tokenBox.setValue(TokenType.CAR); // this trigger setOnAction again but goes to the else
                        customTokenPaths.set(playerIndex, null);
                    }
                } else {
                    // if the user selects a normal enum reset the path and style
                    customTokenPaths.set(playerIndex, null);
                    tokenBox.setStyle("");
                }
            });

            playerTextFields.add(field);
            playerTokenSelectors.add(tokenBox);

            row.getChildren().addAll(field, tokenBox);
            playerFields.getChildren().add(row);
        }
    }

    /**
     * Get the list of selected tokens.
     * 
     * @return list of tokens.
     */
    private List<TokenType> getTokenList() {
        return playerTokenSelectors.stream()
                .map(ComboBox::getValue)
                .toList();
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

        final List<TokenType> tokens = getTokenList();

        if (tokens.stream().anyMatch(Objects::isNull)) {
            showError("All players must select a token");
            return false;
        }

        final Set<TokenType> uniqueTokens = EnumSet.copyOf(tokens);
        if (uniqueTokens.size() != tokens.size()) {
            showError("Players cannot choose the same token");
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
                controller.playerSetupConfirmed(getNameList(), getTokenList(), this.customTokenPaths);
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
     *
     * <p>
     * Suppressed EI_EXPOSE_REP waring: the exposure of the
     * internal instance is intentional and consistent with the architectural role of this class.
     * </p>
     */
    @SuppressWarnings("EI_EXSPOSE_REP")
    @Override
    public BorderPane getRoot() {
        return root;
    }
}
