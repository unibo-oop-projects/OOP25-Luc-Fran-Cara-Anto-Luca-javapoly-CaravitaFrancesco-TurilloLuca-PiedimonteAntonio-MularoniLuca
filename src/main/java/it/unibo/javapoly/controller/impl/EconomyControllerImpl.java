package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.economy.BankImpl;
import it.unibo.javapoly.utils.ValidationUtils;

import java.util.Objects;

/**
 * Implementation of the EconomyController interface.
 */
public final class EconomyControllerImpl implements EconomyController {

    private final Bank bank;
    private final PropertyController propertyController;
    private LiquidationObserver liquidationObserver;

    /**
     * Creates an EconomyController with the given list of properties.
     *
     * @param propertyController property controller.
     */
    public EconomyControllerImpl(final PropertyController propertyController) {
        this.bank = new BankImpl();
        this.propertyController = propertyController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void depositToPlayer(final Player player, final int amount) {
        this.bank.deposit(player, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean afford(final Player player, final int amount) {
        return this.bank.canAfford(player, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void withdrawFromPlayer(final Player player, final int amount) {
        if (!this.bank.withdraw(player, amount) && this.liquidationObserver != null) {
            this.liquidationObserver.onInsufficientFunds(player, null, amount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void payRent(final Player payer, final Player payee, final Property property, final int diceRoll) {
        if (payee == null) {
            return;
        }
        final int currentBalance = payer.getBalance();
        final int rent = this.propertyController.getRent(payer, property.getId(), diceRoll);
        if (currentBalance >= rent && this.bank.transferFunds(payer, payee, rent)) {
            return;
        }
        if (this.liquidationObserver != null) {
            this.liquidationObserver.onInsufficientFunds(payer, payee, rent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean purchaseProperty(final Player buyer, final Property property) {
        final int price = property.getPurchasePrice();
        if (bank.canAfford(buyer, price) && this.propertyController.purchaseProperty(buyer, property.getId())) {
            this.bank.withdraw(buyer, price);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean purchaseHouse(final Player owner, final Property property) {
        final int houseCost = this.propertyController.getHouseCost(property);
        ValidationUtils.requirePositive(houseCost, "Is not a land");
        if (this.bank.canAfford(owner, houseCost)) {
            if (this.propertyController.buildHouse(owner, property.getId())) {
                this.bank.withdraw(owner, houseCost);
                return true;
            }
            throw new IllegalStateException("You don't own all the properties of the same color/house are not homogeneous");
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sellHouse(final Player owner, final Property property) {
        final int price = this.propertyController.getHouseCost(property) / 2;
        if (this.propertyController.destroyHouse(owner, property.getId())) {
            this.bank.deposit(owner, price);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sellProperty(final Player owner, final Property property) {
        Objects.requireNonNull(owner, "owner cannot be null");
        ValidationUtils.requireNonBlank(owner.getName(), "owner cannot be the bank");
        Objects.requireNonNull(property, "property cannot be null");
        if (!property.getState().getOwnerId().equals(owner.getName())) {
            return false;
        }
        final int price = property.getPurchasePrice() / 2;
        this.propertyController.returnPropertyToBank(property);
        this.bank.deposit(owner, price);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLiquidationObserver(final LiquidationObserver newObserver) {
        this.liquidationObserver = newObserver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void payPlayer(final Player payer, final Player payee, final int amount) {
        if (payer.getBalance() < amount) {
            return;
        }
        this.bank.withdraw(payer, amount);
        this.bank.deposit(payee, amount);
    }
}
