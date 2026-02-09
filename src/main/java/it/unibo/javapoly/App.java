package it.unibo.javapoly;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import it.unibo.javapoly.model.api.card.GameCard;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.utils.BoardLoader;
import it.unibo.javapoly.utils.CardLoader;

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
        /*
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
        } */

        try {
            final String pathCards = "src/main/resources/Card/UnexpectedCards.json";
            final List<GameCard> cards = CardLoader.loadCardsFromFile(pathCards);
            LOGGER.info("Cards loaded: " + cards.size());

        } catch (final IOException e) {
            LOGGER.severe("Error loading Cards: " + e.getMessage());
        }

        try {

            final String pathBoardJson = "src/main/resources/Card/BoardTiles.json";
            final BoardImpl board = BoardLoader.loadBoardFromJson(pathBoardJson);
            final Map<String, Property> properties = BoardLoader.loadPropertiesFromJson(pathBoardJson);

            LOGGER.info("Board loaded with " + board.size() + " tiles");
            LOGGER.info("Total properties: " + properties.size());

            final Property vicoloCorto = properties.get("vicolo_corto");
            if (vicoloCorto != null) {
                LOGGER.info("Property: " + vicoloCorto.getCard().getName());
                LOGGER.info("Price: " + vicoloCorto.getPurchasePrice());
            }

        } catch (final IOException e) {
            LOGGER.severe("Error loading board or properties: " + e.getMessage());
        }
    }
}
