package it.unibo.javapoly.view.api;

import it.unibo.javapoly.controller.api.LiquidationCallback;
import it.unibo.javapoly.model.api.Player;
import javafx.scene.layout.BorderPane;

/**
 * View for displaying assets that a player may sell to settle outstanding debts.
 * Provides the UI root for selecting witch assets to liquidate
 */
public interface SellAssetView {
    /**
     * Retrieves the root layout container for this view.
     *
     * @return the {@link BorderPane} serving as the UI root for this view.
     */
    BorderPane getRoot();

    /**
     * Register a callback.
     *
     * @param callback callback.
     */
    void setCallBack(LiquidationCallback callback);

    /**
     * Display the view for the given player with their current debit and available properties.
     * Updates the UI with button to sell house first(if any), otherwise regular properties.
     *
     * @param player the player in turn, must not be {@code null}
     * @param debtAmount the amount of debt to be settled (must be >0 for UI to populate buttons)
     * @throws IllegalArgumentException if {@code player} is null or any list is null.
     */
    void show(Player player, int debtAmount);
}
