package it.unibo.javapoly.view.impl;

import java.util.List;
import java.util.Objects;

import it.unibo.javapoly.model.api.Player;
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
    private List<Player> players;

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

    private Node createToken(Player p) {
        // 1. CARICAMENTO IMMAGINE
        try {
            String imageName = p.getToken().getType().toString().toUpperCase() + ".png";
            Image img = new Image(getClass().getResourceAsStream("/images/tokens/" + imageName));
            ImageView imageView = new ImageView(img);

            // 2. DIMENSIONI
            imageView.setFitWidth(35); // Regola in base alla grandezza delle tue caselle
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // 3. EFFETTO OMBRA
            DropShadow ds = new DropShadow();
            ds.setRadius(5.0);
            ds.setOffsetX(3.0);
            ds.setOffsetY(3.0);
            ds.setColor(Color.color(0, 0, 0, 0.4)); // Ombra semi-trasparente

            imageView.setEffect(ds);

            return imageView;

        } catch (Exception e) {
            Circle circle = new Circle(10);
            circle.setFill(Color.RED);
            circle.setStroke(Color.BLACK);
            return circle;
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
