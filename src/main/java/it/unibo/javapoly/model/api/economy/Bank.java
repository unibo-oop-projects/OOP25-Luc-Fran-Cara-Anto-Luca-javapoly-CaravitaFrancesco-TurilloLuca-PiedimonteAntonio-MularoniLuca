package it.unibo.javapoly.model.api.economy;

import it.unibo.javapoly.model.api.Player;

/**
 * Model interface responsible only for monetary operations.
 * The Bank handles all money deposit, withdrawals, transfers.
 */
public interface Bank {

    /**
     * Deposit money to a player's balance.
     *
     * @param player the player receiving the money.
     * @param amount the amount to deposit.
     */
    void deposit(Player player, int amount);

    /**
     * Withdraws money from a player's balance.
     *
     * @param player the player from whom money is withdrawn.
     * @param amount the amount to withdraw.
     * @return {@code true} if the withdrawn was successful, {@code false} if insufficient funds
     */
    boolean withdraw(Player player, int amount);

    /**
     * Transfers funds from one player to another.
     *
     * @param payer the player sending money.
     * @param payee the player receiving money.
     * @param amount the amount to transfer.
     * @return {@code true} if the transfer was successful, {@code false} if insufficient funds
     */
    boolean transferFunds(Player payer, Player payee, int amount);
}
