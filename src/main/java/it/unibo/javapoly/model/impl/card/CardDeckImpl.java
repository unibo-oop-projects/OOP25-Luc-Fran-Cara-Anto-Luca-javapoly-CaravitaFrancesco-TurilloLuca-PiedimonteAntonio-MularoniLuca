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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Implementation of the CardDeck interface, representing a deck of game cards.
 * It allows drawing, discarding, shuffling, and checking if the deck is empty.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CardDeckImpl implements CardDeck {

    private final Deque<GameCard> drawPile;
    private final Deque<GameCard> discardPile;
    private final Map<GameCard, String> heldCards;
    private final List<GameCard> cards;

    @JsonIgnore
    private final Random random;

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

    /**
     * Constructs a new CardDeckImpl with the provided list of cards.
     * The cards are added to the discard pile, because the first time 
     * we draw cards, the cards will be shuffled. 
     * (This is because we cannot call @Override methods in the constructor)
     * 
     * @param cards the list of cards to initialize the deck with
     * @param drawPile the list of cards to be drawed 
     * @param discardPile the list of cards that are already drawed
     * @param heldCards the list of cards that a player is keeping for later
     */
    @JsonCreator
    public CardDeckImpl(@JsonProperty("cards") final List<GameCard> cards,
                        @JsonProperty("drawPile") final Deque<GameCard> drawPile,
                        @JsonProperty("discardPile") final Deque<GameCard> discardPile,
                        @JsonProperty("heldCards") final Map<String, String> heldCards) {
        this.drawPile = new ArrayDeque<>(drawPile != null ? drawPile : new ArrayDeque<>());
        this.discardPile = new ArrayDeque<>(discardPile != null ? discardPile : new ArrayDeque<>());
        this.cards = new ArrayList<>(cards != null ? cards : new ArrayList<>());
        this.random = new Random();
        this.heldCards = new HashMap<>();

        if (heldCards != null && cards != null) {
            for (final Map.Entry<String, String> entry : heldCards.entrySet()) {
                final String cardId = entry.getKey();
                final String playerId = entry.getValue();

                GameCard card = null;
                for (final GameCard c : cards) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        break;
                    }
                }

                if (card != null) {
                    this.heldCards.put(card, playerId);
                }
            }
        }
    }

    //#region Getter
    /**
     * Getter to serialize: trasform Map<GameCard String>.
     * 
     * <p>
     * This method is used by the library Jackson to serialize
     * the map of GameCard - ownderID
     * 
     * @return the map Map<String String>
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @JsonProperty("heldCards")
    private Map<String, String> getHeldCardsJson() {
        final Map<String, String> result = new HashMap<>();
        for (final Map.Entry<GameCard, String> entry : heldCards.entrySet()) {
            result.put(entry.getKey().getId(), entry.getValue());
        }
        return result;
    }

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
        } else {
            discardPile.addFirst(card);
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

        final GameCard card = playerHeldsCardType(type, playerID);

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

    /**
     * Checks if a player holds a card of a specific type among the cards they possess.
     * 
     * <p>
     * This method search in the map {@code heldCards} the card by the playerID.
     * if any of the cards match the specified type, he will also check if he is the owner
     *
     * @param type the type of card being searched
     * @param playerID the ID of the player
     * @return the card found that matches the specified type and player ID, or {@code null} otherwise
     */
    private GameCard playerHeldsCardType(final CardType type, final String playerID) {
        for (final Map.Entry<GameCard, String> entry : this.heldCards.entrySet()) {
            if (entry.getValue().equals(playerID) && entry.getKey().getType() == type) {
                return entry.getKey();
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
