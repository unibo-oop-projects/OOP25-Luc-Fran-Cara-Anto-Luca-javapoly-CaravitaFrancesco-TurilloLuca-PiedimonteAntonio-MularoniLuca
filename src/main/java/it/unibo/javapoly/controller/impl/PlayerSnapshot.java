package it.unibo.javapoly.controller.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.utils.ValidationUtils;

/**
 * Data Transfer Object that represents a player's state for serialization.
 * Contains information about a player.
 */
public class PlayerSnapshot {
    private final String name;
    private final int balance;
    private final int position;
    private final TokenType tokenType;
    private final String playerState;
    private final Integer turnsInJail;

    /**
     * Constructor for Jackson deserialization.
     *
     * @param name the player's name.
     * @param balance the player's current balance.
     * @param position the player's position on the board.
     * @param tokenType the type of token.
     * @param playerState the current state class name.
     * @param turnsInJail number of turn in jail.
     */
    @JsonCreator
    public PlayerSnapshot(
            @JsonProperty("name") final String name,
            @JsonProperty("balance") final int balance,
            @JsonProperty("position") final int position,
            @JsonProperty("tokenType") final TokenType tokenType,
            @JsonProperty("playerState") final String playerState,
            @JsonProperty("turnsInJail") final Integer turnsInJail
    ) {
        this.name = ValidationUtils.requireNonBlank(name, "Player name cannot be blank");
        this.balance = balance;
        this.position = position;
        this.tokenType = ValidationUtils.requireNonNull(tokenType, "Token type cannot be null");
        this.playerState = ValidationUtils.requireNonNull(playerState, "Player state class name cannot  be null");
        this.turnsInJail = turnsInJail;
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name.
     */
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    /**
     * Gets the player's current balance.
     *
     * @return the player's current balance.
     */
    @JsonProperty("balance")
    public int getBalance() {
        return this.balance;
    }

    /**
     * Gets the player's position on the board.
     *
     * @return the player's position on the board.
     */
    @JsonProperty("position")
    public int getPosition() {
        return this.position;
    }

    /**
     * Gets the type of token.
     *
     * @return the type of token.
     */
    @JsonProperty("tokenType")
    public TokenType getTokenType() {
        return this.tokenType;
    }

    /**
     * Gets the current state class name.
     *
     * @return the current state class name.
     */
    @JsonProperty("playerState")
    public String getPlayerStateName() {
        return this.playerState;
    }

    /**
     * Gets the current state class name.
     *
     * @return the current state class name.
     */
    @JsonProperty("turnsInJail")
    public Integer getPlayerInJail() {
        return this.turnsInJail;
    }
}
