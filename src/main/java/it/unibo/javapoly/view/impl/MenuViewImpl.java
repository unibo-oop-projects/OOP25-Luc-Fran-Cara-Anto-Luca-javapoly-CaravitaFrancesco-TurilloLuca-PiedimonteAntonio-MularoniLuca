package it.unibo.javapoly.view.impl;

import it.unibo.javapoly.controller.api.MenuController;
import it.unibo.javapoly.view.api.PlayerSetupView;
import it.unibo.javapoly.view.api.MenuView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the main menu view for the Javapoly application.
 */
public class MenuViewImpl implements MenuView {
    public static final String BG_COLOR = "-fx-background-color: #edfbea;";
    public static final String TITLE = "Javapoly";
    private static final String MENU = " - Menu";
    private static final String SETUP = " - Setup player";
    private static final String ICON_PATH = "/images/javapolyIcon.png";
    private static final String LOGO_PATH = "/images/javapolyLogo.png";
    private static final String[] DEVELOPERS = {"Francesco Caravita", "Luca Turillo", "Antonio Piedimonte", "Luca Mularoni"};
    private static final int TOP_PADDING = 20;
    private static final double BUTTON_WIDTH_PER = 0.13;
    private static final double BUTTON_HEIGHT_PER = 0.05;
    private static final double SPACING = 0.02;
    private final Stage stage;
    private MenuController controller;

    /**
     * Constructor a new MenuViewImpl with the specific stage.
     *
     * @param stage the primary stage for the view.
     */
    public MenuViewImpl(final Stage stage) {
        this.stage = Objects.requireNonNull(stage);
        configureStage();
        buildLayout();
    }

    /**
     * Configures the stage properties.
     */
    private void configureStage() {
        this.stage.setTitle(TITLE + MENU);
        this.stage.setMaximized(true);
        this.stage.setResizable(false);
        loadIcon();
    }

    /**
     * Loads the window icon if available.
     */
    private void loadIcon() {
        final var iconStream = MenuViewImpl.class.getResourceAsStream(ICON_PATH);
        if (iconStream == null) {
            System.err.println("Icon loading failed.");
            return;
        }
        final Image icon = new Image(iconStream);
        if (icon.isError()) {
            System.err.println("Icon loading failed.");
            return;
        }
        this.stage.getIcons().add(icon);
    }

    /**
     * Create, builds and arranges UI section in the root BorderPane.
     */
    private void buildLayout() {
        final BorderPane root = new BorderPane();
        root.setStyle(BG_COLOR);
        root.setTop(createTopSection());
        root.setCenter(createCenterSection());
        root.setBottom(createCreditSection());
        final Scene scene = new Scene(root);
        this.stage.setScene(scene);
    }

    /**
     * Creates the top section with logo.
     *
     * @return Vbox containing the top section components.
     */
    private VBox createTopSection() {
        final VBox topBox = new VBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(TOP_PADDING));
            final var logoStream = MenuViewImpl.class.getResourceAsStream(LOGO_PATH);
            if (logoStream == null) {
                System.err.println("Logo not found");
                topBox.getChildren().add(createTitleLabel());
                return topBox;
            }
        try (var stream = logoStream) {
            final Image logo = new Image(stream);
            final ImageView logoView = new ImageView(logo);
            logoView.fitWidthProperty().bind(stage.widthProperty().multiply(0.50));
            logoView.setPreserveRatio(true);
            topBox.getChildren().add(logoView);
        } catch (final IOException e) {
            topBox.getChildren().add(createTitleLabel());
        }
        return topBox;
    }

    /**
     * Creates the fallback title label.
     *
     * @return label with title.
     */
    private Label createTitleLabel() {
        final Label titleLabel = new Label(TITLE);
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold");
        return titleLabel;
    }

    /**
     * Creates the center section with three buttons.
     *
     * @return a Vbox containing the center section components.
     */
    private VBox createCenterSection() {
        final VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.spacingProperty().bind(this.stage.heightProperty().multiply(SPACING));
        final Button newGameButton = createMenuButton("New Game", e -> showPlayerSetupView());
        final Button loadGameButton = createMenuButton("Load Game", e -> showLoadGameView());
        final Button exitButton = createMenuButton("Exit", e -> controller.exitGame());
        centerBox.getChildren().addAll(newGameButton, loadGameButton, exitButton);
        return centerBox;
    }

    /**
     * Creates button with dynamic sizing bound to the stage dimension.
     *
     * @param text The label text to display on the button.
     * @param action The event handler to execute when the button is clicked.
     * @return A configured Button instance.
     */
    private Button createMenuButton(final String text, final javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        final Button button = new Button(text);
        button.setOnAction(action);
        button.prefWidthProperty().bind(this.stage.widthProperty().multiply(BUTTON_WIDTH_PER));
        button.prefHeightProperty().bind(this.stage.heightProperty().multiply(BUTTON_HEIGHT_PER));
        return button;
    }

    /**
     * Creates the botton developer credits section.
     *
     * @return HBox with developer labels.
     */
    private HBox createCreditSection() {
        final HBox creditBox = new HBox();
        creditBox.setAlignment(Pos.CENTER);
        creditBox.spacingProperty().bind(stage.widthProperty().multiply(SPACING));
        creditBox.paddingProperty().bind(javafx.beans.binding.Bindings.createObjectBinding(
                () -> new Insets(0, 0, stage.getHeight() * SPACING, 0),
                stage.heightProperty()));

        Arrays.stream(DEVELOPERS).forEach(dev -> creditBox.getChildren().add(new Label(dev)));
        return creditBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setController(final MenuController controller) {
        this.controller = controller;
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
    public void showPlayerSetupView() {
        final PlayerSetupView view = new PlayerSetupViewImpl();
        view.setController(controller);
        view.setStage(this.stage);
        this.stage.getScene().setRoot(view.getRoot());
        this.stage.setTitle(TITLE + SETUP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLoadGameView() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Save Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON file", "*.json"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File selectedFile = fileChooser.showOpenDialog(this.stage);
        if (selectedFile != null) {
            controller.loadGame(selectedFile);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stage getStage() {
        return this.stage;
    }
}
