package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.model.api.economy.Transaction;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.economy.BankImpl;
import it.unibo.javapoly.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static it.unibo.javapoly.model.api.economy.TransactionType.DEPOSIT_FROM_BANK;
import static it.unibo.javapoly.model.api.economy.TransactionType.WITHDRAW_TO_BANK;
import static it.unibo.javapoly.model.api.economy.TransactionType.BUY_HOUSE;
import static it.unibo.javapoly.model.api.economy.TransactionType.PAY_RENT;
import static it.unibo.javapoly.model.api.economy.TransactionType.SELL_HOUSE;
import static it.unibo.javapoly.model.api.economy.TransactionType.SELL_PROPERTY;
import static it.unibo.javapoly.model.api.economy.TransactionType.BUY_PROPERTY;

/**
 * Implementation of the EconomyController interface.
 */
public final class EconomyControllerImpl implements EconomyController {

    private final Bank bank;
    private final List<Player> players;
    private final PropertyController propertyController;
    private final List<Transaction> transactionHistory = new ArrayList<>();
    private int nextTransactionId = 1;
    private final List<LiquidationObserver> liquidationObservers = new ArrayList<>();

    /**
     * Creates an EconomyController with the given list of properties.
     *
     * @param propertyController property controller.
     * @param players list of all player.
     */
    public EconomyControllerImpl(final PropertyController propertyController, final List<Player> players) {
        this.bank = new BankImpl();
        this.propertyController = propertyController;
        this.players = players;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> getTransactions() {
        return new ArrayList<>(this.transactionHistory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void depositToPlayer(final Player player, final int amount) {
        this.bank.deposit(player, amount);
        recordTransaction(new Transaction(this.nextTransactionId,
                    DEPOSIT_FROM_BANK,
                    Optional.empty(),
                    Optional.of(player.getName()),
                    Optional.empty(),
                    amount));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean withdrawFromPlayer(final Player player, final int amount) {
        if (this.bank.withdraw(player, amount)) {
            recordTransaction(new Transaction(this.nextTransactionId,
                    WITHDRAW_TO_BANK,
                    Optional.of(player.getName()),
                    Optional.empty(),
                    Optional.empty(),
                    amount));
            return true;
        }
        //TODO bankrupt
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean payRent(final Player payer, final String payeeId, final Property property, final int diceRoll) {
        final Player payee = getPlayerByID(payeeId);
        final int rent = this.propertyController.getRent(payer, property.getId(), diceRoll);
        if (this.bank.transferFunds(payer, payee, rent)) {
            recordTransaction(new Transaction(this.nextTransactionId,
                    PAY_RENT,
                    Optional.of(payer.getName()),
                    Optional.of(payee.getName()),
                    Optional.of(property.getId()),
                    rent));
            return true;
        }
        //TODO bankrupt
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean purchaseProperty(final Player buyer, final Property property) {
        final int price = property.getPurchasePrice();
        if (this.bank.withdraw(buyer, price)) {
            this.propertyController.purchaseProperty(buyer, property.getId());
            recordTransaction(new Transaction(this.nextTransactionId,
                    BUY_PROPERTY,
                    Optional.of(buyer.getName()),
                    Optional.empty(),
                    Optional.of(property.getId()),
                    price));
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
                this.bank.withdraw(owner, houseCost); //it cant fail because with the first if we see that player have money
                recordTransaction(new Transaction(this.nextTransactionId,
                        BUY_HOUSE,
                        Optional.of(owner.getName()),
                        Optional.empty(),
                        Optional.of(property.getId()),
                        houseCost));
                return true;
            }
            throw new IllegalStateException("You don't own all the properties of the same color/ te house are not homogeneous");
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
            recordTransaction(new Transaction(this.nextTransactionId,
                    SELL_HOUSE,
                    Optional.empty(),
                    Optional.of(owner.getName()),
                    Optional.of(property.getId()),
                    price));
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
        recordTransaction(new Transaction(this.nextTransactionId,
                    SELL_PROPERTY,
                    Optional.empty(),
                    Optional.of(owner.getName()),
                    Optional.of(property.getId()),
                    price));
        return true;
    }

    /**
     * Records a transaction in the history.
     *
     * @param transaction the transaction to record.
     */
    private void recordTransaction(final Transaction transaction) {
        this.transactionHistory.add(transaction);
        this.nextTransactionId++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLiquidationObserver(final LiquidationObserver observer) {
        this.liquidationObservers.add(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLiquidationObserver(final LiquidationObserver observer) {
        this.liquidationObservers.remove(observer);
    }

    /**
     * Return player with the same playerID from input.
     *
     * @param playerId id of the player
     * @return player with the same playerID from input.
     */
    private Player getPlayerByID(final String playerId) {
        return players.stream()
                .filter(p -> p.getName().equals(playerId))
                .findFirst()
                .orElse(null);
    }
}
