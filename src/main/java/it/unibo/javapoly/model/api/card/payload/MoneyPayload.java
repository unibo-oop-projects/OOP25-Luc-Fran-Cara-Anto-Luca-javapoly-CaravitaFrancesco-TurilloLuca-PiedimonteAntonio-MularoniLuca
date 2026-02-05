package it.unibo.javapoly.model.api.card.payload;

/**
 * The class represents the payload of a "Money" object.
 * It contains the amount and the receiver associated with it.
 */
public final class MoneyPayload implements CardPayload {

    private final int amount;
    private final String receiver;

    /**
     * Constructor to create an instance of MoneyPayload.
     *
     * @param amount the amount to transfer
     * @param receiver the receiver of the transfer
     * @throws IllegalArgumentException if the amount is negative
     */
    public MoneyPayload(final int amount, final String receiver) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount >= 0");
        }
        this.amount = amount;
        this.receiver = receiver;
    }

    /**
     * Returns the amount associated with this payload.
     * 
     * @return the amount
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Returns the receiver associated with this payload.
     * 
     * @return the receiver
     */
    public String getReceiverMoney() {
        return this.receiver;
    }

    /**
     * Returns a string representation of the MoneyPayload object.
     * 
     * @return a string representing the object
     */
    @Override
    public String toString() {
        return "MoneyPayload[amount=" + this.amount + ", receiver=" + this.receiver + "]";
    }
}
