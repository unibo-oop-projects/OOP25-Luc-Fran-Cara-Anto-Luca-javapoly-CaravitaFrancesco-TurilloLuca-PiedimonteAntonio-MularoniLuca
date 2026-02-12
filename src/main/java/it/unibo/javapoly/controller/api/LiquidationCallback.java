package it.unibo.javapoly.controller.api;

/**
 * Thi interface need to exchange information from view to observer.
 */
public interface LiquidationCallback {

    /**
     * If liquidation complete it will be true, false otherwise.
     *
     * @param success how goes the liquidation.
     * @param remainingDebt debt to pay.
     */
    void onLiquidationCompleted(boolean success, int remainingDebt);
}
