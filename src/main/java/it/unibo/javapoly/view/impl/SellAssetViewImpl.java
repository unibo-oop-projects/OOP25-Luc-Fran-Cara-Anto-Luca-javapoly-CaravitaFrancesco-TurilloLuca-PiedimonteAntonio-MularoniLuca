package it.unibo.javapoly.view.impl;

import it.unibo.javapoly.controller.api.LiquidationCallback;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.view.api.SellAssetView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link SellAssetView}, providing a UI for player to sell property/houses
 * in order to satisfy debt obligation.
 */
public class SellAssetViewImpl implements SellAssetView {
    public static final String CURRENCY = "€";
    private final BorderPane root;
    private final GridPane propertyGrid;
    private final Label titleLabel;
    private final Label debtLabel;
    private final MatchController matchController;
    private String playerName;
    private int originalDebt;
    private int remainingDebt;
    private LiquidationCallback liquidationCallback;

    /**
     * Constructor a new sell-asset view.
     *
     * @param controller the match controller, must not be {@code null}.
     * @throws IllegalArgumentException if {@code controller} is null.
     */
    public SellAssetViewImpl(final MatchController controller) {
        this.root = new BorderPane();
        this.propertyGrid = new GridPane();
        this.titleLabel = new Label("Amount to pay off: ");
        this.debtLabel = new Label();
        this.matchController = Objects.requireNonNull(controller, "Match controller must not be null");
        initialize();
    }

    /**
     * Initialize the UI layout structure.
     */
    private void initialize() {
        final HBox header = new HBox(titleLabel, debtLabel);
        header.setAlignment(Pos.CENTER);

        this.root.setTop(header);
        this.root.setCenter(this.propertyGrid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(final Player player, final int debtAmount) {
        this.playerName = player.getName();
        this.originalDebt = debtAmount;
        this.remainingDebt = debtAmount;
        debtDisplay();
        refreshPropertyGrid();
    }

    /**
     * This method update debtLabel with remaining debt.
     */
    private void debtDisplay() {
        this.debtLabel.setText(this.remainingDebt + CURRENCY);
    }

    /**
     * This method update propertyGrid with buttons.
     */
    private void refreshPropertyGrid() {
        this.propertyGrid.getChildren().clear();
        final Player currentPlayer = this.matchController.getCurrentPlayer();
        this.remainingDebt = Math.max(0, this.originalDebt - currentPlayer.getBalance());
        if (this.remainingDebt == 0) {
            completeLiquidation(true);
            return;
        }
        final List<Property> properties =
                new ArrayList<>(this.matchController.getPropertyController().getOwnedProperties(this.playerName));
        final List<Property> houses =
                new ArrayList<>(this.matchController.getPropertyController().getPropertiesWithHouseByOwner(currentPlayer));
        if (properties.isEmpty() && houses.isEmpty()) {
            currentPlayer.setState(BankruptState.getInstance());
            declareBankruptcy();
            return;
        }
        int row = 0;
        int col = 0;
        if (!houses.isEmpty()) {
            for (final Property propertyWithHouse: houses) {
                final Button houseButton = createHouseButton(propertyWithHouse);
                propertyGrid.add(houseButton, col, row);
                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }
        } else {
            for (final Property propertyWithoutHouse: properties) {
                final Button propertyButton = createPropertyButton(propertyWithoutHouse);
                propertyGrid.add(propertyButton, col, row);
                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    /**
     * Creates a button to sell a property with a house.
     * Value returned is half of the house construction cost.
     *
     * @param property the property, must not be null.
     * @return a non-null button.
     */
    private Button createHouseButton(final Property property) {
        final Label nameLabel = new Label(property.getCard().getName() + " ");
        final Label houseCountLabel = new Label("House " + property.getState().getHouses() + " ");
        final int housePriceLabel = this.matchController.getPropertyController().getHouseCost(property) / 2;
        final Label price = new Label("Sell for " + housePriceLabel + CURRENCY);
        final HBox content = new HBox(nameLabel, houseCountLabel, price);
        final Button houseButton = new Button();
        houseButton.setGraphic(content);
        houseButton.setOnAction(e -> sellHouse(property, housePriceLabel));
        return houseButton;
    }

    /**
     * Creates a button to sell a property.
     * Value returned is half of the property purchase cost.
     *
     * @param property the property, must not be null;
     * @return a non-null button.
     */
    private Button createPropertyButton(final Property property) {
        final Label name = new Label(property.getCard().getName() + " ");
        final int pricePropertyToSell = property.getState().getPurchasePrice() / 2;
        final Label price = new Label(String.valueOf(pricePropertyToSell));
        final HBox content = new HBox(name, price);
        final Button propertyButton = new Button();
        propertyButton.setGraphic(content);
        propertyButton.setOnAction(e -> sellProperty(property, pricePropertyToSell));
        return propertyButton;
    }

    /**
     * Sells a house and update debt.
     *
     * @param property the property with house.
     * @param housePrice the sale price.
     */
    private void sellHouse(final Property property, final int housePrice) {
        final Player currentPlayer = this.matchController.getCurrentPlayer();
        final boolean success = matchController.getEconomyController().sellHouse(currentPlayer, property);
        if (!success) {
           return;
        }
        matchController.getMainViewImpl().addLog(
                currentPlayer.getName() + " sold house in " + property.getId() + " for " + housePrice + CURRENCY);
        if (currentPlayer.getBalance() >= this.originalDebt) {
            this.remainingDebt = 0;
            debtDisplay();
            completeLiquidation(true);
            return;
        }
        refreshPropertyGrid();
    }

    /**
     * Sells a property and updates the debt.
     *
     * @param property the property.
     * @param pricePropertyToSell the sale price.
     */
    private void sellProperty(final Property property, final int pricePropertyToSell) {
        final Player currentPlayer = this.matchController.getCurrentPlayer();
        final boolean success = matchController.getEconomyController().sellProperty(currentPlayer, property);
        if (!success) {
            return;
        }
        matchController.getMainViewImpl().addLog(
                    currentPlayer.getName() + " sold " + property.getId() + " for " + pricePropertyToSell + CURRENCY);
        if (currentPlayer.getBalance() >= this.originalDebt) {
            this.remainingDebt = 0;
            debtDisplay();
            completeLiquidation(true);
            return;
        }
        refreshPropertyGrid();
    }

    /**
     * Declares bankruptcy.
     */
    private void declareBankruptcy() {
        final Player currentPlayer = this.matchController.getCurrentPlayer();
        currentPlayer.setState(BankruptState.getInstance());
        completeLiquidation(false);
    }

    /**
     * Completes the liquidation process and notifies callback.
     *
     * @param success true if debt was full paid.
     */
    private void completeLiquidation(final boolean success) {
        if (this.liquidationCallback != null) {
            this.liquidationCallback.onLiquidationCompleted(success, this.remainingDebt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BorderPane getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCallBack(final LiquidationCallback callback) {
        this.liquidationCallback = callback;
    }
}
