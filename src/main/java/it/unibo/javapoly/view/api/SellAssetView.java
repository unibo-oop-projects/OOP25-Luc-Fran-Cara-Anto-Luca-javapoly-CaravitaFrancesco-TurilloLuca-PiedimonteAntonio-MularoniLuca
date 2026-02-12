package it.unibo.javapoly.view.api;

import it.unibo.javapoly.controller.api.LiquidationCallback;
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
}
