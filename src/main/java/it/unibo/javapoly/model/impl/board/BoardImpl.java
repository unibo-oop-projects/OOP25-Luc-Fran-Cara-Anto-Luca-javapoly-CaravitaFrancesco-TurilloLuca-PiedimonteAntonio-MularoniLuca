package it.unibo.javapoly.model.impl.board;

import java.util.ArrayList;
import java.util.List;

import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;

/**
 * Concrete implementation of the game board.
 */
public final class BoardImpl implements Board {

    private final List<Tile> tiles;

    /**
     * Creates a board with the given tiles.
     *
     * @param tiles the tiles composing the board
     */
    public BoardImpl(final List<Tile> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return tiles.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile getTileAt(final int position) {
        return tiles.get(normalizePosition(position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int normalizePosition(final int position) {
        final int size = size();
        return ((position % size) + size) % size;
    }
}
