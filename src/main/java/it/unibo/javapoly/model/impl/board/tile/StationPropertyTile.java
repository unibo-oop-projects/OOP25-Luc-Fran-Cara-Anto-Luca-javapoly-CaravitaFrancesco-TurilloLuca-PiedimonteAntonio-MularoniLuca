package it.unibo.javapoly.model.impl.board.tile;

import java.util.Objects;

import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.property.PropertyImpl;

/**
 * Represents a tile associated with a station property.
 */
public final class StationPropertyTile extends AbstractTile {

    private final Property property;

    /**
     * Creates a station property tile.
     *
     * @param position the position of the tile on the board
     * @param type the tile type
     * @param name the tile name
     * @param property the station property associated with this tile
     */
    public StationPropertyTile(final int position,
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
     * Returns the property group of the station.
     *
     * @return the property group
     */
    public PropertyGroup getPropertyGroup() {
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
