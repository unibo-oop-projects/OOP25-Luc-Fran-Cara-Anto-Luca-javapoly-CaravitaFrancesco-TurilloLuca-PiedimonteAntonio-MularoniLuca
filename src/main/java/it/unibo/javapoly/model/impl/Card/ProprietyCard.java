package it.unibo.javapoly.model.impl.Card;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import it.unibo.javapoly.model.api.card.Card;
import it.unibo.javapoly.utils.JsonUtils;

// TODO: add all the JavaDoc comment

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = LandProprietyCard.class, name = "street"),
//   @JsonSubTypes.Type(value = StationPropertyCard.class, name = "station"),
  @JsonSubTypes.Type(value = UtilityProprietyCard.class, name = "utility")
})
@JsonRootName(value = "ProprietyCard")
public abstract class ProprietyCard implements Card{

    final String id;
    final String name;
    final String description;
    final int proprietyCost;
    final String group;

    public ProprietyCard(final String id, final String name, final String description, final int proprietyCost, final String group) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.proprietyCost = proprietyCost;
        this.group = group;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public int getProprietyCost() {
        return this.proprietyCost;
    }

    public String getGroup() {
        return this.group;
    }

    /**
    *   this method return the final rent that a player need to pay
    *   (including house, multiplier, number of station)
    */
    public abstract int calculateRent(int number);

    @Override
    public String toString() {
        try {
            return JsonUtils.mapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Serialization failed\"}";
        }
    }
    
}
