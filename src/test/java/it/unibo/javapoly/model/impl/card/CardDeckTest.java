package it.unibo.javapoly.model.impl.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.api.card.payload.MoveToPayload;

/**
 * Unit tests for {@link CardDeckImpl}.
 *
 * <p>
 * The test suite verifies only observable behavior through the public API.
 */
@DisplayName("CardDeckImpl tests")
class CardDeckTest {

    private static final String PLAYER1 = "player1";
    private static final String PLAYER2 = "player2";

    private GameCard createCard(
            final String id,
            final CardType type,
            final boolean keep) {
        final MoveToPayload payload = new MoveToPayload(1);
        return new GameCardImpl(id, "name-" + id, "desc-" + id, type, payload, keep);
    }

    /**
     * Verifies that getAllCards() returns a defensive copy.
     */
    @Test
    @DisplayName("getAllCards returns a defensive copy")
    void testGetAllCardsReturnsCopy() {
        final List<GameCard> input = new ArrayList<>();
        input.add(createCard("a", CardType.NO_EFFECT, false));
        input.add(createCard("b", CardType.PAY_BANK, false));

        final CardDeckImpl deck = new CardDeckImpl(input);

        final List<GameCard> all = deck.getAllCards();
        assertEquals(2, all.size());

        all.clear();

        final List<GameCard> after = deck.getAllCards();
        assertEquals(2, after.size());
    }

    /**
     * When a keep-until-used card is drawn,
     * discardByType should succeed for the correct player.
     */
    @Test
    @DisplayName("draw keeps card for player when required")
    void testDrawKeepsWhenRequired() {
        final GameCard keep = createCard(
                "keep",
                CardType.GET_OUT_OF_JAIL_FREE,
                true);

        final List<GameCard> list = new ArrayList<>();
        list.add(keep);

        final CardDeckImpl deck = new CardDeckImpl(list);

        final GameCard drawn = deck.draw(PLAYER1);

        assertNotNull(drawn);
        assertEquals("keep", drawn.getId());

        final boolean ok = deck.discardByType(
                CardType.GET_OUT_OF_JAIL_FREE,
                PLAYER1);
        assertTrue(ok);

        final boolean second = deck.discardByType(
                CardType.GET_OUT_OF_JAIL_FREE,
                PLAYER1);
        assertFalse(second);
    }

    /**
     * Verifies that discard(GameCard) removes a held card
     * so that discardByType no longer succeeds.
     */
    @Test
    @DisplayName("discard removes held card")
    void testDiscardRemovesHeldCard() {
        final GameCard card = createCard(
                "d1",
                CardType.GET_OUT_OF_JAIL_FREE,
                true);

        final List<GameCard> list = new ArrayList<>();
        list.add(card);

        final CardDeckImpl deck = new CardDeckImpl(list);

        final GameCard drawn = deck.draw(PLAYER1);
        assertNotNull(drawn);

        deck.discard(drawn);

        final boolean result = deck.discardByType(
                CardType.GET_OUT_OF_JAIL_FREE,
                PLAYER1);
        assertFalse(result);
    }

    /**
     * Verifies discardByType returns false
     * if the player does not hold that card type.
     */
    @Test
    @DisplayName("discardByType returns false for wrong player")
    void testDiscardByTypeWrongPlayer() {
        final GameCard card = createCard(
                "ct",
                CardType.GET_OUT_OF_JAIL_FREE,
                true);

        final List<GameCard> list = new ArrayList<>();
        list.add(card);

        final CardDeckImpl deck = new CardDeckImpl(list);

        deck.draw(PLAYER1);

        final boolean result = deck.discardByType(
                CardType.GET_OUT_OF_JAIL_FREE,
                PLAYER2);
        assertFalse(result);
    }

    /**
     * Verifies that isEmpty() behaves correctly.
     */
    @Test
    @DisplayName("isEmpty reflects deck state")
    void testIsEmpty() {
        final List<GameCard> list = new ArrayList<>();
        list.add(createCard("x", CardType.NO_EFFECT, false));

        final CardDeckImpl deck = new CardDeckImpl(list);

        assertFalse(deck.isEmpty());

        deck.draw(PLAYER1);

        assertTrue(deck.isEmpty());
    }
}
