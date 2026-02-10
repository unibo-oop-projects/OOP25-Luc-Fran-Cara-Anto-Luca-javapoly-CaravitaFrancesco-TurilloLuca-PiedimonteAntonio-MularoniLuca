package it.unibo.javapoly.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.board.BoardImpl;

/**
 * Utility class to load Board and Property data from JSON files.
 */
public final class BoardLoader {

    private BoardLoader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Loads a BoardImpl instance from a JSON file.
     *
     * @param jsonFilePath the path to the JSON file containing board data
     * @return a new BoardImpl instance populated with tiles from the JSON
     * @throws IOException if an I/O error occurs during file reading or parsing
     */
    public static BoardImpl loadBoardFromJson(final String jsonFilePath) throws IOException {
        final ObjectMapper mapper = JsonUtils.getInstance().mapper();

        final JsonNode root = mapper.readTree(new File(jsonFilePath));

        final JsonNode tilesNode = root.get("tiles");
        if (tilesNode == null || !tilesNode.isArray()) {
            throw new IllegalStateException("Formato JSON non valido: manca l'array 'tiles'");
        }

        final List<Tile> tiles = mapper.convertValue(
            tilesNode,
            new TypeReference<>() { }
        );

        return new BoardImpl(tiles);
    }

    /**
     * Loads a Map of Property objects indexed by their property ID from a JSON file.
     * This method extracts all properties from tiles that contain them
     * (LandPropertyTile, StationPropertyTile, UtilityPropertyTile).
     *
     * @param jsonFilePath the path to the JSON file containing board data
     * @return a Map where keys are property IDs and values are Property instances
     * @throws IOException if an I/O error occurs during file reading or parsing
     */
    public static Map<String, Property> loadPropertiesFromJson(final String jsonFilePath) throws IOException {
        final ObjectMapper mapper = JsonUtils.getInstance().mapper();

        final JsonNode root = mapper.readTree(new File(jsonFilePath));
        final JsonNode tilesNode = root.path("tiles");
        if (!tilesNode.isArray()) {
            throw new IllegalStateException("Formato JSON non valido: manca l'array 'tiles' in root");
        }

        final Map<String, Property> map = new LinkedHashMap<>();

        for (final JsonNode tileNode : tilesNode) {
            final JsonNode propNode = tileNode.get("property");
            if (propNode == null || propNode.isNull()) {
                continue; // FIXME: Eliminare il continue
            }

            final Property property = mapper.convertValue(propNode, new TypeReference<>() { });

            if (property == null) {
                continue;
            }

            // presumiamo che Property esponga getId()
            map.put(property.getId(), property);
        }

        return map;
    }
}
