package it.unibo.javapoly.view.impl;

import it.unibo.javapoly.controller.api.MenuController;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the main menu view for the Javapoly application.
 */
public class MenuViewImpl implements MenuView {

    private static final String TITLE = "Javapoly";
    private static final String MENU = " - Menu";
    private static final String ICON_PATH = "/images/javapolyICON.png";
    private static final String LOGO_PATH = "/images/javapolyLogo.png";
    private static final int TOP_PADDING = 20;
    private static final String[] DEVELOPERS = {"Francesco Caravita", "Luca Turillo", "Antonio Piedimonte", "Luca Mularoni"};
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
        Objects.requireNonNull(stage);
        this.stage = stage;
        initializeUI();
    }

    /**
     * Fully initializes UI components, stage, and layout.
     */
    private void initializeUI() {
        configureStage();
        buildLayout();
    }

    /**
     * Configures the stage properties.
     */
    private void configureStage() {
        stage.setTitle(TITLE + MENU);
        stage.setFullScreen(true);
        stage.setResizable(false);
        loadIcon();
    }

    /**
     * Loads the window icon if available.
     */
    private void loadIcon() {
        try {
            final var iconStream = getClass().getResourceAsStream(ICON_PATH);
            if (iconStream == null) {
                System.err.println("Icon loading failed.");
                return;
            }
            final Image icon = new Image(iconStream);
            stage.getIcons().add(icon);
        } catch (final NullPointerException e) {
            System.err.println("Icon loading failed.");
        }
    }

    /**
     * Create, builds and arranges UI section in the root BorderPane.
     */
    private void buildLayout() {
        final BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #edfbea;");

        root.setTop(createTopSection());
        root.setCenter(createCenterSection());
        root.setBottom(createCreditSection());
        final Scene scene = new Scene(root);
        stage.setScene(scene);
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

            final var logoStream = getClass().getResourceAsStream(LOGO_PATH);
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
        centerBox.spacingProperty().bind(stage.heightProperty().multiply(SPACING));
        final Button newGameButton = createMenuButton("New Game", null);
        final Button loadGameButton = createMenuButton("Load Game", null);
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
        button.prefWidthProperty().bind(stage.widthProperty().multiply(BUTTON_WIDTH_PER));
        button.prefHeightProperty().bind(stage.heightProperty().multiply(BUTTON_HEIGHT_PER));
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

        Arrays.stream(DEVELOPERS).forEach(dev -> creditBox.getChildren().add(createDeveloperLabel(dev)));
        return creditBox;
    }

    /**
     * Creates label with developer name.
     *
     * @param developerName name of developer
     * @return Label with developer name.
     */
    private Label createDeveloperLabel(final String developerName) {
        return new Label(developerName);
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

}
