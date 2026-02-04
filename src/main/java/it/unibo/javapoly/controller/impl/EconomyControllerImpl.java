package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.model.api.economy.PropertyManager;
import it.unibo.javapoly.model.api.economy.Transaction;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.economy.BankImpl;
import it.unibo.javapoly.model.impl.economy.PropertyManagerImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the EconomyController interface.
 */
public class EconomyControllerImpl implements EconomyController {

    private final Bank bank;
    private final PropertyManager propertyManager;
    private final List<Transaction> transactionHistory;

    /**
     * Creates an EconomyController with the given list of properties.
     *
     * @param properties the list of all properties in the game
     */
    public EconomyControllerImpl(final List<Property> properties) {
        this.bank = new BankImpl();
        this.propertyManager = new PropertyManagerImpl(properties);
        this.transactionHistory = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bank getBank() {
        return this.bank;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyManager getPropertyManager() {
        return this.propertyManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> getTransactions() {
        return new ArrayList<>(this.transactionHistory);
    }
}
