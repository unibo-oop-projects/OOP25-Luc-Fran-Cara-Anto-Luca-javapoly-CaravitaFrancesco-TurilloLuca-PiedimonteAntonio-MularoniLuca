package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents a tax tile.
 */
public final class TaxTile extends AbstractTile {

    private final int amountTax;

    /**
     * Creates a tax tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     * @param amount the tax amount to be paid
     */
    public TaxTile(final int position,
                   final String name,
                   final int amount) {
        super(position, TileType.TAX, name);
        this.amountTax = amount;
    }

    /**
     * Returns the tax amount.
     *
     * @return the tax amount
     */
    public int getAmountTax() {
        return this.amountTax;
    }
}
