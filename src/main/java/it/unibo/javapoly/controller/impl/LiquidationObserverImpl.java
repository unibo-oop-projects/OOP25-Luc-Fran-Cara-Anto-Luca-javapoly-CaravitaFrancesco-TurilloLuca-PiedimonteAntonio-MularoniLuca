package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.utils.ValidationUtils;

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
        matchController.getMainViewImpl().addLog(
                playerNoFunds.getName() + " owes " + requiredAmount + CURRENCY + ". Sell your asset!!!");
        matchController.getMainViewImpl().showLiquidation();
        matchController.getMainViewImpl().getInfoPanel().showLiquidation(playerNoFunds, this.currentDebt);
        matchController.getMainViewImpl().getInfoPanel().setLiquidationCallback(this::onLiquidationCompleted);
    }

    private void onLiquidationCompleted(final boolean success, final int remainingDebt) {
        matchController.getMainViewImpl().hideLiquidation();
                if (success && remainingDebt == 0) {
                    handleSuccessfulLiquidation();
                } else {
                    handleBankruptcy(remainingDebt);
                }
                this.playerName = null;
                this.currentDebt = 0;
                this.currentCreditorName = null;
                matchController.getMainViewImpl().refreshAll();
    }

    private void handleSuccessfulLiquidation() {
        final Player player = getPlayerByName(this.playerName);
        final Player creditor = getPlayerByName(this.currentCreditorName);
        if (player != null && player.getBalance() >= this.currentDebt) {
            if (creditor != null) {
                matchController.getEconomyController()
                        .payPlayer(player, creditor, this.currentDebt);
                matchController.getMainViewImpl().addLog(
                        this.playerName + " pay debt to " + currentCreditorName + " of " + this.currentDebt + CURRENCY);
            } else {
                matchController.getEconomyController().withdrawFromPlayer(player, this.currentDebt);
                matchController.getMainViewImpl().addLog(this.playerName + " pay debt of " + this.currentDebt + CURRENCY);
            }
        }
    }

    private void handleBankruptcy(final int remainingDebt) {
        final Player player = getPlayerByName(this.playerName);
        final Player creditor = getPlayerByName(this.currentCreditorName);
        if (player != null) {
            onBankruptcyDeclared(player, creditor, remainingDebt);
            matchController.getMainViewImpl().addLog(this.playerName + " is in bankrupt of " + remainingDebt + CURRENCY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBankruptcyDeclared(final Player payer, final Player payee, final int requiredAmount) {
        ValidationUtils.requireNonNull(payer, "payer cannot be null");
        if (payee != null && payer.getBalance() > 0) {
            matchController.getEconomyController().payPlayer(payer, payee, payer.getBalance());
            matchController.getMainViewImpl().addLog(
                    payer.getName() + " gives remaining " + payer.getBalance() + CURRENCY + " to " + payee.getName());
        } else if (payer.getBalance() > 0) {
            matchController.getEconomyController().withdrawFromPlayer(payer, payer.getBalance());
        }
        payer.setState(BankruptState.getInstance());
        matchController.getMainViewImpl().refreshAll();
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
