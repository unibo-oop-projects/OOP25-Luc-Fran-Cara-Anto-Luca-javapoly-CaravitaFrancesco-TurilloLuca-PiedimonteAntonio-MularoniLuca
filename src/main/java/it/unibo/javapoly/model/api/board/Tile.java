package it.unibo.javapoly.model.api.board;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.unibo.javapoly.model.impl.board.tile.FreeParkingTile;
import it.unibo.javapoly.model.impl.board.tile.GoToJailTile;
import it.unibo.javapoly.model.impl.board.tile.JailTile;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.model.impl.board.tile.StartTile;
import it.unibo.javapoly.model.impl.board.tile.TaxTile;
import it.unibo.javapoly.model.impl.board.tile.UnexpectedTile;

/**
 * Represents a tile on the board.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = StartTile.class, name = "START"),
    @JsonSubTypes.Type(value = PropertyTile.class, name = "PROPERTY"),
    @JsonSubTypes.Type(value = PropertyTile.class, name = "RAILROAD"),
    @JsonSubTypes.Type(value = PropertyTile.class, name = "UTILITY"),
    @JsonSubTypes.Type(value = TaxTile.class, name = "TAX"),
    @JsonSubTypes.Type(value = JailTile.class, name = "JAIL"),
    @JsonSubTypes.Type(value = FreeParkingTile.class, name = "FREE_PARKING"),
    @JsonSubTypes.Type(value = GoToJailTile.class, name = "GO_TO_JAIL"),
    @JsonSubTypes.Type(value = UnexpectedTile.class, name = "UNEXPECTED")
})
public interface Tile {

    /**
     * Returns the position of the tile on the board.
     *
     * @return the tile position
     */
    int getPosition();

    /**
     * Returns the type of the tile.
     *
     * @return the tile type
     */
    TileType getType();

    /**
     * Returns the name of the tile.
     *
     * @return the tile name
     */
    String getName();
}
