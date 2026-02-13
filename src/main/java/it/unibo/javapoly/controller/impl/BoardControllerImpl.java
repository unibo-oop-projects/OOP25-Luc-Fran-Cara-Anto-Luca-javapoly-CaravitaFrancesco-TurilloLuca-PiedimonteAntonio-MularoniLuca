package it.unibo.javapoly.controller.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import it.unibo.javapoly.model.impl.JailedState;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.model.impl.board.tile.TaxTile;
import it.unibo.javapoly.model.impl.card.StationPropertyCard;
import it.unibo.javapoly.model.impl.card.UtilityPropertyCard;

/**
 * Implementation of the BoardController interface.
 * Manages player movement on the board, handling "Go" bonuses,
 * tile logic execution, and special movements (to jail, to nearest tile, etc.).
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BoardControllerImpl implements BoardController {

    private static final int MAX_DICE = 12;
    private static final int GO_BONUS = 200;
    private static final int BOARD_SIZE = 40;
    private static final int JAIL_POSITION = 10;

    private static final String JAIL_FREE =
        "Hai usato una carta esci di prigione gratis.";

    @JsonIgnore
    private final Board board;

    @JsonIgnore
    private final PropertyController propertyController;

    @JsonIgnore
    private final EconomyController bank;

    private final CardController cardController;

    @JsonIgnore
    private String message;

    /**
     * Constructs a new BoardControllerImpl.
     *
     * @param board the game board
     * @param bank the bank instance for handling transactions
     * @param propertyController the property controller for handling tile properties
     */
    public BoardControllerImpl(
            final Board board,
            final PropertyController propertyController) {

        this.board = board;
        this.bank = new EconomyControllerImpl(propertyController);
        this.propertyController = propertyController;
        this.cardController = new CardControllerImpl(this, this.propertyController);
        this.message = "";
    }

    /**
     * Constructs a new BoardControllerImpl.
     *
     * @param board the game board
     * @param bank the bank instance for handling transactions
     * @param propertyController the property controller for handling tile properties
     * @param cardController the card controller for handling cards
     */
    @JsonCreator
    public BoardControllerImpl(
            @JsonProperty("board") final Board board,
            @JsonProperty("propertyController") final PropertyController propertyController,
            @JsonProperty("cardController") final CardController cardController) {

        this.board = board;
        this.bank = new EconomyControllerImpl(propertyController);
        this.propertyController = propertyController;
        this.cardController = cardController;
        this.message = "";
    }

    /**
     * Moves a player by the specified number of steps.
     * If the player passes through "Go", they are awarded the bonus.
     *
     * @param player the player to move
     * @param steps the number of steps to move
     * @return the tile the player landed on
     */
    @Override
    public Tile movePlayer(final Player player, final int steps) {
        final int currentPos = player.getCurrentPosition();
        final int newPos =
            this.board.normalizePosition(currentPos + steps);

        if (passedThroughGo(currentPos, newPos)) {
            awardGoBonus(player);
        }

        return this.board.getTileAt(newPos);
    }

    /**
     * Moves a player directly to a specified tile.
     * If the player passes through "Go", they are awarded the bonus.
     *
     * @param player the player to move
     * @param targetPos the position of the target tile
     * @return the tile the player landed on
     */
    @Override
    public Tile movePlayerToTile(
            final Player player,
            final int targetPos) {

        if (targetPos == -1) {
            throw new IllegalArgumentException(
                "Tile with pos " + targetPos + " not found"
            );
        }

        final int currentPos = player.getCurrentPosition();

        if (passedThroughGo(currentPos, targetPos)
                && targetPos + BOARD_SIZE - currentPos < MAX_DICE
                && targetPos != JAIL_POSITION) {

            awardGoBonus(player);
        }

        return this.board.getTileAt(targetPos);
    }

    /**
     * Moves a player to the nearest tile of a specified type.
     * If the player passes through "Go", they are awarded the bonus.
     *
     * @param player the player to move
     * @param tileType the type of tile to move to
     * @return the tile the player landed on
     */
    @Override
    public Tile movePlayerToNearestTileOfType(
            final Player player,
            final TileType tileType) {

        final int currentPos = player.getCurrentPosition();
        final int nearestPos =
            findNearestTileOfType(currentPos, tileType);

        if (nearestPos == -1) {
            throw new IllegalArgumentException(
                "No tile of type " + tileType + " found"
            );
        }

        if (passedThroughGo(currentPos, nearestPos)) {
            awardGoBonus(player);
        }

        return this.board.getTileAt(nearestPos);
    }

    /**
     * Executes the logic associated with the tile the player lands on.
     * This includes actions like paying rent, going to jail, drawing cards, etc.
     *
     * @param player the player whose tile logic is to be executed
     * @param pos the position of the tile
     * @param diceRoll the result of the dice roll that moved the player
     * @return the tile the player landed on after executing the logic
     */
    @Override
    public Tile executeTileLogic(
            final Player player,
            final int pos,
            final int diceRoll) {

        final Tile tile = this.board.getTileAt(pos);

        this.message += tile.getName()
            + System.lineSeparator()
            + tile.getDescription()
            + System.lineSeparator();

        switch (tile.getType()) {

            case TAX:
                if (tile instanceof TaxTile) {
                    final TaxTile tax = (TaxTile) tile;
                    this.bank.withdrawFromPlayer(
                        player,
                        tax.getAmountTax()
                    );
                }
                break;

            case GO_TO_JAIL:
                if (!this.cardController
                        .useGetOutOfJailFreeCard(player.getName())) {
                    return sendPlayerToJail(player);
                }
                this.message += JAIL_FREE
                    + System.lineSeparator();
                break;

            case UNEXPECTED:
                final GameCard cardDrawn =
                    this.cardController.drawCard(player.getName());

                this.message += cardDrawn.getName()
                    + System.lineSeparator();

                if (CardType.GO_TO_JAIL == cardDrawn.getType()) {
                    if (!this.cardController
                            .useGetOutOfJailFreeCard(player.getName())) {
                        return sendPlayerToJail(player);
                    }

                    this.message += JAIL_FREE
                        + System.lineSeparator();
                    return tile;
                }

                final int destPos =
                    this.cardController.executeCardEffect(
                        player,
                        cardDrawn,
                        BOARD_SIZE
                    );

                return destPos != -1
                    ? this.board.getTileAt(destPos)
                    : tile;

            case PROPERTY:
            case RAILROAD:
            case UTILITY:
                if (tile instanceof PropertyTile) {
                    final PropertyTile prop =
                        (PropertyTile) tile;

                    if (this.propertyController
                            .checkPayRent(
                                player,
                                prop.getPropertyID())) {

                        this.bank.payRent(
                            player,
                            this.propertyController
                                .getOwnerByProperty(
                                    prop.getProperty()
                                ),
                            prop.getProperty(),
                            diceRoll
                        );

                        this.message +=
                            "Questa non è la tua Proprietà, paga l'affitto"
                            + System.lineSeparator();
                    }
                }
                break;

            default:
                break;
        }

        return tile;
    }

    /**
     * Sends the player to jail and updates their state.
     * 
     * @param player the player to send to jail
     * @return the jail tile
     */
    @Override
    public Tile sendPlayerToJail(final Player player) {
        player.setState(new JailedState());
        return movePlayerToTile(player, JAIL_POSITION);
    }

    /**
     * Gets the printed message generated from player actions.
     * Clears the internal message buffer after retrieval.
     *
     * @return the current message
     */
    @JsonIgnore
    @Override
    public String getMessagePrint() {
        final String tmp = this.message;
        this.message = "";
        return tmp;
    }

    /**
     * Checks if a player has passed through the "Go" position.
     *
     * @param fromPosition the starting position
     * @param toPosition the ending position
     * @return true if the player passed through "Go", false otherwise
     */
    @Override
    public boolean passedThroughGo(
            final int fromPosition,
            final int toPosition) {

        return toPosition < fromPosition;
    }

    /**
     * Awards the "Go" bonus to the specified player.
     * Deposits the bonus amount into the player's balance
     * and updates the internal message.
     *
     * @param player the player receiving the bonus
     */
    private void awardGoBonus(final Player player) {
        this.message +=
            "Siete passati dal via, ritirate 200"
            + System.lineSeparator();

        this.bank.depositToPlayer(player, GO_BONUS);
    }

    /**
     * Finds the nearest tile of the specified type,
     * searching clockwise from the given starting position.
     *
     * @param startPos the starting position
     * @param tileType the type of tile to search for
     * @return the position of the nearest matching tile,
     *         or -1 if no such tile exists
     */
    private int findNearestTileOfType(
            final int startPos,
            final TileType tileType) {

        for (int offset = 1; offset < BOARD_SIZE; offset++) {

            final int pos =
                this.board.normalizePosition(startPos + offset);

            final Tile tile = this.board.getTileAt(pos);

            if (tile instanceof PropertyTile pt) {

                final var card =
                    pt.getProperty().getCard();

                if (tileType == TileType.RAILROAD
                        && card instanceof StationPropertyCard) {
                    return pos;
                }

                if (tileType == TileType.UTILITY
                        && card instanceof UtilityPropertyCard) {
                    return pos;
                }
            }
        }

        return -1;
    }
}
