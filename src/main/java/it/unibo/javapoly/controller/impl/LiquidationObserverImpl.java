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
    private String playerName;
    private int currentDebt;
    private String currentCreditorName;

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
        this.playerName = playerNoFunds.getName();
        this.currentDebt = requiredAmount;
        this.currentCreditorName = (payee != null) ? payee.getName() : null;
        matchController.getMainView().addLog(
                playerNoFunds.getName() + " owes " + requiredAmount + CURRENCY + ". Sell your asset!!!");
        matchController.getMainView().showLiquidation();
        final SellAssetView view = matchController.getMainView().getSellAssetView();
        view.show(playerNoFunds, this.currentDebt);
        view.setCallBack(this::onLiquidationCompleted);
    }

    private void onLiquidationCompleted(final boolean success, final int remainingDebt) {
        matchController.getMainView().hideLiquidation();
                if (success && remainingDebt == 0) {
                    handleSuccessfulLiquidation();
                } else {
                    handleBankruptcy(remainingDebt);
                }
                this.playerName = null;
                this.currentDebt = 0;
                this.currentCreditorName = null;
                matchController.getMainView().refreshAll();
    }

    private void handleSuccessfulLiquidation() {
        final Player player = getPlayerByName(this.playerName);
        final Player creditor = getPlayerByName(this.currentCreditorName);
        if (player != null  && player.getBalance() >= this.currentDebt) {
            if (creditor != null) {
                matchController.getEconomyController()
                        .payPlayer(player, creditor, this.currentDebt);
                matchController.getMainView().addLog(
                        this.playerName + " pay debt to " + currentCreditorName + " of " + this.currentDebt + CURRENCY);
            } else {
                matchController.getEconomyController().withdrawFromPlayer(player, this.currentDebt);
                matchController.getMainView().addLog(this.playerName + " pay debt of " + this.currentDebt + CURRENCY);
            }
        }
    }

    private void handleBankruptcy(final int remainingDebt) {
        final Player player = getPlayerByName(this.playerName);
        final Player creditor = getPlayerByName(this.currentCreditorName);
        onBankruptcyDeclared(player, creditor, remainingDebt);
        matchController.getMainView().addLog(this.playerName + " is in bankrupt of " + remainingDebt + CURRENCY);
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

    /**
     * Helper method to retrieve a player by name.
     *
     * @param playerName the player's name.
     * @return the Player object, or null if not found.
     */
    private Player getPlayerByName(final String playerName) {
        if (playerName == null) {
            return null;
        }
        return matchController.getPlayers().stream()
                .filter(p->p.getName().equals(playerName))
                .findFirst()
                .orElse(null);
    }
}
