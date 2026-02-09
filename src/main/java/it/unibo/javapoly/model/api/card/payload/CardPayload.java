package it.unibo.javapoly.model.api.card.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Marker interface for typed payloads associated with cards.
 * Concrete implementations contain the fields useful for that card type.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoneyPayload.class, name = "PayLoadMoney"),
    @JsonSubTypes.Type(value = MoveRelativePayload.class, name = "PayLoadMoveRelative"),
    @JsonSubTypes.Type(value = MoveToPayload.class, name = "PayLoadMoveTo"),
    @JsonSubTypes.Type(value = MoveToNearestPayload.class, name = "PayLoadMoveToNearest"),
    @JsonSubTypes.Type(value = BuildingPayload.class, name = "PayloadBuilding")
})
public interface CardPayload { }
