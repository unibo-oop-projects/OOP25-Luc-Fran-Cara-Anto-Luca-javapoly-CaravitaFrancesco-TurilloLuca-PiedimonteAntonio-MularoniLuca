package it.unibo.javapoly.utils;

import java.util.Collection;
import java.util.Objects;

/**
 * Utility class for common validation checks.
 *
 * This class provides static methods to validate arguments and throw
 * appropriate exceptions if the validation fails. It cannot be instantiated.
 */
public final class ValidationUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ValidationUtils() {
    }

    /**
     * Validates that an object is not null.
     *
     * @param <T>     the type of the object
     * @param obj     the object to validate
     * @param message the error message to display if validation fails
     * @return the validated object
     * @throws NullPointerException if the object is {@code null}
     */
    public static <T> T requireNonNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }

    /**
     * Validates that a {@link String} is not null or blank (empty or only
     * whitespace).
     *
     * @param input        the {@link String} to validate
     * @param errorMessage the error message to display if validation fails
     * @return the validated {@link String}
     * @throws IllegalArgumentException if the input is {@code null} or blank
     */
    public static String requireNonBlank(String input, String errorMessage) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return input;
    }

    /**
     * Validates that an {@code int} is not negative.
     *
     * @param value        the value to validate.
     * @param errorMessage the error message to display if validation fails.
     * @return the validated {@code int} value.
     * @throws IllegalArgumentException if the value is negative.
     */
    public static int requireNonNegative(int value, String errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    /**
     * Validates that an {@code int} is positive (greater than zero).
     *
     * @param value        the value to validate.
     * @param errorMessage the error message to display if validation fails.
     * @return the validated {@code int} value.
     * @throws IllegalArgumentException if the value is not positive.
     */
    public static int requirePositive(int value, String errorMessage) {
        if (value <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    /**
     * Validates that an {@code int} is within a specific range (inclusive).
     *
     * @param value        the value to validate.
     * @param min          the minimum acceptable value.
     * @param max          the maximum acceptable value.
     * @param errorMessage the error message to display if validation fails.
     * @return the validated {@code int} value.
     * @throws IllegalArgumentException if the value is out of range.
     */
    public static int requireRange(int value, int min, int max, String errorMessage) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    errorMessage + " (Value: " + value + ", Range: " + min + "-" + max + ")");
        }
        return value;
    }

    /**
     * Validates that an index is valid for a given collection.
     *
     * @param index        the index to validate.
     * @param collection   the collection on which to validate the index.
     * @param errorMessage the error message to display if validation fails.
     * @return the validated {@code int} index.
     * @throws IndexOutOfBoundsException if the index is out of bounds for the
     *                                   collection.
     */
    public static int requireValidIndex(int index, Collection<?> collection, String errorMessage) {
        if (index < 0 || index >= collection.size()) {
            throw new IndexOutOfBoundsException(
                    errorMessage + " (Index: " + index + ", Size: " + collection.size() + ")");
        }
        return index;
    }
}
