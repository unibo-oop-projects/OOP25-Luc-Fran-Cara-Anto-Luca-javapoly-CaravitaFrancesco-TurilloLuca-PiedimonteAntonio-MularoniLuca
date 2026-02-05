package it.unibo.javapoly;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.card.AbstractPropertyCard;
import it.unibo.javapoly.model.impl.card.LandPropertyCard;
import it.unibo.javapoly.utils.JsonUtils;

/**
 * Main application entry-point's class.
 */
public final class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private App() { }

    /**
     * Main application entry-point.
     *
     * @param args passed to JavaFX.
     */
    public static void main(final String[] args) {
        final List<Integer> rents = List.of(35, 90, 170, 260);

        final AbstractPropertyCard p = new LandPropertyCard(
            "VIA_ROMA",
            "Via Roma",
            "Descrizione",
            100,
            PropertyGroup.RED,
            10,
            rents,
            10,
            10,
            10
        );

        try {
            final String json = JsonUtils.mapper().writeValueAsString(p);
            LOGGER.info(json);
        } catch (final JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Failed to serialize property card", e);
        }
    }
}
