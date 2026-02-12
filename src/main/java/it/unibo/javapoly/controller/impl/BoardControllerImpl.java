package it.unibo.javapoly.controller.impl;

import it.unibo.javapoly.controller.api.BoardController;
import it.unibo.javapoly.controller.api.CardController;
import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.model.impl.board.tile.TaxTile;
import it.unibo.javapoly.model.impl.card.StationPropertyCard;
import it.unibo.javapoly.model.impl.card.UtilityPropertyCard;
import it.unibo.javapoly.model.impl.JailedState;

/**
 * Implementation of the BoardController interface.
 * Manages player movement on the board, handling "Go" bonuses, tile logic execution,
 * and special movements (to jail, to nearest tile, etc.).
 */
public class BoardControllerImpl implements BoardController {

    private static final int MAX_DICE = 12;
    private static final int GO_BONUS = 200;
    private static final int BOARD_SIZE = 40;
    private static final String JAIL_FREE = "Hai usato una carta esci di prigione gratis.";
    private static final int JAIL_POSITION = 10;

    private final Board board;
    private final PropertyController propertyController;
    private final EconomyController bank;
    private final CardController cardController;

    private String message;

    /**
     * Constructs a new BoardControllerImpl.
     *
     * @param board the game board
     * @param bank the bank instance for handling transactions
     * @param propertyController the property controller for handling tile properties
     */
    public BoardControllerImpl(final Board board, 
                               final EconomyController bank,
                               final PropertyController propertyController) {
        this.board = board;
        this.bank = bank;
        this.propertyController = propertyController;
        this.cardController = new CardControllerImpl(bank, this, this.propertyController);
        this.message = "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile movePlayer(final Player player, final int steps) {
        final int currentPos = player.getCurrentPosition();
        final int newPos = this.board.normalizePosition(currentPos+steps);

        if (passedThroughGo(currentPos, newPos)) {
            awardGoBonus(player);
        }

        return board.getTileAt(newPos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile movePlayerToTile(final Player player, final int targetPos) {
        if (targetPos == -1) {
            throw new IllegalArgumentException("Tile with pos " + targetPos + " not found");
        }

        final int currentPos = player.getCurrentPosition();

        if (passedThroughGo(currentPos, targetPos) 
           && targetPos + this.BOARD_SIZE - currentPos < this.MAX_DICE
           && targetPos != this.JAIL_POSITION) {
            awardGoBonus(player);
        }

        return board.getTileAt(targetPos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile movePlayerToNearestTileOfType(final Player player, final TileType tileType) {
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
    public Tile executeTileLogic(final Player player, final int pos, final int diceRoll) {
        final Tile tile = this.board.getTileAt(pos);
        this.message += tile.getName() + "\n" + tile.getDescription() + "\n";

        switch (tile.getType()) {
            case TAX:
                if (tile instanceof TaxTile) {
                    final TaxTile tax = (TaxTile) tile;
                    this.bank.withdrawFromPlayer(player, tax.getAmountTax());
                }
                break;
            case GO_TO_JAIL:
                if (!this.cardController.useGetOutOfJailFreeCard(player.getName())){
                    return sendPlayerToJail(player);
                }
                this.message += this.JAIL_FREE + "\n";

            case UNEXPECTED:
                final GameCard cardDrawed = this.cardController.drawCard(player.getName());

                this.message += cardDrawed.getName() + "\n";

                if (CardType.GO_TO_JAIL == cardDrawed.getType()){
                    if (!this.cardController.useGetOutOfJailFreeCard(player.getName())){
                        return sendPlayerToJail(player);
                    }
                    this.message += this.JAIL_FREE + "\n";
                    return tile;
                }

                final int destPos = this.cardController.executeCardEffect(player, cardDrawed, BOARD_SIZE);
                return destPos != -1 ? this.board.getTileAt(destPos) : tile;
            case PROPERTY:
            case RAILROAD:
            case UTILITY:
                if (tile instanceof PropertyTile) {
                    final PropertyTile prop = (PropertyTile) tile;

                    if (this.propertyController.checkPayRent(player, prop.getPropertyID())) {
                        this.bank.payRent(player, this.propertyController.getOwnerByProperty(prop.getProperty()), prop.getProperty(), diceRoll);
                        this.message += "Questa non è la tua Proprietà, paga l'affitto" + "\n";
                    }
                }
                break;
            default:
                break;
        }


        return tile;
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
    public String getMessagePrint() {
        final String tmp = this.message;
        this.message = ""; 
        return tmp;
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
     * @param player the player receiving the bonus
     */
    private void awardGoBonus(final Player player) {
        this.message += "Siete passati dal via, ritirate 200" + "\n"; 
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
            /*
            if (tile.getType() == tileType 
                && (tileType == tileType.RAILROAD || tileType == tileType.UTILITY )) {
                return pos;
            } */
            
            if(tile instanceof PropertyTile pt){
                final var card = pt.getProperty().getCard();
                if(tileType == tileType.RAILROAD && card instanceof StationPropertyCard){
                    return pos;
                }
                if(tileType == tileType.UTILITY && card instanceof UtilityPropertyCard){
                    return pos;
                }
            }
        }
        return -1;
    }

}
