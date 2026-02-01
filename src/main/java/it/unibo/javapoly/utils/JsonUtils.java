package it.unibo.javapoly.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility class for JSON processing using the Jackson ObjectMapper.
 * This class provides a pre-configured singleton-like {@link ObjectMapper}
 * instance tailored for specific serialization and deserialization needs.
 * The configuration includes:
 * <ul>
 * <li>Excluding null values during serialization
 * ({@code JsonInclude.Include.NON_NULL}).</li>
 * <li>Disabling writing dates as timestamps.</li>
 * <li>Enabling indented output (pretty printing).</li>
 * <li>Disabling failure on empty beans.</li>
 * <li>Enabling wrapping of the root value during serialization.</li>
 * <li>Enabling unwrapping of the root value during deserialization.</li>
 * </ul>
 * This class is final and cannot be instantiated.
 */
public final class JsonUtils {

    /**
     * The shared {@link ObjectMapper} instance with specific configurations.
     */
    private static final ObjectMapper MAPPER = create();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonUtils() {
    }

    /**
     * Creates and configures the {@link ObjectMapper}.
     *
     * @return a configured {@link ObjectMapper} instance.
     */
    private static ObjectMapper create() {
        final ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        m.enable(SerializationFeature.WRAP_ROOT_VALUE);
        m.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        return m;
    }

    /**
     * Returns a copy of the pre-configured {@link ObjectMapper}.
     * Returning a copy ensures that changes made to the returned mapper do not
     * affect the shared configuration.
     *
     * @return a copy of the configured {@link ObjectMapper}.
     */
    public static ObjectMapper mapper() {
        return MAPPER.copy();
    }
}
