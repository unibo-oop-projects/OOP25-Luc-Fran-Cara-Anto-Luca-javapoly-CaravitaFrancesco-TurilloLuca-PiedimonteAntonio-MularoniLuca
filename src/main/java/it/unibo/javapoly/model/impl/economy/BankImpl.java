package it.unibo.javapoly.model.impl.economy;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.utils.ValidationUtils;

import java.util.Objects;

/**
 * Implementation of the Bank interface.
 */
public final class BankImpl implements Bank {

    private static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be positive";
    private static final String PLAYER_NO_NULL = "Player can not be null";

    /**
     * {@inheritDoc}
     */
    @Override
    public void deposit(final Player player, final int amount) {
        Objects.requireNonNull(player, PLAYER_NO_NULL);
        ValidationUtils.requirePositive(amount, AMOUNT_MUST_BE_POSITIVE);
        player.receiveMoney(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean withdraw(final Player player, final int amount) {
        Objects.requireNonNull(player, PLAYER_NO_NULL);
        ValidationUtils.requireNonNegative(amount, AMOUNT_MUST_BE_POSITIVE);
        return player.tryToPay(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transferFunds(final Player payer, final Player payee, final int amount) {
        Objects.requireNonNull(payer, PLAYER_NO_NULL);
        Objects.requireNonNull(payee, PLAYER_NO_NULL);
        ValidationUtils.requireNonNegative(amount, AMOUNT_MUST_BE_POSITIVE);
        if (payer.tryToPay(amount)) {
            payee.receiveMoney(amount);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAfford(final Player player, final int amount) {
        Objects.requireNonNull(player, PLAYER_NO_NULL);
        ValidationUtils.requirePositive(amount, AMOUNT_MUST_BE_POSITIVE);
        return player.getBalance() >= amount;
    }
}
