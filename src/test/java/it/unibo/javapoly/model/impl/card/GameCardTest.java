package it.unibo.javapoly.model.impl.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.api.card.payload.MoveToPayload;

/**
 * Unit tests for {@link GameCardImpl}.
 *
 * <p>
 * Verifies the getters and the textual representation.
 */
@DisplayName("GameCardImpl tests")
class GameCardTest {

    private static final String CARD_ID = "c1";
    private static final String NAME = "name-";

    private GameCard createCard(final String id, final CardType type, final boolean keep) {
        final MoveToPayload payload = new MoveToPayload(1);
        return new GameCardImpl(id, NAME + id, "desc-" + id, type, payload, keep);
    }

    /**
     * Verifies getters and toString() contain expected content.
     */
    @Test
    @DisplayName("getters and toString")
    void testGameCardGettersAndToString() {
        final GameCard c = createCard(CARD_ID, CardType.MOVE_TO, true);

        assertEquals(CARD_ID, c.getId());
        assertEquals(NAME + CARD_ID, c.getName());
        assertEquals("desc-" + CARD_ID, c.getDescription());
        assertEquals(CardType.MOVE_TO, c.getType());
        assertNotNull(c.getPayload());
        assertTrue(c.isKeepUntilUsed());

        final String s = c.toString();
        assertTrue(s.contains(CARD_ID));
        assertTrue(s.contains(NAME + CARD_ID));
    }
}
