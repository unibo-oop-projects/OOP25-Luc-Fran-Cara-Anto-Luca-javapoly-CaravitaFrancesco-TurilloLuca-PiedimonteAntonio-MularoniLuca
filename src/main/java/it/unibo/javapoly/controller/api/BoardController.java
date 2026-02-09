package it.unibo.javapoly.controller.api;

import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.Player;

/**
 * Controller responsible for managing board operations such as player movement,
 * tile logic execution, and handling the "Go" tile bonus.
 */
public interface BoardController {

    /**
     * Moves a player by a specified number of steps on the board.
     * Handles passing through the "Go" tile and awards the bonus if applicable.
     * 
     * @param player the player to move
     * @param steps the number of steps to move (can be negative for backwards movement)
     * @return the tile where the player landed
     */
    Tile movePlayer(Player player, int steps);

    /**
     * Moves a player directly to a specific tile by its ID.
     * Awards the "Go" bonus if the player passes through or lands on it.
     * 
     * @param player the player to move
     * @param tilePos the position of destination tile
     * @return the destination tile
     */
    Tile movePlayerToTile(Player player, int tilePos);

    /**
     * Moves a player to the nearest tile of a specific type.
     * Used for cards like "Go to nearest station" or "Go to nearest utility".
     * 
     * @param player the player to move
     * @param tileType the type of tile to search for
     * @return the nearest tile of the specified type
     */
    Tile movePlayerToNearestTileOfType(Player player, TileType tileType);

    /**
     * Executes the logic associated with landing on a tile.
     * This includes paying taxes, drawing cards, going to jail, etc.
     * 
     * @param player the player who landed on the tile
     * @param tile the tile the player landed on
     * @param diceRoll the sum of the two dice
     */
    void executeTileLogic(Player player, Tile tile, int diceRoll);

    /**
     * Sends a player directly to jail without passing through "Go".
     * 
     * @param player the player to send to jail
     * 
     * @return the destination tile
     */
    Tile sendPlayerToJail(Player player);

    /**
     * Checks if moving from one position to another passes through the "Go" tile.
     * 
     * @param fromPosition the starting position
     * @param toPosition the destination position
     * @return true if the movement passes through "Go", false otherwise
     */
    boolean passedThroughGo(int fromPosition, int toPosition);
}
