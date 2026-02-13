package it.unibo.javapoly.controller.impl;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import it.unibo.javapoly.controller.api.BoardController;
import it.unibo.javapoly.controller.api.CardController;
import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.card.CardDeck;
import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.api.card.payload.BuildingPayload;
import it.unibo.javapoly.model.api.card.payload.CardPayload;
import it.unibo.javapoly.model.api.card.payload.MoneyPayload;
import it.unibo.javapoly.model.api.card.payload.MoveRelativePayload;
import it.unibo.javapoly.model.api.card.payload.MoveToNearestPayload;
import it.unibo.javapoly.model.api.card.payload.MoveToPayload;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.card.CardDeckImpl;
import it.unibo.javapoly.utils.CardLoader;

/**
 * Implementation of the CardController interface.
 * Manages drawing and executing effects of unexpected game cards.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CardControllerImpl implements CardController {

    private static final String BANK_REC = "BANK";
    private static final int VALUE_DEF = -1;
    private static final String PATH_CARD = "src/main/resources/Card/UnexpectedCards.json";

    @JsonIgnore
    private static final Logger LOGGER = Logger.getLogger(CardController.class.getName());


    private final CardDeck cardDeck;

    @JsonIgnore
    private final BoardController boardController;

    @JsonIgnore
    private final PropertyController propertyController;

    @JsonIgnore
    private final EconomyController bank;

    /**
     * Constructs a new CardControllerImpl.
     *
     * @param bank the bank instance for money transactions
     * @param boardController the board controller for movement operations
     * @param propertyController the property controller for property-related actions
     */
    public CardControllerImpl(final EconomyController bank, final BoardController boardController, 
                              final PropertyController propertyController) {
        this.boardController = boardController;
        this.bank = bank;
        this.propertyController = propertyController;

        List<GameCard> cardsList = new ArrayList<>();
        try {
            cardsList = loadCardDeck();
        } catch (final IOException exc) {
            LOGGER.severe("Error loading Cards: " + exc.getMessage());
        }

        this.cardDeck = new CardDeckImpl(cardsList);
    }

        /**
     * Constructs a new CardControllerImpl.
     *
     * @param bank the bank instance for money transactions
     * @param boardController the board controller for movement operations
     * @param propertyController the property controller for property-related actions
     */
    @JsonCreator
    public CardControllerImpl(@JsonProperty("bank") final EconomyController bank, 
                              @JsonProperty("boardController") final BoardController boardController, 
                              @JsonProperty("propertyController") final PropertyController propertyController,
                              @JsonProperty("deck") CardDeck deck) {
        this.boardController = boardController;
        this.bank = bank;
        this.propertyController = propertyController;
                                
        this.cardDeck = deck;
    }

    /**
     * Draws a card for the player.
     *
     * @param playerId the ID of the player drawing the card
     * @return the drawn game card
     */
    @Override
    public GameCard drawCard(final String playerId) {
        return cardDeck.draw(playerId);
    }

    /**
     * Executes the effect of the drawn card.
     * Handles different types of card payloads (money, move, etc.).
     *
     * @param player the player for whom the card effect is executed
     * @param card the drawn game card
     * @param diceRoll the dice roll value
     * @return the position of the tile the player ended up on, or a default value (-1) for non-move cards
     */
    @Override
    public int executeCardEffect(final Player player, final GameCard card, 
                                  final int diceRoll) {
        if (card.isKeepUntilUsed()) {
            return this.VALUE_DEF;
        }

        final CardPayload payload = card.getPayload();

        if (payload instanceof MoneyPayload) {
            return handleMoneyPayload(player, (MoneyPayload) payload);
        } else if (payload instanceof MoveToPayload) {
            return handleMoveToPayload(player, (MoveToPayload) payload, diceRoll);
        } else if (payload instanceof MoveRelativePayload) {
            return handleMoveRelativePayload(player, (MoveRelativePayload) payload, diceRoll);
        } else if (payload instanceof MoveToNearestPayload) {
            return handleMoveToNearestPayload(player, (MoveToNearestPayload) payload, diceRoll);
        } else if (payload instanceof BuildingPayload) {
            return handleMoneyPerBuilding(player, (BuildingPayload) payload);
        }

        return this.VALUE_DEF;
    }

    /**
     * Uses a "Get Out Of Jail Free" card for the player.
     *
     * @param playerId the ID of the player using the card
     * @return true if the card was successfully discarded, false otherwise
     */
    @Override
    public boolean useGetOutOfJailFreeCard(final String playerId) {
        return this.cardDeck.discardByType(CardType.GET_OUT_OF_JAIL_FREE, playerId);
    }

    /**
     * Handles money-related card effects (pay or receive).
     *
     * @param player the player involved in the transaction
     * @param payload the money payload containing the amount and recipient
     * @return a default value (-1) after processing the money transaction
     */
    private int handleMoneyPayload(final Player player, final MoneyPayload payload) {
        if (this.BANK_REC.equals(payload.getReceiverMoney())) {
            this.bank.withdrawFromPlayer(player, payload.getAmount());
            return this.VALUE_DEF;
        }

        this.bank.depositToPlayer(player, payload.getAmount());
        return this.VALUE_DEF;
    }

    /**
     * Handles "move to" card effects (go to specific tile).
     *
     * @param player the player who is moving
     * @param payload the move-to payload containing the target position
     * @param diceRoll the dice roll value
     * @return the position of the tile the player ended up on
     */
    private int handleMoveToPayload(final Player player, 
                                     final MoveToPayload payload,
                                     final int diceRoll) {
        final int position = payload.getTargetPosition();
        final Tile tile = boardController.movePlayerToTile(player, position);
        return this.boardController.executeTileLogic(player, tile.getPosition(), diceRoll).getPosition();
    }

    /**
     * Handles "move relative" card effects (go back/forward N spaces).
     *
     * @param player the player who is moving
     * @param payload the move-relative payload containing the number of steps
     * @param diceRoll the dice roll value
     * @return the position of the tile the player ended up on
     */
    private int handleMoveRelativePayload(final Player player, 
                                           final MoveRelativePayload payload,
                                           final int diceRoll) {
        final int steps = payload.getDelta();
        final Tile tile = boardController.movePlayer(player, steps);
        return this.boardController.executeTileLogic(player, tile.getPosition(), diceRoll).getPosition();
    }

    /**
     * Handles "move to nearest" card effects (go to nearest station/utility).
     *
     * @param player the player who is moving
     * @param payload the move-to-nearest payload
     * @param diceRoll the dice roll value (used for rent calculation)
     * @return the position of the tile the player ended up on
     */
    private int handleMoveToNearestPayload(final Player player, final MoveToNearestPayload payload, 
                                            final int diceRoll) {
        final TileType type = payload.getCategory();
        final Tile tile = boardController.movePlayerToNearestTileOfType(player, type);
        return this.boardController.executeTileLogic(player, tile.getPosition(), diceRoll).getPosition();
    }

    /**
     * Handles "money per building" card effects (earn money for houses/hotels).
     *
     * @param player the player receiving the money
     * @param payload the building payload containing the multiplier values
     * @return the amount of money the player receives
     */
    private int handleMoneyPerBuilding(final Player player, final BuildingPayload payload) {
        final List<Property> list = this.propertyController.getPropertiesWithHouseByOwner(player);

        int amount = 1;
        for (final Property property : list) {
            final int taxHouse = property.getBuiltHouses() * payload.getMoltiplierHouse();
            amount += property.hotelIsBuilt() ? payload.getMoltiplierHotel() : taxHouse;
        }

        return handleMoneyPayload(player, new MoneyPayload(amount, BANK_REC));
    }

    /**
     * Loads the card deck from a file.
     *
     * @return the list of game cards loaded from the file
     * @throws IOException if there is an error reading the file
     */
    private List<GameCard> loadCardDeck() throws IOException {
        try {
            return CardLoader.loadCardsFromFile(this.PATH_CARD);
        } catch (final IOException e) {
            LOGGER.severe("Error loading Cards: " + e.getMessage());
            throw new IOException(e);
        }
    }
}
