package it.unibo.javapoly.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.javapoly.model.impl.card.GameCardImpl;
import it.unibo.javapoly.model.api.card.GameCard;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * CardLoader is a utility class that helps load and write GameCard objects from and to JSON files.
 * It uses Jackson's ObjectMapper for deserialization and serialization of cards.
 */
public final class CardLoader {

    /**
     * Constructs a new CardLoader.
     */
    private CardLoader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Loads a list of GameCard objects from a file.
     * 
     * @param filePath The path to the file containing the JSON data.
     * @return A List of GameCard objects.
     * @throws IOException If there is an error reading from the file.
     */
    public static List<GameCard> loadCardsFromFile(final InputStream filePath) throws IOException {
        final ObjectMapper mapper = JsonUtils.getInstance().mapper();
        return mapper.readValue(filePath, 
            mapper.getTypeFactory().constructCollectionType(List.class, GameCardImpl.class));
    }

}

