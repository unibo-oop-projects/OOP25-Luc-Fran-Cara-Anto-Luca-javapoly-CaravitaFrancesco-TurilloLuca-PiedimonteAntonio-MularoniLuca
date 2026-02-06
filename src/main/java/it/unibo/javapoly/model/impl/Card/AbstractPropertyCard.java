package it.unibo.javapoly.model.impl.Card;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.card.Card;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.utils.JsonUtils;

/**
 * Base representation of a property card.
 *
 * <p>
 * Concrete implementations represent the different kinds of properties
 * (streets, stations, utilities). This class contains basic common fields
 * and behaviour shared by all property cards.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = LandPropertyCard.class, name = "street"),
    @JsonSubTypes.Type(value = StationPropertyCard.class, name = "station"),
    @JsonSubTypes.Type(value = UtilityPropertyCard.class, name = "utility")
})
@JsonRootName("PropertyCard")
public abstract class AbstractPropertyCard implements Card {

    /**
     * Error message used when a rent list is unexpectedly empty.
     */
    protected static final String ERR_LIST_IS_EMPTY = "The rent list is empty";

    /**
     * Error message used when a rent list is null.
     */
    protected static final String ERR_LIST_IS_NULL = "The rent list is null";

    /**
     * Error message used when an index is outside the valid bounds of a rent list.
     */
    protected static final String ERR_INDEX_OUT_LIMITS = "The given index is out of size: ";

    private final String id;
    private final String name;
    private final String description;
    private final int propertyCost;
    private final PropertyGroup group;

    /**
     * Creates a new property card.
     *
     * @param id the card identifier.
     * @param name the card name.
     * @param description the card description.
     * @param propertyCost the purchase cost of the property.
     * @param group the color / group of the property.
     */
    public AbstractPropertyCard(final String id,
                                final String name,
                                final String description,
                                final int propertyCost,
                                final PropertyGroup group) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.propertyCost = propertyCost;
        this.group = group;
    }

    /**
     * Returns the unique identifier of the card.
     *
     * @return the card identifier.
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Returns the display name of the card.
     *
     * @return the display name.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns the description of the card.
     *
     * @return the description.
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the purchase cost of this property.
     *
     * @return the property cost.
     */
    public int getPropertyCost() {
        return this.propertyCost;
    }

    /**
     * Returns the group (color set) to which this property belongs.
     *
     * @return the property group.
     */
    public PropertyGroup getGroup() {
        return this.group;
    }

    /**
     * Returns the final rent that a player needs to pay for this property,
     * depending on the provided parameter (e.g. number of houses, or other
     * modifiers specific to the concrete property type).
     *
     * @param rent the rent calculation context (e.g. dice total, owned utilities).
     * @return the calculated rent for the given context.
     */
    public abstract int calculateRent(RentContext rent);

    /**
     * Returns a JSON representation of this card.
     *
     * <p>
     * NOTE: this method uses {@code JsonUtils}. If you want to avoid this dependency
     * in the model package, consider moving serialization logic elsewhere.
     *
     * @return the JSON string representing this card; if serialization fails a short JSON error object is returned.
     */
    @Override
    public String toString() {     // FIXME: Model can't use utils
        try {
            return JsonUtils.mapper().writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            return "{\"error\":\"Serialization failed\"}";
        }
    }
}
