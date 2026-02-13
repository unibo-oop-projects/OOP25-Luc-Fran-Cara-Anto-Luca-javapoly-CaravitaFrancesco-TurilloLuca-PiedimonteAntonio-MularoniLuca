package it.unibo.javapoly.view.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Locale;
import java.util.List;
import java.util.Objects;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.model.impl.card.LandPropertyCard;
import it.unibo.javapoly.model.impl.card.StationPropertyCard;
import it.unibo.javapoly.view.api.BoardPanel;
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
public final class BoardPanelImpl implements BoardPanel {

    private static final int GRID_SIZE = 11;
    private static final double CELL_PERCENT = 100.0 / GRID_SIZE;
    private static final int TILE_BAR_HEIGHT = 15;
    private static final int TOKEN_SIZE = 35;
    private static final double SHADOW_RADIUS = 5.0;
    private static final double SHADOW_OPACITY = 0.4;
    private static final int FALLBACK_CIRCLE_RADIUS = 12;
    private static final int POSITION_THRESHOLD_10 = 10;
    private static final int POSITION_THRESHOLD_20 = 20;
    private static final int POSITION_THRESHOLD_30 = 30;

    private final GridPane root;
    private final Board board;
    private final List<Player> players;

    /**
     * Constructor for BoardPanel.
     * 
     * @param board the game board.
     * @param players the list of players.
     */
    public BoardPanelImpl(final Board board, final List<Player> players) {
        this.board = Objects.requireNonNull(board);
        this.players = List.copyOf(Objects.requireNonNull(players));
        this.root = new GridPane();
        this.root.setStyle("-fx-background-color: #CDE6D0; -fx-padding: 5; -fx-border-color: black;");
        this.root.setAlignment(Pos.CENTER);
        this.renderBoard();
    }

    private String getColorForOwner(final String ownerId) {
        final String[] colors = {"#e74c3c", "#3498db", "#f1c40f", "#9b59b6", "#e67e22"};
        final int hash = ownerId.hashCode() & Integer.MAX_VALUE;
        return colors[hash % colors.length];
    }

    private StackPane createTileUI(final Tile tile, final int index) {
        final StackPane container = new StackPane();
        final VBox tileDesign = new VBox();
        tileDesign.setStyle("-fx-border-color: black; -fx-background-color: white;");
        tileDesign.setAlignment(Pos.TOP_CENTER);

        if (tile instanceof PropertyTile pt) {
            final Property prop = pt.getProperty();
            String groupColor = "grey";
            if (pt.getProperty().getCard() instanceof LandPropertyCard lpc) {
                groupColor = lpc.getGroup().toString().toLowerCase(Locale.ROOT);
            } else if (pt.getProperty().getCard() instanceof StationPropertyCard) {
                groupColor = "black";
            }

            final Pane groupBar = new Pane();
            groupBar.setPrefHeight(TILE_BAR_HEIGHT);
            groupBar.setStyle("-fx-background-color: " + groupColor + "; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
            tileDesign.getChildren().add(groupBar);

            if (prop.getIdOwner() != null) {
                tileDesign.setStyle("-fx-border-color: " + getColorForOwner(prop.getIdOwner()) 
                + "; -fx-border-width: 3; -fx-background-color: white;");
            }

            final HBox houseContainer = new HBox(2);
            houseContainer.setAlignment(Pos.CENTER);
            houseContainer.setPrefHeight(TILE_BAR_HEIGHT);

            final int houseCount = prop.getBuiltHouses();
            for (int i = 0; i < houseCount; i++) {
                final Circle house = new Circle(4, Color.GREEN);
                houseContainer.getChildren().add(house);
            }
            tileDesign.getChildren().add(houseContainer);
        }
        if (tile != null) {
            final Label nameLabel = new Label(tile.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 9px;");
            nameLabel.setWrapText(true);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            tileDesign.getChildren().add(nameLabel);
        }

        final FlowPane tokenLayer = new FlowPane();
        tokenLayer.setAlignment(Pos.CENTER);
        tokenLayer.setPickOnBounds(false);
        for (final Player p : players) {
            if (p.getCurrentPosition() == index) {
                tokenLayer.getChildren().add(createToken(p));
            }
        }
        container.getChildren().addAll(tileDesign, tokenLayer);
        return container;
    }

    private Node createToken(final Player p) {
        Image img = null;

        if (p.getTokenType() == TokenType.CUSTOM) {
            final String path = p.getCustomTokenPath();
            if (path != null && !path.isBlank()) {
                img = new Image(path);
            }
        }

        if (img == null || img.isError()) {
            final String imageName = p.getTokenType().toString().toUpperCase(Locale.ROOT) + ".png";
            final var stream = getClass().getResourceAsStream("/images/tokens/" + imageName);
            if (stream != null) {
                img = new Image(stream);
            }
        }

        if (img == null || img.isError()) {
            final ImageView imageView = new ImageView(img);
            imageView.setFitWidth(TOKEN_SIZE);
            imageView.setFitHeight(TOKEN_SIZE);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            final DropShadow ds = new DropShadow();
            ds.setRadius(SHADOW_RADIUS);
            ds.setColor(Color.color(0, 0, 0, SHADOW_OPACITY));
            imageView.setEffect(ds);
            return imageView;
        } else {
            final Color fallbackColor = (p.getTokenType() == TokenType.CUSTOM) ? Color.PURPLE : Color.RED; 
            final Circle circle = new Circle(FALLBACK_CIRCLE_RADIUS);
            circle.setFill(fallbackColor);
            circle.setStroke(Color.BLACK);
            final Label initial = new Label(p.getName().substring(0, 1));
            return new StackPane(circle, initial);
        }
    }

    private int calculateX(final int i) {
        if (i <= POSITION_THRESHOLD_10) {
            return POSITION_THRESHOLD_10 - i;
        }
        if (i <= POSITION_THRESHOLD_20) {
            return 0;
        }
        if (i <= POSITION_THRESHOLD_30) {
            return i - POSITION_THRESHOLD_20;
        }
        return POSITION_THRESHOLD_10;
    }

    private int calculateY(final int i) {
        if (i <= POSITION_THRESHOLD_10) {
            return POSITION_THRESHOLD_10;
        }
        if (i <= POSITION_THRESHOLD_20) {
            return POSITION_THRESHOLD_10 - (i - POSITION_THRESHOLD_10);
        }
        if (i <= POSITION_THRESHOLD_30) {
            return 0;
        }
        return i - POSITION_THRESHOLD_30;
    }

    private void renderBoard() {
        this.root.getChildren().clear();
        this.root.getRowConstraints().clear();
        this.root.getColumnConstraints().clear();

        for (int i = 0; i < GRID_SIZE; i++) {
            final ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(CELL_PERCENT);
            final RowConstraints row = new RowConstraints();
            row.setPercentHeight(CELL_PERCENT);
            this.root.getColumnConstraints().add(col);
            this.root.getRowConstraints().add(row);
        }

        final int size = board.size();
        for (int i = 0; i < size; i++) {
            final Tile tile = board.getTileAt(i);
            final StackPane tileUI = createTileUI(tile, i);
            tileUI.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            this.root.add(tileUI, calculateX(i), calculateY(i));
        }
    }

    /**
     * Returns the visual root of the board.
     *
     * @return the visual root of the board.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "JavaFX nodes must be exposed to be added to the scene graph"
    )
    @Override
    public Pane getRoot() {
        return this.root;
    }

    /**
     * Updates the view based on current model state.
     */
    @Override
    public void update() {
        renderBoard();
    }
}
