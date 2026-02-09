package it.unibo.javapoly.controller.api;

import it.unibo.javapoly.model.api.Player;

/**
 * Observer liquidation-related events.
 * Used to trigger UI flows like asset-selling dialogs.
 */
public interface LiquidationObserver {

    /**
     * Notifies that the specified player lacks sufficient funds to cover an immediate financial obligation.
     * This event is triggered before bankruptcy is declared.
     *
     * @param player the player who cannot fulfill the payment due to insufficient balance.
     * @param requiredAmount the amount needed now.
     */
    void onInsufficientFunds(Player player, int requiredAmount);

    /**
     * Notifies that a player has entered formal bankruptcy state.
     *
     * @param bankruptPlayer the player who is declared bankrupt. Must not be {@code null}.
     * @param creditor the player owed the unpaid debt.
     * @param totalDebt the total unpaid amount that triggered the bankruptcy. Must be {@code >= 0}.
     */
    void onBankruptcyDeclared(Player bankruptPlayer, Player creditor, int totalDebt);
}
