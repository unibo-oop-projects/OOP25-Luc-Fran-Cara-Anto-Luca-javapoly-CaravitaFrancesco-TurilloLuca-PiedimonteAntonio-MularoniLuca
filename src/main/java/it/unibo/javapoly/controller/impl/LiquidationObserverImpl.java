package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.utils.ValidationUtils;

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
        //TODO create response
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
