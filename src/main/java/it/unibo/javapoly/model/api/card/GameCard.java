package it.unibo.javapoly.model.api.card;

import it.unibo.javapoly.model.api.card.payload.CardPayload;

/**
 * General interface that every type of card will implement (not the {@code ProprietyCard} type).
 */
public interface GameCard extends Card {

    /**
     * Returns the logical card type (e.g., MOVE_TO, PAY_BANK, etc.).
     * 
     * @return the card type.
     */
    CardType getType();

    /**
     * Returns the data associated with the card (e.g., amount, targetPosition, delta, etc.).
     * The controller interprets the data.
     * 
     * @return a CardPayload containing the data associated with the card.
     */
    CardPayload getPayload();

    /**
     * Returns whether the card should be kept by the player (e.g., Get Out of Jail Free).
     * 
     * @return true if the card should be kept until used, false otherwise.
     */
    boolean isKeepUntilUsed();
}
