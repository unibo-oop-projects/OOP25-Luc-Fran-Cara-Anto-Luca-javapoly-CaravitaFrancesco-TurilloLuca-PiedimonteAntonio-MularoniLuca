package it.unibo.javapoly.view.impl;

import java.util.Objects;

import it.unibo.javapoly.controller.api.LiquidationCallback;
import it.unibo.javapoly.view.api.InfoPanel;
import it.unibo.javapoly.view.api.SellAssetView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;

/**
 * InfoPanel displays information about the current player,
 * such as name, balance, and board position.
 */
public final class InfoPanelImpl implements InfoPanel {

    private static final int SPACING = 15;
    private static final int PADDING = 20;
    private static final int PREF_WIDTH = 280;
    private static final int CARD_SPACING = 5;
    private static final int CARD_PADDING = 12;
    private static final int ICON_SIZE = 40;
    private static final int TITLE_FONT_SIZE = 16;
    private static final int NAME_FONT_SIZE = 14;
    private static final int BALANCE_FONT_SIZE = 13;
    private static final int POSITION_FONT_SIZE = 11;
    private static final int HEADER_SPACING = 10;
    private static final String FONT_FAMILY = "Segoe UI";

    private final VBox root;
    private final MatchController matchController;
    private final SellAssetView sellAssetView;
    private final VBox liquidation;

    /**
     * Constructor: creates labels and adds them to the panel.
     *
     * @param matchController reference to the game controller
     */
    public InfoPanelImpl(final MatchController matchController) {
        this.matchController = Objects.requireNonNull(matchController);

        this.root = new VBox(SPACING);
        this.root.setPadding(new Insets(PADDING));
        this.root.setPrefWidth(PREF_WIDTH); 
        this.root.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #CCCCCC; -fx-border-width: 0 0 0 1;");
        this.sellAssetView = new SellAssetViewImpl(this.matchController);
        this.liquidation = new VBox();
        this.liquidation.getChildren().add(this.sellAssetView.getRoot());
        this.sellAssetView.getRoot().setVisible(false);
        this.sellAssetView.getRoot().setManaged(false);
        this.root.getChildren().add(this.liquidation);
        this.updateInfo();
    }

    /**
     * Updates the labels to show current player's info.
     */
    public void updateInfo() {
        this.root.getChildren().clear();
        final Label title = new Label("PLAYERS");
        title.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, TITLE_FONT_SIZE));
        title.setPadding(new Insets(0, 0, HEADER_SPACING, 0));
        this.root.getChildren().add(title);
        for (final Player p : this.matchController.getPlayers()) {
            this.root.getChildren().add(createPlayerCard(p));
        }

        this.root.getChildren().add(this.liquidation);
    }

    private VBox createPlayerCard(final Player p) {
        final VBox card = new VBox(CARD_SPACING);
        card.setPadding(new Insets(CARD_PADDING));
        card.setAlignment(Pos.CENTER_LEFT);

        final ImageView icon = new ImageView();
        try {
            final String fileName = p.getToken().getType().toString().toLowerCase();
            final var stream = getClass().getResourceAsStream("/images/tokens/" + fileName + ".png");
            if (stream == null) {
                throw new IllegalArgumentException("Resource not found");
            }
            final Image tokenImg = new Image(stream);
            icon.setImage(tokenImg);
        } catch (final IllegalArgumentException e) {
            System.err.println("Image not found for: " + p.getName());
        }

        icon.setFitWidth(ICON_SIZE);
        icon.setFitHeight(ICON_SIZE);
        icon.setPreserveRatio(true);

        final Label name = new Label(p.getName());
        name.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, NAME_FONT_SIZE));

        final Label balance = new Label("Balance: " + p.getBalance() + "€");
        balance.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, BALANCE_FONT_SIZE));

        final Label position = new Label("Position: " + p.getCurrentPosition());
        position.setFont(Font.font(FONT_FAMILY, FontPosture.ITALIC, POSITION_FONT_SIZE));

        final HBox header = new HBox(HEADER_SPACING);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(icon, name);

        card.getChildren().addAll(header, balance, position);

        String style = "-fx-background-radius: 10; -fx-background-color: white; " 
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);";

        if (p.equals(this.matchController.getCurrentPlayer())) {
            style += "-fx-border-color: #4CAF50; -fx-border-width: 2.5; -fx-background-color: #F1F8E9;";
            name.setText("▶ " + p.getName()); 
        } else {
            style += "-fx-border-color: #D3D3D3; -fx-border-width: 1;";
        }

        card.setStyle(style);
        return card;
    }

    /**
     * Returns the root node of this panel to be added to a Scene.
     *
     * @return the {@link VBox} containing the labels.
     */
    public VBox getRoot() {
        return this.root;
    }

    /**
     * Show the sell asset view for liquidation.
     */
    public void showSellAssetView() {
        this.sellAssetView.getRoot().setVisible(true);
        this.sellAssetView.getRoot().setManaged(true);
    }

    /**
     * Hide the sell asset view for liquidation.
     */
    public void hideSellAssetView() {
        this.sellAssetView.getRoot().setVisible(false);
        this.sellAssetView.getRoot().setManaged(false);
    }

    /**
     * Shows the sell asset view with player and debt information.
     *
     * @param player the player who needs to liquidate assets.
     * @param debtAmount the amount of debt to pay off.
     */
    public void showLiquidation(final Player player, final int debtAmount) {
        this.sellAssetView.show(player, debtAmount);
        showSellAssetView();
    }

    /**
     * Sets the callback to invoke when liquidation completes.
     *
     * @param callback the callback to invoke when liquidation completes.
     */
    public void setLiquidationCallback(final LiquidationCallback callback) {
        this.sellAssetView.setCallBack(callback);
    }
}
