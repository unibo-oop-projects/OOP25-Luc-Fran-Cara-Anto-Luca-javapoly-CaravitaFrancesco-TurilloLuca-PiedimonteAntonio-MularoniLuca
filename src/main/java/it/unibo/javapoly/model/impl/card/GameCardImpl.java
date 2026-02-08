package it.unibo.javapoly.model.impl.card;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.api.card.payload.CardPayload;

/**
 * Implementation of the {@link GameCard} interface.
 * This class represents a game card with various properties such as:
 * ID, name, description, type, payload, and whether it is kept until used.
 */
@JsonRootName("GameCard")
public class GameCardImpl implements GameCard {

    private final String id;
    private final String name;
    private final String description;
    private final CardType type;
    private final CardPayload payload;
    private final boolean keepUntilUsed;

    /**
     * Constructs a new {@link GameCardImpl} with the specified parameters.
     * 
     * @param id the ID of the card
     * @param name the name of the card
     * @param description a description of the card
     * @param type the type of the card (e.g., MOVE_TO, PAY_BANK)
     * @param payload the data associated with the card
     * @param keepUntilUsed whether the card should be kept by the player until used (e.g., Get Out of Jail Free)
     */
    @JsonCreator
    public GameCardImpl(@JsonProperty("id") final String id,
                        @JsonProperty("name") final String name,
                        @JsonProperty("description") final String description,
                        @JsonProperty("type") final CardType type,
                        @JsonProperty("payload") final CardPayload payload,
                        @JsonProperty("keepUntilUsed") final boolean keepUntilUsed) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = description == null ? "" : description;
        this.type = Objects.requireNonNull(type);
        this.payload = payload;
        this.keepUntilUsed = keepUntilUsed;
    }

    //#region Getter
    /**
     * Returns the ID of the game card.
     * 
     * @return the ID of the game card
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Returns the name of the game card.
     * 
     * @return the name of the game card
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns the description of the game card.
     * 
     * @return the description of the game card
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the type of the game card.
     * 
     * @return the type of the game card (e.g., MOVE_TO, PAY_BANK)
     */
    @Override
    public CardType getType() {
        return this.type;
    }

    /**
     * Returns the data payload of the game card.
     * 
     * @return a map containing the data associated with the game card
     */
    @Override
    public CardPayload getPayload() {
        return this.payload;
    }

    /**
     * Returns whether the game card should be kept until used.
     * 
     * @return {@code true} if the card should be kept until used, {@code false} otherwise
     */
    @Override
    public boolean isKeepUntilUsed() {
        return this.keepUntilUsed;
    }
    //#endregion

    /**
     * Returns a string representation of the game card.
     * 
     * @return a string representation of the game card
     */
    @Override
    public String toString() {
        return "GameCardImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", payload=" + payload +
                ", keepUntilUsed=" + keepUntilUsed +
                '}';
    }

}
