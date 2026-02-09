package it.unibo.javapoly.controller.impl;

import java.io.IOException;
import java.util.logging.Logger;
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
import it.unibo.javapoly.model.api.player.Player;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.card.CardDeckImpl;
import it.unibo.javapoly.utils.CardLoader;

/**
 * Implementation of the CardController interface.
 * Manages drawing and executing effects of unexpected game cards.
 */
public class CardControllerImpl implements CardController {

    private static final String BANK_REC = "BANK";
    private static final String PATH_CARD = "src/main/resources/Card/UnexpectedCards.json";

    private static final Logger LOGGER = Logger.getLogger(CardController.class.getName());

    private final CardDeck cardDeck;
    private final BoardController boardController;
    private final PropertyController propertyController;
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
     * {@inheritDoc}
     */
    @Override
    public GameCard drawCard(final String playerId) {
        return cardDeck.draw(playerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeCardEffect(final Player player, final GameCard card, 
                                  final int diceRoll) {
        if (card.isKeepUntilUsed()) {
            return;
        }

        final CardPayload payload = card.getPayload();

        if (payload instanceof MoneyPayload) {
            handleMoneyPayload(player, (MoneyPayload) payload);
        } else if (payload instanceof MoveToPayload) {
            handleMoveToPayload(player, (MoveToPayload) payload, diceRoll);
        } else if (payload instanceof MoveRelativePayload) {
            handleMoveRelativePayload(player, (MoveRelativePayload) payload, diceRoll);
        } else if (payload instanceof MoveToNearestPayload) {
            handleMoveToNearestPayload(player, (MoveToNearestPayload) payload, diceRoll);
        } else if (payload instanceof BuildingPayload) {
            handleMoneyPerBuilding(player, (BuildingPayload) payload);
        }

        if (CardType.GO_TO_JAIL == card.getType() && !useGetOutOfJailFreeCard(player.getName())) {
            boardController.sendPlayerToJail(player);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean useGetOutOfJailFreeCard(final String playerId) {
        return this.cardDeck.discardByType(CardType.GET_OUT_OF_JAIL_FREE, playerId);
    }

    /**
     * Handles money-related card effects (pay or receive).
     *
     * @param player the player
     * @param payload the money payload
     */
    private void handleMoneyPayload(final Player player, final MoneyPayload payload) {
        if (this.BANK_REC.equals(payload.getReceiverMoney())) {
            this.bank.withdrawFromPlayer(player, payload.getAmount());
            return;
        }

        this.bank.depositToPlayer(player, payload.getAmount());
    }

    /**
     * Handles "move to" card effects (go to specific tile).
     *
     * @param player the player
     * @param payload the move-to payload
     * @param diceRoll the dice roll value
     */
    private void handleMoveToPayload(final Player player, 
                                     final MoveToPayload payload,
                                     final int diceRoll) {
        final int position = payload.getTargetPosition();
        final Tile tile = boardController.movePlayerToTile(player, position);
        this.boardController.executeTileLogic(player, tile, diceRoll);
    }

    /**
     * Handles "move relative" card effects (go back/forward N spaces).
     *
     * @param player the player
     * @param payload the move-relative payload
     * @param diceRoll the dice roll value
     */
    private void handleMoveRelativePayload(final Player player, 
                                           final MoveRelativePayload payload,
                                           final int diceRoll) {
        final int steps = payload.getDelta();
        final Tile tile = boardController.movePlayer(player, steps);
        this.boardController.executeTileLogic(player, tile, diceRoll);
    }

    /**
     * Handles "move to nearest" card effects (go to nearest station/utility).
     *
     * @param player the player
     * @param payload the move-to-nearest payload
     * @param diceRoll the dice roll value (for utility rent calculation)
     */
    private void handleMoveToNearestPayload(final Player player, final MoveToNearestPayload payload, 
                                            final int diceRoll) {
        final TileType type = payload.getCategory();
        final Tile tile = boardController.movePlayerToNearestTileOfType(player, type);
        this.boardController.executeTileLogic(player, tile, diceRoll);
    }

    /**
     * Handles "money per building" card effects (earn money for houses/hotels).
     *
     * @param player the player
     * @param payload the building payload
     */
    private void handleMoneyPerBuilding(final Player player, final BuildingPayload payload) {
        final List<Property> list = this.propertyController.getPropertiesWithHouseByOwner(player);

        int amount = 0;
        for (final Property property : list) {
            final int taxHouse = property.getBuiltHouses() * payload.getMoltiplierHouse();
            amount += property.hotelIsBuilt() ? payload.getMoltiplierHotel() : taxHouse;
        }

        handleMoneyPayload(player, new MoneyPayload(amount, BANK_REC));
    }

    private List<GameCard> loadCardDeck() throws IOException {
        try {
            return CardLoader.loadCardsFromFile(this.PATH_CARD);
        } catch (final IOException e) {
            LOGGER.severe("Error loading Cards: " + e.getMessage());
            throw new IOException(e);
        }
    }
}
