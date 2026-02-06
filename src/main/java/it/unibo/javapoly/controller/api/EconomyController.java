package it.unibo.javapoly.controller.api;

import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.model.api.economy.PropertyManager;
import it.unibo.javapoly.model.api.economy.Transaction;

import java.util.List;

/**
 * Main controller interface for managing the economy of the game.
 */
public interface EconomyController {

    /**
     * Gets the {@link Bank} instance used for monetary operations.
     *
     * @return the {@link Bank} instance.
     */
    Bank getBank();

    /**
     * Gets the {@link PropertyManager} instance used for property operations.
     *
     * @return {@link PropertyManager} instance.
     */
    PropertyManager getPropertyManager();

    /**
     * Gets the complete transaction history.
     *
     * @return list of all transaction.
     */
    List<Transaction> getTransactions();

}
