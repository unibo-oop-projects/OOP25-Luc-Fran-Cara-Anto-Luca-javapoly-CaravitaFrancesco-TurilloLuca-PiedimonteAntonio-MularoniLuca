package it.unibo.javapoly.controller.api;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.economy.Transaction;
import it.unibo.javapoly.model.api.property.Property;

import java.util.List;

/**
 * Main controller interface for managing the economy of the game.
 */
public interface EconomyController {

    /**
     * Gets the complete transaction history.
     *
     * @return list of all transaction.
     */
    List<Transaction> getTransactions();

    /**
     * Deposits money to a player from bank.
     *
     * @param player the player receiving money.
     * @param amount the amount to deposit.
     * @throws NullPointerException if {@code player} is {@code null}.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     */
    void depositToPlayer(Player player, int amount);

    /**
     * Withdraws money from a player to the bank.
     *
     * @param player the player from whom to withdraw.
     * @param amount the amount to withdraw.
     * @return {@code true} if successful, {@code false} if insufficient funds (bankruptcy)
     * @throws NullPointerException if {@code player} is {@code null}.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     */
    boolean withdrawFromPlayer(Player player, int amount);

    /**
     * Transfer property ownership from bank to player.
     *
     * @param buyer future owner of the property.
     * @param property to buy.
     * @return {@code true} if successful, {@code false}.
     */
    boolean purchaseProperty(Player buyer, Property property);

    /**
     * Builds a house on a property.
     *
     * @param owner of the property.
     * @param property where to build house.
     * @return {@code true} if successful, {@code false} otherwise.
     */
    boolean purchaseHouse(Player owner, Property property);

    /**
     * Sell a house from a property to bank.
     *
     * @param owner of the property.
     * @param property where to sell house.
     * @return {@code true} if successful, {@code false} if insufficient funds (bankruptcy).
     */
    boolean sellHouse(Player owner, Property property);

    /**
     * Sell a property to bank.
     *
     * @param owner ex-owner of the property.
     * @param property property to sell.
     * @return {@code true} if successful, {@code false} otherwise.
     */
    boolean sellProperty(Player owner, Property property);

    /**
     * Pay rent to payee.
     *
     * @param payer the player who make payment.
     * @param payee tha player who collect payment.
     * @param property property where is the payer (owned by payee).
     * @param diceRoll the dice roll used to calculate rent (especially for utilities).
     * @return {@code true} if successful, {@code false} if insufficient funds (bankruptcy).
     */
    boolean payRent(Player payer, Player payee, Property property, int diceRoll);
}
