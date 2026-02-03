package it.unibo.javapoly.model.impl.property;

import java.util.Objects;

import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.api.property.PropertyState;
import it.unibo.javapoly.model.impl.card.AbstractPropertyCard;

public class PropertyImpl  implements Property{

    private final String id;
    private final int position; 
    private final AbstractPropertyCard card;
    private final PropertyStateImpl state;

    /**
     * Create a property tile.
     *
     * @param id unique id
     * @param position board index
     * @param card associated card
     */
    public PropertyImpl(final String id, final int position, final AbstractPropertyCard card) {
        this.id = Objects.requireNonNull(id);
        this.position = position;
        this.card = Objects.requireNonNull(card);
        this.state = new PropertyStateImpl(card.getPropertyCost());
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public AbstractPropertyCard getCard() {
        return this.card;
    }

    @Override
    public PropertyState getState() {
        return this.state;
    }

    @Override
    public int getPosition() {
        return this.position;
    }
    
}
