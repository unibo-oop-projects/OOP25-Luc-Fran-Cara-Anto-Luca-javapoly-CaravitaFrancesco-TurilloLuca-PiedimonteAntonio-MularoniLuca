package it.unibo.javapoly.model.api.card;

/**
 * Common minimal interface for a card in the game.
 * 
 * <p>
 * Implementations represent different kinds of cards (properties, chance, etc.).
 */
public interface Card {

    /**
     * Returns the unique identifier of the card.
     *
     * @return the card id.
     */
    String getId();

    /**
     * Returns the display name of the card.
     *
     * @return the card name.
     */
    String getName();

    /**
     * Returns the description of the card.
     *
     * @return the card description.
     */
    String getDescription();

    /**
     * Returns a string representation of the card containing
     * relevant fields (id, name, description) for debugging or logging.
     *
     * @return the string representation of the card.
     */
    @Override
    String toString();
}
