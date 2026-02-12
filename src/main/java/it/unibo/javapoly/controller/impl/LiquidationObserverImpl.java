package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.utils.ValidationUtils;
import it.unibo.javapoly.view.api.SellAssetView;

/**
 * Implementation of LiquidationObserver.
 */
public class LiquidationObserverImpl implements LiquidationObserver {
    private final MatchController matchController;
    private Player player;
    private int currentDebt;
    private Player currentCreditor;

    /**
     * Creates a new liquidation observer.
     *
     * @param matchController the match controller, must not be null.
     */
    public LiquidationObserverImpl(final MatchController matchController) {
        this.matchController = ValidationUtils.requireNonNull(matchController, "matchController is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInsufficientFunds(final Player playerNoFunds, final int requiredAmount) {
        ValidationUtils.requireNonNull(playerNoFunds, "player cannot be null");
        ValidationUtils.requirePositive(requiredAmount, "requiredAmount must be positive");
        this.currentCreditor = playerNoFunds;
        this.currentDebt = requiredAmount;
        this.currentCreditor = null;
        matchController.getMainView().addLog(playerNoFunds.getName() + "owns " + requiredAmount + "$. Sell your asset!!!");
        matchController.getMainView().showLiquidation(playerNoFunds, requiredAmount);
        final SellAssetView view = matchController.getMainView().getSellAssetView();
        view.setCallBack((success, remainingDebt) -> {
            onLiquidationCompleted(success, remainingDebt);
        });
    }

    private void onLiquidationCompleted(final boolean success, final int remainingDebt) {
        matchController.getMainView().getSellAssetView().getRoot().setVisible(false);
                if (success) {
                    handleSuccessfulLiquidation();
                }
                handleBankruptcy(remainingDebt);
                this.player = null;
                this.currentDebt = 0;
                this.currentCreditor = null;
                matchController.getMainView().refreshAll();
    }

    private void handleSuccessfulLiquidation() {
        if (this.player.getBalance() >= this.currentDebt) {
            final boolean paymentSuccess =
                    matchController.getEconomyController().withdrawFromPlayer(currentCreditor, currentDebt);
            if (paymentSuccess) {
                matchController.getMainView().addLog(this.player.getName() + "sold is debit of " + this.currentDebt + "$");
            }
        } else {
            handleBankruptcy(currentDebt - player.getBalance());
        }
    }

    private void handleBankruptcy(final int remainingDebt) {
        onBankruptcyDeclared(this.player, this.currentCreditor, remainingDebt);
        matchController.getMainView().addLog(this.player.getName() + "is in Bankrupt of " + this.currentDebt + "$");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBankruptcyDeclared(final Player payer, final Player payee, final int requiredAmount) {
        ValidationUtils.requireNonNull(payer, "payer cannot be null");
        payer.setState(BankruptState.getInstance());
        if (payee != null && payer.getBalance() > 0) {
            matchController.getEconomyController().payPlayer(payer, payee, payer.getBalance());
            matchController.getMainView().addLog(
                    player.getName() + "give " + payer.getBalance() + " to" + payee.getName());
        }
        matchController.getMainView().refreshAll();
    }

}
