package it.unibo.javapoly.model.impl.board.tile;

import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;

public abstract class AbstractTile implements Tile{
    private final int position;
    private final TileType type;
    private final String name;

    protected AbstractTile(final int position, final TileType type, final String name) {
        this.position = position;
        this.type = type;
        this.name = name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public TileType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
