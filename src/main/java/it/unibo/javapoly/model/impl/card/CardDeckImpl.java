package it.unibo.javapoly.model.impl.card;

import it.unibo.javapoly.model.api.card.CardDeck;
import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.card.GameCard;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Implementation of the CardDeck interface, representing a deck of game cards.
 * It allows drawing, discarding, shuffling, and checking if the deck is empty.
 */
public class CardDeckImpl implements CardDeck {

    private final Deque<GameCard> drawPile;
    private final Deque<GameCard> discardPile;
    private final Map<GameCard, String> heldCards;
    private final Random random;
    private final List<GameCard> cards;

    /**
     * Constructs a new CardDeckImpl with the provided list of cards.
     * The cards are added to the discard pile, because the first time 
     * we draw cards, the cards will be shuffled. 
     * (This is because we cannot call @Override methods in the constructor)
     * 
     * @param cards the list of cards to initialize the deck with
     */
    public CardDeckImpl(final List<GameCard> cards) {
        this.drawPile = new ArrayDeque<>();
        this.discardPile = new ArrayDeque<>(cards);
        this.heldCards = new HashMap<>();
        random = new Random();
        this.cards = new ArrayList<>(cards);
    }

    //#region Getter
    /**
     * Returns a copy of the list containing all the cards in the deck.
     * This includes both the cards in the draw pile and discard pile.
     *
     * @return a list of all the GameCard objects in the deck
     */
    public List<GameCard> getAllCards() {
        return new ArrayList<>(this.cards);
    }
    //#endregion

    /**
     * Draws a card from the draw pile for the given player.
     * If the draw pile is empty, the discard pile is recycled.
     * 
     * @param playerId the ID of the player drawing the card
     * @return the drawn GameCard
     */
    @Override
    public GameCard draw(final String playerId) {
        if (drawPile.isEmpty()) {
            recycleDiscard();
        }

        final GameCard card = drawPile.removeFirst();

        if (card.isKeepUntilUsed()) {
            heldCards.put(card, playerId);
        }

        return card;
    }

    /**
     * Discards the given card, removing it from the held cards and adding it to the discard pile.
     * 
     * @param card the card to discard
     */
    @Override
    public void discard(final GameCard card) {
        heldCards.remove(card);
        discardPile.addFirst(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean discardByType(final CardType type, final String playerID) {

        final GameCard card = isHeldsByPlayer(type, playerID);

        if (Objects.isNull(card)) {
            return false;
        }

        discard(card);
        return true;
    }

    /**
     * Shuffles the cards in the draw pile.
     * The deck is shuffled using a random generator.
     */
    @Override
    public void shuffle() {
        final List<GameCard> temp = new ArrayList<>(drawPile);
        Collections.shuffle(temp, random);
        drawPile.clear();
        drawPile.addAll(temp);
    }

    /**
     * Checks if both the draw pile and discard pile are empty.
     * 
     * @return true if both piles are empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return drawPile.isEmpty() && discardPile.isEmpty();
    }

    private GameCard isHeldsByPlayer(final CardType type, final String playerID) {
        for (final GameCard card : this.heldCards.keySet()) {
            if (this.heldCards.get(card).equals(playerID) && card.getType() == type) {
                return card;
            }
        }

        return null;
    }

    /**
     * Recycles the cards from the discard pile into the draw pile.
     * The cards are added back to the draw pile and the deck is shuffled again.
     */
    private void recycleDiscard() {
        while (!discardPile.isEmpty()) {
            drawPile.addLast(discardPile.removeFirst());
        }
        shuffle();
    }
}
