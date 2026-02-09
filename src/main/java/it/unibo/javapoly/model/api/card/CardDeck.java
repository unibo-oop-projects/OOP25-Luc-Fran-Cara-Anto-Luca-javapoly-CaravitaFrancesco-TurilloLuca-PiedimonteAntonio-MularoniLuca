package it.unibo.javapoly.model.api.card;

/**
 * Represents a deck of game cards.
 * Provides operations to draw, discard, shuffle cards, and check if the deck is empty.
 */
public interface CardDeck {

    /**
     * Draws a card from the deck for the specified player.
     *
     * @param playerId the ID of the player drawing the card
     * @return the {@link GameCard} drawn from the deck
     */
    GameCard draw(String playerId);

    /**
     * Discards a card back into the deck or discard pile.
     *
     * @param card the {@link GameCard} to discard
     */
    void discard(GameCard card);

    /**
     * Shuffles the cards in the deck randomly.
     */
    void shuffle();

    /**
     * Checks if the deck has no more cards to draw.
     *
     * @return {@code true} if the deck is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Discards the given card type, only if the player helds it.
     * Removing it from the held cards and adding it to the discard pile.
     * 
     * @param type the type card to use and discard
     * @param playerID tha player id that should have the card with the same type
     * 
     * @return {@code true} if the give type card has been discarded, {@code false} otherwise
     */
    boolean discardByType(CardType type, String playerID);

}
