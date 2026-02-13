package it.unibo.javapoly.controller.api;

/**
 * Invoked when the liquidation process for a player has concluded.
 */
@FunctionalInterface
public interface LiquidationCallback {

    /**
     * Invoked when the liquidation process for a player has concluded.
     *
     * @param success {@code true} if the player's debt was fully satisfied, {@code false}
     *                 {@code false} if the partial or no liquidation succeeded and remains.
     * @param remainingDebt the absolute amount of debt still owed.
     */
    void onLiquidationCompleted(boolean success, int remainingDebt);
}
