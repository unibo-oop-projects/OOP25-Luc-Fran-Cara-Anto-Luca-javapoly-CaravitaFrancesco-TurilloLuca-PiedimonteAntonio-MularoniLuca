package it.unibo.javapoly.view.api;

import it.unibo.javapoly.controller.api.LiquidationCallback;
import it.unibo.javapoly.model.api.Player;
import javafx.scene.layout.VBox;

/**
 * Interface representing the panel that displays player statistics and 
 * handles asset liquidation views.
 */
public interface InfoPanel {

    /**
     * Updates the information displayed in the panel to reflect 
     * the current state of all players.
     */
    void updateInfo();

    /**
     * Returns the visual root node of this panel.
     *
     * @return the VBox containing the player information and liquidation area.
     */
    VBox getRoot();

    /**
     * Sets the visibility of the asset liquidation view to true.
     */
    void showSellAssetView();

    /**
     * Sets the visibility of the asset liquidation view to false.
     */
    void hideSellAssetView();

    /**
     * Prepares and displays the liquidation view for a specific player in debt.
     *
     * @param player     the player who needs to liquidate assets.
     * @param debtAmount the amount of debt to be settled.
     */
    void showLiquidation(Player player, int debtAmount);

    /**
     * Sets the callback to be executed once the liquidation process is finished.
     *
     * @param callback the liquidation callback.
     */
    void setLiquidationCallback(LiquidationCallback callback);
}
