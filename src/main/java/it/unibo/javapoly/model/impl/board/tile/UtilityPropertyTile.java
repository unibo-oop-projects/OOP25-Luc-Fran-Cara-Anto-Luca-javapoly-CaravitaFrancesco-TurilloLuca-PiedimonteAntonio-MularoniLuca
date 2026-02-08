package it.unibo.javapoly.model.impl.board.tile;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.property.PropertyImpl;

/**
 * Represents a tile associated with a utility property.
 */
@JsonRootName("UtilityPropertyTile")
public final class UtilityPropertyTile extends AbstractTile {

    private final Property property;

    /**
     * Creates a utility property tile.
     *
     * @param position the position of the tile on the board
     * @param name the tile name
     * @param property the utility property associated with this tile
     */
    @JsonCreator
    public UtilityPropertyTile(
            @JsonProperty("position") final int position,
            @JsonProperty("name") final String name,
            @JsonProperty("property") final Property property) {
        super(position, TileType.UTILITY, name);
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
     * Returns the property group of the utility.
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
