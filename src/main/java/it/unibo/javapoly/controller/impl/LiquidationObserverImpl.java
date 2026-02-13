package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.utils.ValidationUtils;
import it.unibo.javapoly.view.api.SellAssetView;

import static it.unibo.javapoly.view.impl.SellAssetViewImpl.CURRENCY;

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
    public void onInsufficientFunds(final Player playerNoFunds, final Player payee, final int requiredAmount) {
        ValidationUtils.requireNonNull(playerNoFunds, "player cannot be null");
        ValidationUtils.requirePositive(requiredAmount, "requiredAmount must be positive");
        this.player = playerNoFunds;
        this.currentDebt = requiredAmount;
        this.currentCreditor = payee;
        matchController.getMainView().addLog(
                playerNoFunds.getName() + " owes " + requiredAmount + CURRENCY + ". Sell your asset!!!");
        matchController.getMainView().showLiquidation();
        final SellAssetView view = matchController.getMainView().getSellAssetView();
        view.show(this.player, this.currentDebt);
        view.setCallBack(this::onLiquidationCompleted);
    }

    private void onLiquidationCompleted(final boolean success, final int remainingDebt) {
        matchController.getMainView().hideLiquidation();
                if (success && remainingDebt == 0) {
                    handleSuccessfulLiquidation();
                } else {
                    handleBankruptcy(remainingDebt);
                }
                this.player = null;
                this.currentDebt = 0;
                this.currentCreditor = null;
                matchController.getMainView().refreshAll();
    }

    private void handleSuccessfulLiquidation() {
        if (this.player.getBalance() >= this.currentDebt) {
            if (this.currentCreditor != null) {
                matchController.getEconomyController()
                        .payPlayer(this.player, this.currentCreditor, this.currentDebt);
                matchController.getMainView().addLog(
                        this.player.getName() + " pay debt to " + this.currentCreditor.getName() + " of " + this.currentDebt + CURRENCY);
            } else {
                matchController.getEconomyController().withdrawFromPlayer(this.player, this.currentDebt);
                matchController.getMainView().addLog(this.player.getName() + " pay debt of " + this.currentDebt + CURRENCY);
            }
        }
    }

    private void handleBankruptcy(final int remainingDebt) {
        onBankruptcyDeclared(this.player, this.currentCreditor, remainingDebt);
        matchController.getMainView().addLog(this.player.getName() + " is in bankrupt of " + remainingDebt + CURRENCY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBankruptcyDeclared(final Player payer, final Player payee, final int requiredAmount) {
        ValidationUtils.requireNonNull(payer, "payer cannot be null");
        if (payee != null && payer.getBalance() > 0) {
            matchController.getEconomyController().payPlayer(payer, payee, payer.getBalance());
            matchController.getMainView().addLog(
                    payer.getName() + " gives remaining " + payer.getBalance() + CURRENCY + " to " + payee.getName());
        } else if (payer.getBalance() > 0) {
            matchController.getEconomyController().withdrawFromPlayer(payer, payer.getBalance());
        }
        payer.setState(BankruptState.getInstance());
        matchController.getMainView().refreshAll();
    }

}
