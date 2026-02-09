package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.BoardController;
import it.unibo.javapoly.controller.api.CardController;
import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.player.Player;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.model.impl.board.tile.TaxTile;
import it.unibo.javapoly.model.impl.player.JailedState;

/**
 * Implementation of the BoardController interface.
 * Manages player movement on the board, handling "Go" bonuses, tile logic execution,
 * and special movements (to jail, to nearest tile, etc.).
 */
public class BoardControllerImpl implements BoardController {

    private static final int MAX_DICE = 12;
    private static final int GO_BONUS = 200;
    private static final int BOARD_SIZE = 40;
    private static final int JAIL_POSITION = 10;

    private final Board board;

    private final PropertyController PropertyController;
    private final EconomyController bank;
    private final CardController CardController;

    /**
     * Constructs a new BoardControllerImpl.
     *
     * @param board the game board
     * @param bank the bank instance for handling transactions
     * @param pc the property controller for handling tile property
     * 
     */
    public BoardControllerImpl(final Board board, 
                               final EconomyController bank,
                               final PropertyController pc) {
        this.board = board;
        this.bank = bank;

        this.PropertyController = pc;
        this.CardController = new CardControllerImpl(bank, this, this.PropertyController);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile movePlayer(final Player player, final int steps) {
        final int currentPos = player.getCurrentPosition();
        final int newPos = this.board.normalizePosition(steps);

        if (passedThroughGo(currentPos, newPos)) {
            awardGoBonus(player);
        }

        return board.getTileAt(newPos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile movePlayerToTile(final Player player, int targetPos) {
        final int currentPos = player.getCurrentPosition();

        if (targetPos == -1) {
            throw new IllegalArgumentException("Tile with pos " + targetPos + " not found");
        }

        /* It also check if the steps is > 12 and destination is JAIL_POSITION, 
        this means that the player is going to jail as a prisoner */
        if (passedThroughGo(currentPos, targetPos) && 
           (targetPos + (this.BOARD_SIZE- player.getCurrentPosition()) < this.MAX_DICE) &&
           (targetPos != this.JAIL_POSITION)) {
            awardGoBonus(player);
        }

        return board.getTileAt(targetPos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile movePlayerToNearestTileOfType(final Player player, TileType tileType) {
        final int currentPos = player.getCurrentPosition();
        final int nearestPos = findNearestTileOfType(currentPos, tileType);

        if (nearestPos == -1) {
            throw new IllegalArgumentException("No tile of type " + tileType + " found");
        }

        if (passedThroughGo(currentPos, nearestPos)) {
            awardGoBonus(player);
        }

        return board.getTileAt(nearestPos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeTileLogic(final Player player, final Tile tile, final int diceRoll) {

        switch (tile.getType()) {
            case TAX:
                if (tile instanceof TaxTile){
                    final TaxTile tax = (TaxTile) tile;
                    this.bank.withdrawFromPlayer(player, tax.getAmountTax());
                }
                break;
            case GO_TO_JAIL:
                sendPlayerToJail(player);
                return;
            case UNEXPECTED:
                this.CardController.executeCardEffect(player, this.CardController.drawCard(player.getName()), BOARD_SIZE);;
                break;
            case PROPERTY:
            case RAILROAD:
            case UTILITY:
                if (tile instanceof PropertyTile){
                    PropertyTile prop = (PropertyTile) tile;

                    if (this.PropertyController.checkPayRent(player, prop.getPropertyID())){
                        this.bank.payRent(player.getName(), prop.getProperty().getIdOwner() , diceRoll);
                    }
                }
            
                break;
            // case START:
            // case FREE_PARKING:
            // case JAIL:
                // break;
            default:
                break;
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile sendPlayerToJail(final Player player) {
        player.setState(new JailedState());
        return movePlayerToTile(player, this.JAIL_POSITION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean passedThroughGo(final int fromPosition, final int toPosition) {
        return toPosition < fromPosition;
    }

    /**
     * Awards the "Go" bonus to a player.
     * Uses the bank to transfer money.
     *
     * @param playerId the ID of the player receiving the bonus
     */
    private void awardGoBonus(final Player player) {
        this.bank.depositToPlayer(player, this.GO_BONUS);
        System.out.println("Player " + player + " passed GO! Receives " + GO_BONUS); // NOPMD
    }

    /**
     * Finds the nearest tile of a specific type starting from a position.
     * Searches clockwise around the board.
     *
     * @param startPos the starting position
     * @param tileType the type of tile to search for
     * @return the position of the nearest tile, or -1 if not found
     */
    private int findNearestTileOfType(final int startPos, final TileType tileType) {
        for (int offset = 1; offset < BOARD_SIZE; offset++) {
            final int pos = this.board.normalizePosition(startPos + offset);
            final Tile tile = board.getTileAt(pos);
            
            if (matchesTileType(tile, tileType)) {
                return pos;
            }
        }
        return -1;
    }

    /**
     * Checks if a tile matches the specified type.
     *
     * @param tile the tile to check
     * @param tileType the type to match against
     * @return true if the tile matches the type
     */
    private boolean matchesTileType(final Tile tile, final TileType tileType) {
        // Gestisce sia TileType enum che stringhe come "STATION", "UTILITY"
        
        if (tile.getType() == TileType.RAILROAD && tileType.equals(tile.getType())) {
            return true;
        }
        if (tile.getType() == TileType.UTILITY && tileType.equals(tile.getType())) {
            return true;
        }
        
        return tile.getType().equals(tileType);
    }
}
