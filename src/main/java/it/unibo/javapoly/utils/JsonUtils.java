package it.unibo.javapoly.utils;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Utility class that provides a pre-configured {@link ObjectMapper} for JSON
 * serialization and deserialization across the project.
 *
 * <p>
 * The mapper is configured with consistent options (indentation, root wrapping,
 * date handling, ...). Callers receive a copy of the shared configuration
 * through {@link #mapper()} to avoid accidental cross-thread/state mutation.
 */
public final class JsonUtils {
    private final ObjectMapper mapper;

    private JsonUtils() {
        this.mapper = create();
    }

    /**
     * Creates and configures the {@link ObjectMapper} used as template.
     *
     * @return a configured ObjectMapper instance (the internal template).
     */
    private static ObjectMapper create() {
        final ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        m.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // These one lines enable the @JsonRootName of the classes.
        m.enable(SerializationFeature.WRAP_ROOT_VALUE);
        // m.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        return m;
    }

    /**
     * Returns a copy of the pre-configured {@link ObjectMapper}.
     *
     * @return a copy of the configured ObjectMapper to be used for (de)serialization.
     */
    public ObjectMapper mapper() {
        return mapper.copy();
    }

    /**
     * This method return the only istance of this class.
     *
     * @return the only one existing istance.
     */
    public static JsonUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Static class containing the single JsonUtils instance.
     * The instance will only be created when actually requested.
     */
    private static final class SingletonHelper {
        private static final JsonUtils INSTANCE = new JsonUtils();
    }

}
