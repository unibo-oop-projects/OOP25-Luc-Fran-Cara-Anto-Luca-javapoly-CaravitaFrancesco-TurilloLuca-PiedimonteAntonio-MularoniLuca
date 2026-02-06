package it.unibo.javapoly.model.api.economy;

import java.util.Optional;

/**
 * Immutable record representing a financial transaction in the game.
 *
 * @param id the unique identifier for this transaction.
 * @param type the type of transaction.
 * @param payerId the unique identifier of the player performing the payment (empty if bank).
 * @param payeeId the unique identifier of the player receiving funds (empty if bank).
 * @param propertyId the property involved in the transaction (empty if none).
 * @param amount the monetary amount of the transaction.
 */
public record Transaction(
        int id,
        TransactionType type,
        Optional<String> payerId,
        Optional<String> payeeId,
        Optional<String> propertyId,
        int amount
) {

    /**
     * Creates a transaction for a player-to-player payment.
     *
     * @param id the unique identifier for this transaction.
     * @param type the type of transaction.
     * @param payerId the unique identifier of the player performing the payment (empty if bank).
     * @param payeeId the unique identifier of the player receiving funds (empty if bank).
     * @param amount the monetary amount of the transaction.
     * @return a new Transaction
     */
    public static Transaction playerToPlayer(final int id,
                                             final TransactionType type,
                                             final String payerId,
                                             final String payeeId,
                                             final int amount) {
        return new Transaction(id, type, Optional.of(payerId), Optional.of(payeeId), Optional.empty(), amount);

    }

    /**
     * Creates a transaction for a bank-to-player payment.
     *
     * @param id the unique identifier for this transaction.
     * @param type the type of transaction.
     * @param payeeId the unique identifier of the player receiving funds (empty if bank).
     * @param amount the monetary amount of the transaction.
     * @return a new Transaction
     */
    public static Transaction bankToPlayer(final int id,
                                             final TransactionType type,
                                             final String payeeId,
                                             final int amount) {
        return new Transaction(id, type, Optional.empty(), Optional.of(payeeId), Optional.empty(), amount);
    }

    /**
     * Creates a transaction for a player-to-bank payment.
     *
     * @param id the unique identifier for this transaction.
     * @param type the type of transaction.
     * @param payerId the unique identifier of the player performing the payment (empty if bank).
     * @param amount the monetary amount of the transaction.
     * @return a new Transaction
     */
    public static Transaction playerToBank(final int id,
                                           final TransactionType type,
                                           final String payerId,
                                           final String propertyId,
                                           final int amount) {
        return new Transaction(id, type, Optional.of(payerId), Optional.empty(), Optional.of(propertyId), amount);
    }
}
