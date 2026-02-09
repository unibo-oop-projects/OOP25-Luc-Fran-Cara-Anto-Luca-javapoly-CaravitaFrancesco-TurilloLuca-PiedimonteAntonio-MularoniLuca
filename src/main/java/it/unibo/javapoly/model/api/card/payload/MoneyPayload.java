package it.unibo.javapoly.model.api.card.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The class represents the payload of a "Money" object.
 * It contains the amount and the receiver associated with it.
 */
@JsonRootName("PayLoadMoney")
public final class MoneyPayload implements CardPayload {
    /**
     * Contains the minimum value that the amounts must have.
     */
    private static final int ERR_VALUE = 0;

    /**
     * Contains the String error for the value that the amounts must have.
     */
    private static final String ERR_MSG = "amount >= 0";

    private final int amount;
    private final String receiver;

    /**
     * Constructor to create an instance of MoneyPayload.
     *
     * @param amount the amount to transfer
     * @param receiver the receiver of the transfer
     * @throws IllegalArgumentException if the amount is negative
     */
    @JsonCreator
    public MoneyPayload(@JsonProperty("amount") final int amount, 
                        @JsonProperty("receiver") final String receiver) {
        if (amount < this.ERR_VALUE) {
            throw new IllegalArgumentException(this.ERR_MSG);
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
