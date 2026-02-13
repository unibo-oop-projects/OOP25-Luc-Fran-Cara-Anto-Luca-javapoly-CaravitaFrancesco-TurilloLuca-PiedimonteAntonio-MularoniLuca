package it.unibo.javapoly.controller.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.unibo.javapoly.controller.impl.CardControllerImpl;
import it.unibo.javapoly.controller.impl.PropertyControllerImpl;
import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.api.Player;

/**
 * Controller responsible for managing game cards (Chance/Community Chest).
 * Handles drawing cards and executing their effects on players.
 */
@JsonDeserialize(as = CardControllerImpl.class)
public interface CardController {

    /**
     * Draws a card from the deck for the specified player.
     * 
     * @param playerId the ID of the player drawing the card
     * @return the drawn GameCard
     */
    GameCard drawCard(String playerId);

    /**
     * Executes the effect of a game card on the player.
     * This method handles all card types: money transfers, movement, jail, etc.
     * 
     * @param player the player affected by the card
     * @param card the GameCard to execute
     * @param diceRoll the current dice roll value (used for utility rent calculations)
     * @return the position
     */
    int executeCardEffect(Player player, GameCard card, int diceRoll);

    /**
     * Uses a "Get Out of Jail Free" card held by a player.
     * 
     * @param playerId the ID of the player using the card
     * @return true if the card was successfully used, false if the player doesn't have one
     */
    boolean useGetOutOfJailFreeCard(String playerId);

}
