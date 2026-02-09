package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;

/**
 * Represents a tax tile.
 */
@JsonRootName("TaxTile")
public final class TaxTile extends AbstractTile {

    private final int amountTax;

    /**
     * Creates a tax tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     * @param amount the tax amount to be paid
     */
    @JsonCreator
    public TaxTile(
            @JsonProperty("position") final int position,
            @JsonProperty("name") final String name,
            @JsonProperty("amountTax") final int amount) {
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
