package it.unibo.javapoly.model.impl.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.impl.board.tile.StartTile;

/**
 * Unit tests for {@link BoardImpl}.
 *
 * <p>
 * These tests verify the board size, tile access, and index normalization behavior.
 */
@DisplayName("BoardImpl tests")
class BoardTest {

    private static final int START_TILE_VALUE = 200;

    /**
     * Verifies size(), getTileAt() and normalizePosition() behaviors.
     */
    @Test
    @DisplayName("size, getTileAt and normalizePosition behavior")
    void testBoardSizeAndGetTileAtAndNormalize() {
        final List<Tile> tiles = new ArrayList<>();
        tiles.add(new StartTile(0, "Start", START_TILE_VALUE, "Start description"));
        tiles.add(new StartTile(1, "Second", 0, "Second description"));
        tiles.add(new StartTile(2, "Third", 0, "Third description"));

        final Board board = new BoardImpl(tiles);

        assertEquals(3, board.size());

        final Tile t0 = board.getTileAt(0);
        assertNotNull(t0);
        assertEquals("Start", t0.getName());
        assertEquals("Start description", t0.getDescription());

        final Tile t3 = board.getTileAt(3);
        assertNotNull(t3);
        assertEquals(t0.getName(), t3.getName());
        assertEquals(t0.getDescription(), t3.getDescription());

        final Tile tn1 = board.getTileAt(-1);
        assertNotNull(tn1);
        assertEquals("Third", tn1.getName());
        assertEquals("Third description", tn1.getDescription());

        assertEquals(0, board.normalizePosition(0));
        assertEquals(2, board.normalizePosition(-1));
        assertEquals(1, board.normalizePosition(4));
    }
}
