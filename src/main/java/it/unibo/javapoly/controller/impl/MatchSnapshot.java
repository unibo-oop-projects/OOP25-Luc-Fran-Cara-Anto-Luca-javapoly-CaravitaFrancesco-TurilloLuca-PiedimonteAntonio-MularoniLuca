package it.unibo.javapoly.controller.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal data to save and restore a match.
 */
@JsonRootName("matchSnapshot")
public class MatchSnapshot {
    private final List<PlayerSnapshot> players;
    private final List<PropertySnapshot> properties;
    private final int currentPlayerIdx;
    private final int consecutiveDoubles;
    private final boolean hasRolled;
    private final Map<String, Integer> jailTurnCounter;

    /**
     * Constructor for Jackson deserialization and manual creation.
     *
     * @param players the list of player states.
     * @param properties the list of property states.
     * @param currentPlayerIdx the index of the current player.
     * @param consecutiveDoubles number of the consecutive doubles rolled.
     * @param hasRolled whether the current player has already rolled.
     * @param jailTurnCounter map of the players names to their jail turn count.
     */
    @JsonCreator
    public MatchSnapshot(
            @JsonProperty("players") final List<PlayerSnapshot> players,
            @JsonProperty("properties") final List<PropertySnapshot> properties,
            @JsonProperty("currentPlayerIdx") final int currentPlayerIdx,
            @JsonProperty("consecutiveDoubles") final int consecutiveDoubles,
            @JsonProperty("hasRolled") final boolean hasRolled,
            @JsonProperty("jailTurnCounter") final Map<String, Integer> jailTurnCounter
    ) {
        this.players = players != null ? new ArrayList<>(players) : new ArrayList<>();
        this.properties = properties != null ? new ArrayList<>(properties) : new ArrayList<>();
        this.jailTurnCounter = jailTurnCounter != null ? new HashMap<>(jailTurnCounter) : new HashMap<>();
        this.currentPlayerIdx = currentPlayerIdx;
        this.consecutiveDoubles = consecutiveDoubles;
        this.hasRolled = hasRolled;
    }

    /**
     * Get players list.
     *
     * @return players list.
     */
    @JsonProperty("players")
    public List<PlayerSnapshot> getPlayers() {
        return new ArrayList<>(this.players);
    }

    /**
     * Get properties list.
     *
     * @return properties list.
     */
    @JsonProperty("properties")
    public List<PropertySnapshot> getProperties() {
        return new ArrayList<>(this.properties);
    }

    /**
     * Get current player id.
     *
     * @return the index of the current player.
     */
    @JsonProperty("currentPlayerIdx")
    public int getCurrentPlayerIdx() {
        return this.currentPlayerIdx;
    }

    /**
     * Get number of the consecutive doubles rolled.
     *
     * @return number of the consecutive doubles rolled.
     */
    @JsonProperty("consecutiveDoubles")
    public int getConsecutiveDoubles() {
        return this.consecutiveDoubles;
    }

    /**
     * Get whether the current player has already rolled.
     *
     * @return whether the current player has already rolled.
     */
    @JsonProperty("hasRolled")
    public boolean hasRolled() {
        return this.hasRolled;
    }

    /**
     * Get map of the players names to their jail turn count.
     *
     * @return map of the players names to their jail turn count.
     */
    @JsonProperty("jailTurnCounter")
    public Map<String, Integer> getJailTurnCounter() {
        return new HashMap<>(this.jailTurnCounter);
    }
}
