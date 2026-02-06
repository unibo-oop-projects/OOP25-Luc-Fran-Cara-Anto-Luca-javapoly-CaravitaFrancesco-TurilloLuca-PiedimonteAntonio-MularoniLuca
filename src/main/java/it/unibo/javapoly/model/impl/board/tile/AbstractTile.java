package it.unibo.javapoly.model.impl.board.tile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;

/**
 * Abstract base implementation of a board tile.
 * 
 * <p>
 * This class provides common state and behavior for all tile implementations.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = StartTile.class, name = "start"),
    @JsonSubTypes.Type(value = LandPropertyTile.class, name = "land_property"),
    @JsonSubTypes.Type(value = StationPropertyTile.class, name = "station_property"),
    @JsonSubTypes.Type(value = UtilityPropertyTile.class, name = "utility_property"),
    @JsonSubTypes.Type(value = TaxTile.class, name = "tax"),
    @JsonSubTypes.Type(value = JailTile.class, name = "jail"),
    @JsonSubTypes.Type(value = FreeParkingTile.class, name = "free_parking"),
    @JsonSubTypes.Type(value = GoToJailTile.class, name = "go_to_jail"),
    @JsonSubTypes.Type(value = UnexpectedTile.class, name = "unexpected")
})
public abstract class AbstractTile implements Tile {

    private final int position;
    private final TileType type;
    private final String name;

    /**
     * Creates a tile with the given position, type and name.
     *
     * @param position the position of the tile on the board
     * @param type the tile type
     * @param name the tile name
     */
    protected AbstractTile(final int position, final TileType type, final String name) {
        this.position = position;
        this.type = type;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
}
