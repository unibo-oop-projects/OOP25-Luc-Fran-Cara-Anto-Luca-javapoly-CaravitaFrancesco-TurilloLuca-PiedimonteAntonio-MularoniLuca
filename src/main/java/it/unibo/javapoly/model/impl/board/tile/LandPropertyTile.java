package it.unibo.javapoly.model.impl.board.tile;

import java.util.Objects;

import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.property.PropertyImpl;

/**
 * Represents a tile associated with a land property.
 */
public final class LandPropertyTile extends AbstractTile {

    private final Property property;

    /**
     * Creates a land property tile.
     *
     * @param position the position of the tile on the board
     * @param type the tile type
     * @param name the tile name
     * @param property the property associated with this tile
     */
    public LandPropertyTile(final int position,
                            final TileType type,
                            final String name,
                            final Property property) {
        super(position, type, name);
        this.property = new PropertyImpl(Objects.requireNonNull(property));

    }

    /**
     * Returns the property associated with this tile.
     *
     * @return the property
     */
    public Property getProperty() {
        return new PropertyImpl(this.property);
    }

    /**
     * Returns the property group (color) of this tile.
     *
     * @return the property group
     */
    public PropertyGroup getPropertyColor() {
        return this.property.getCard().getGroup();
    }

    /**
     * Returns the identifier of the property.
     *
     * @return the property id
     */
    public String getPropertyID() {
        return this.property.getId();
    }
}
