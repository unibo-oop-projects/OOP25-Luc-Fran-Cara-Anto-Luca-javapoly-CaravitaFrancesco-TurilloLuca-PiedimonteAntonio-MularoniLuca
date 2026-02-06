package it.unibo.javapoly.model.impl.economy;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.economy.Bank;

import java.util.Objects;

/**
 * Implementation of the Bank interface.
 */
public final class BankImpl implements Bank {

    private static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be positive";

    /**
     * {@inheritDoc}
     */
    @Override
    public void deposit(final Player player, final int amount) {
        Objects.requireNonNull(player, "Player can not be null");
        if (amount < 0) {
            throw new IllegalArgumentException(AMOUNT_MUST_BE_POSITIVE);
        }
        player.receiveMoney(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean withdraw(final Player player, final int amount) {
        Objects.requireNonNull(player, "Player can not be null");
        if (amount < 0) {
            throw new IllegalArgumentException(AMOUNT_MUST_BE_POSITIVE);
        }
        return player.tryToPay(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transferFunds(final Player payer, final Player payee, final int amount) {
        Objects.requireNonNull(payer, "Payer can not be null");
        Objects.requireNonNull(payee, "Payee can not be null");
        if (amount < 0) {
            throw new IllegalArgumentException(AMOUNT_MUST_BE_POSITIVE);
        }
        if (payer.tryToPay(amount)) {
            payee.receiveMoney(amount);
            return true;
        }
        return false;
    }
}
