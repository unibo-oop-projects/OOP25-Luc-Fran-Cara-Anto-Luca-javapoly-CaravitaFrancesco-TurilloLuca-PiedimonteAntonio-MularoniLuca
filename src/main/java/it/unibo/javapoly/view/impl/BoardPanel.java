package it.unibo.javapoly.view.impl;

import java.util.List;
import java.util.Objects;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

/**
 * BoardPanel handles the visual representation of the board.
 */
public class BoardPanel {

    private final GridPane root;
    private final Board board;
    private final List<Player> players;

    public BoardPanel(final Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board);
        this.players = Objects.requireNonNull(players);
        this.root = new GridPane();

        this.root.setStyle("-fx-background-color: #CDE6D0; -fx-padding: 5; -fx-border-color: black;");
        this.root.setAlignment(Pos.CENTER);

        this.renderBoard();
    }

    private String getColorForOwner(String ownerId) {
        int hash = ownerId.hashCode();
        String[] colors = { "#e74c3c", "#3498db", "#f1c40f", "#9b59b6", "#e67e22" };
        return colors[Math.abs(hash) % colors.length];
    }

    private StackPane createTileUI(final Tile tile, final int index) {
        final StackPane container = new StackPane();
        final VBox tileDesign = new VBox();
        tileDesign.setStyle("-fx-border-color: black; -fx-background-color: white;");
        tileDesign.setAlignment(Pos.TOP_CENTER);

        if (tile instanceof PropertyTile pt) {
            Property prop = pt.getProperty();
            if (prop.getIdOwner() != null) {
                Pane ownerBar = new Pane();
                ownerBar.setPrefHeight(10);
                ownerBar.setStyle("-fx-background-color: " + getColorForOwner(prop.getIdOwner()) + ";");
                tileDesign.getChildren().add(ownerBar);
            }

            HBox houseContainer = new HBox(2);
            houseContainer.setAlignment(Pos.CENTER);
            houseContainer.setPrefHeight(15);

            int houseCount = prop.getBuiltHouses();
            for (int i = 0; i < houseCount; i++) {
                Circle house = new Circle(4, Color.GREEN);
                houseContainer.getChildren().add(house);
            }
            tileDesign.getChildren().add(houseContainer);
        }
        if (tile != null) {
            Label nameLabel = new Label(tile.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 9px;");
            nameLabel.setWrapText(true);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            tileDesign.getChildren().add(nameLabel);
        }
        FlowPane tokenLayer = new FlowPane();
        tokenLayer.setAlignment(Pos.CENTER);
        tokenLayer.setPickOnBounds(false);
        for (Player p : players) {
            if (p.getCurrentPosition() == index) {
                tokenLayer.getChildren().add(createToken(p));
            }
        }
        container.getChildren().addAll(tileDesign, tokenLayer);
        return container;
    }

    /**
     * Creates a visual representation (Node) for a player's token.
     * It attempts to load an image based on the player's token type.
     * If loading fails, it creates a fallback graphical representation.
     *
     * @param p The player for whom to create the token.
     * @return A Node containing the visual representation of the token.
     */
    private Node createToken(Player p) {
        Image img = null;

        try {
            // --- new section try to load custom token ---
            if (p.getTokenType() == TokenType.CUSTOM) {
                String path = p.getCustomTokenPath();
                if (path != null && !path.isBlank()) {
                    img = new Image(path);
                }
            }

            // --- old section standard loading ---
            // if img is still null (because the token is CAR/DOG... or because the custom
            // failed)
            if (img == null || img.isError()) {
                // build the classic name: CAR.png, DOG.png...
                String imageName = p.getTokenType().toString().toUpperCase() + ".png";

                // search in the resources
                var stream = getClass().getResourceAsStream("/images/tokens/" + imageName);
                if (stream != null) {
                    img = new Image(stream);
                }
            }

            // if even the standard resources fail, throw an error to go to the
            // colored circle fallback
            if (img == null || img.isError()) {
                throw new Exception("Image not found for token: " + p.getTokenType());
            }

            // create the ImageView with the loaded image
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(35);
            imageView.setFitHeight(35);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // drop shadow effect
            DropShadow ds = new DropShadow();
            ds.setRadius(5.0);
            ds.setColor(Color.color(0, 0, 0, 0.4));
            imageView.setEffect(ds);

            return imageView;

        } catch (Exception e) {
            // --- extreme fallback (if even the PNG in the resources is missing) ---
            // draw the colored circle
            Color fallbackColor = (p.getTokenType() == TokenType.CUSTOM)
                    ? Color.PURPLE // purple for Custom
                    : Color.RED; // red for broken Standard

            Circle circle = new Circle(12);
            circle.setFill(fallbackColor);
            circle.setStroke(Color.BLACK);

            StackPane stack = new StackPane(circle, new Label(p.getName().substring(0, 1)));
            return stack;
        }
    }

    private int calculateX(int i) {
        if (i <= 10)
            return 10 - i;
        if (i <= 20)
            return 0;
        if (i <= 30)
            return i - 20;
        return 10;
    }

    private int calculateY(int i) {
        if (i <= 10)
            return 10;
        if (i <= 20)
            return 10 - (i - 10);
        if (i <= 30)
            return 0;
        return i - 30;
    }

    private void renderBoard() {
        this.root.getChildren().clear();
        this.root.getRowConstraints().clear();
        this.root.getColumnConstraints().clear();

        for (int i = 0; i < 11; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 11);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 11);

            this.root.getColumnConstraints().add(col);
            this.root.getRowConstraints().add(row);
        }

        final int size = board.size();

        for (int i = 0; i < size; i++) {
            final Tile tile = board.getTileAt(i);
            final StackPane tileUI = createTileUI(tile, i);

            // Espandi la VBox per riempire tutta la cella del GridPane
            tileUI.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            final int x = calculateX(i);
            final int y = calculateY(i);

            this.root.add(tileUI, x, y);
        }
    }

    /** @return the visual root of the board. */
    public Pane getRoot() {
        return this.root;
    }

    /** Updates the view based on current model state. */
    public void update() {
        renderBoard();
    }
}
