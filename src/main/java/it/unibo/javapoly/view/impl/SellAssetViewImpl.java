package it.unibo.javapoly.view.impl;

import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.view.api.SellAssetView;
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

    private final BorderPane root;
    private final GridPane list;
    private final Label title = new Label("Amount to pay off:");
    private final Label debt = new Label();

    private final MatchController matchController;
    private Player currentPlayer;

    /**
     * Constructor a new sell-asset view.
     *
     * @param controller the match controller, must not be {@code null}.
     * @throws IllegalArgumentException if {@code controller} is null.
     */
    public SellAssetViewImpl(final MatchController controller) {
        this.root = new BorderPane();
        this.list = new GridPane();
        this.matchController = Objects.requireNonNull(controller, "Match controller must not be null");
        initialize();
    }

    /**
     * Initialize the UI layout structure.
     */
    public void initialize() {
        final HBox titleHBox = new HBox();
        titleHBox.getChildren().addAll(title, debt);
        this.root.setTop(titleHBox);
        this.root.setCenter(this.list);
    }

    /**
     * Display the view for the given player with their current debit and available properties.
     * Updates the UI with button to sell house first(if any), otherwise regular properties.
     *
     * @param player the player in turn, must not be {@code null}
     * @param debtAmount the amount of debt to be settled (must be >0 for UI to populate buttons)
     * @throws IllegalArgumentException if {@code player} is null or any list is null.
     */
    public void show(final Player player, final int debtAmount) {
        this.currentPlayer = player;
        final List<Property> properties =
                new ArrayList<>(this.matchController.getPropertyController().getOwnedProperties(player.getName()));
        final List<Property> houses =
                new ArrayList<>(this.matchController.getPropertyController().getPropertiesWithHouseByOwner(player));
        this.debt.setText(debtAmount + " $");
        this.list.getChildren().clear();
        if (debtAmount <= 0) {
            return;
        }
        if (properties.isEmpty() && houses.isEmpty()) {
            player.setState(BankruptState.getInstance());
        }
        int row = 0;
        int col = 0;
        if (!houses.isEmpty()) {
            for (final Property propertyWithHouse: houses) {
                final Button houseButton = createHouseButton(propertyWithHouse, debtAmount);
                list.add(houseButton, col++, row);
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
            return;
        }
        if (!properties.isEmpty()) {
            for (final Property propertyWithoutHouse: properties) {
                final Button propertyButton = createPropertyButton(propertyWithoutHouse, debtAmount);
                list.add(propertyButton, col++, row);
                if (col >= 3) {
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
     * @param debtAmount the actual debt amount.
     * @return a non-null button.
     */
    private Button createHouseButton(final Property property, final int debtAmount) {
        final Label name = new Label(property.getCard().getName());
        final Label houseCount = new Label("House " + property.getState().getHouses());
        final int housePrice = this.matchController.getPropertyController().getHouseCost(property) / 2;
        final Label price = new Label("Sell for " + housePrice + " $");
        final HBox content = new HBox(name, houseCount, price);
        final Button houseButton = new Button();
        houseButton.setGraphic(content);
        houseButton.setOnAction(e -> {
            this.matchController.getEconomyController().sellHouse(this.currentPlayer, property);
            final int updateDebt = debtAmount - housePrice;
            debt.setText(updateDebt + "$");
            show(currentPlayer, debtAmount);
        });
        return houseButton;
    }

    /**
     * Creates a button to sell a property.
     * Value returned is half of the property purchase cost.
     *
     * @param property the property, must not be null;
     * @param debtAmount the actual debt amount.
     * @return a non-null button
     */
    private Button createPropertyButton(final Property property, final int debtAmount) {
        final Label name = new Label(property.getCard().getName());
        final int propertySellHouse = property.getState().getPurchasePrice() / 2;
        final Label price = new Label(String.valueOf(propertySellHouse));
        final HBox content = new HBox(name, price);
        final Button propertyButton = new Button();
        propertyButton.setGraphic(content);
        propertyButton.setOnAction(e -> {
            this.matchController.getEconomyController().sellProperty(this.currentPlayer, property);
            final int updateDebt = debtAmount - propertySellHouse;
            debt.setText(updateDebt + "$");
            show(currentPlayer, debtAmount);
        });
        return propertyButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BorderPane getRoot() {
        return root;
    }
}
