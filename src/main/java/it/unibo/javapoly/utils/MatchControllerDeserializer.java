package it.unibo.javapoly.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.javapoly.controller.impl.MatchControllerImpl;
import java.io.File;
import java.io.IOException;

/**
 * Utility class for serializing and deserializing MatchControllerImpl instances to and from JSON files.
 */
public final class MatchControllerDeserializer {
    private MatchControllerDeserializer() {
    }

    /**
     * Deserializes a MatchControllerImpl from the given JSON file.
     *
     * @param file the JSON file containing the serialized MatchControllerImpl.
     * @return the deserialized MatchControllerImpl instance.
     * @throws IOException if an error occurs during deserialization or file reading.
     */
    public static MatchControllerImpl deserialize(final File file) throws IOException {
        final ObjectMapper mapper = JsonUtils.getInstance().mapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        final JsonNode root = mapper.readTree(file);
        final JsonNode matchNode = root.get("MatchControllerImpl");
        if (matchNode == null) {
            throw new IllegalArgumentException("Invalid JSON: missing 'MatchControllerImpl' field");
        }
        return mapper.treeToValue(matchNode, MatchControllerImpl.class);
    }
}
