package it.unibo.javapoly.utils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ValidationUtils}.
 */
class ValidationUtilsTest {

    private static final String HELLO = "hello";
    private static final String CUSTOM_ERROR = "custom error";
    private static final String BLANK_ERROR = "blank error";
    private static final String NEGATIVE_ERROR = "negative error";
    private static final String POSITIVE_ERROR = "positive error";
    private static final String RANGE_ERROR = "range error";
    private static final String INDEX_ERROR = "index error";
    private static final String AT_LEAST_ERROR = "at least error";
    private static final String AT_MOST_ERROR = "at most error";
    private static final String EXPECTED_ERROR = "expected error";
    private static final String NOT_EXPECTED_ERROR = "not expected error";
    private static final String VALUE_STR = "value";
    private static final String TEST_STR = "test";
    private static final String OK_STR = "ok";
    private static final String STR_A = "a";
    private static final String STR_B = "b";
    private static final String STR_C = "c";
    private static final String SAME_STR = "same";

    private static final int NUM_3 = 3;
    private static final int NUM_5 = 5;
    private static final int NUM_6 = 6;
    private static final int NUM_7 = 7;
    private static final int NUM_10 = 10;
    private static final int NUM_11 = 11;
    private static final int NUM_15 = 15;
    private static final int NUM_20 = 20;
    private static final int NUM_42 = 42;
    private static final int NUM_100 = 100;

    // --- requireNonNull ---

    @Test
    void testRequireNonNullWithValidObject() {
        assertEquals(HELLO, ValidationUtils.requireNonNull(HELLO));
    }

    @Test
    void testRequireNonNullWithNullThrowsNullPointerException() {
        // Use a conditional to prevent static analysis from detecting a constant known
        // null
        final Object nullValue = System.currentTimeMillis() >= 0 ? null : new Object();
        assertThrows(NullPointerException.class, () -> ValidationUtils.requireNonNull(nullValue));
    }

    @Test
    void testRequireNonNullWithCustomMessageAndValidObject() {
        final Integer value = NUM_42;
        assertEquals(value, ValidationUtils.requireNonNull(value, CUSTOM_ERROR));
    }

    @Test
    void testRequireNonNullWithCustomMessageAndNullThrows() {
        // Use a conditional to prevent static analysis from detecting a constant known
        // null
        final Object nullValue = System.currentTimeMillis() >= 0 ? null : new Object();
        final NullPointerException ex = assertThrows(NullPointerException.class,
                () -> ValidationUtils.requireNonNull(nullValue, CUSTOM_ERROR));
        assertEquals(CUSTOM_ERROR, ex.getMessage());
    }

    // --- requireNonBlank ---

    @Test
    void testRequireNonBlankWithValidString() {
        assertEquals(HELLO, ValidationUtils.requireNonBlank(HELLO));
    }

    @Test
    void testRequireNonBlankWithNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank(null));
    }

    @Test
    void testRequireNonBlankWithEmptyStringThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank(""));
    }

    @Test
    void testRequireNonBlankWithWhitespaceOnlyThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank("   "));
    }

    @Test
    void testRequireNonBlankWithTabsAndNewlinesThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank("\t\n "));
    }

    @Test
    void testRequireNonBlankWithCustomMessageAndNullThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonBlank(null, BLANK_ERROR));
        assertEquals(BLANK_ERROR, ex.getMessage());
    }

    @Test
    void testRequireNonBlankWithCustomMessageAndValidString() {
        assertEquals(TEST_STR, ValidationUtils.requireNonBlank(TEST_STR, BLANK_ERROR));
    }

    // --- requireNonNegative ---

    @Test
    void testRequireNonNegativeWithZero() {
        assertEquals(0, ValidationUtils.requireNonNegative(0));
    }

    @Test
    void testRequireNonNegativeWithPositiveValue() {
        assertEquals(NUM_5, ValidationUtils.requireNonNegative(NUM_5));
    }

    @Test
    void testRequireNonNegativeWithNegativeValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonNegative(-1));
    }

    @Test
    void testRequireNonNegativeWithCustomMessageAndNegativeThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonNegative(-NUM_10, NEGATIVE_ERROR));
        assertEquals(NEGATIVE_ERROR, ex.getMessage());
    }

    @Test
    void testRequireNonNegativeWithCustomMessageAndValidValue() {
        assertEquals(NUM_3, ValidationUtils.requireNonNegative(NUM_3, NEGATIVE_ERROR));
    }

    // --- requirePositive ---

    @Test
    void testRequirePositiveWithPositiveValue() {
        assertEquals(1, ValidationUtils.requirePositive(1));
    }

    @Test
    void testRequirePositiveWithLargeValue() {
        assertEquals(NUM_100, ValidationUtils.requirePositive(NUM_100));
    }

    @Test
    void testRequirePositiveWithZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requirePositive(0));
    }

    @Test
    void testRequirePositiveWithNegativeValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requirePositive(-NUM_5));
    }

    @Test
    void testRequirePositiveWithCustomMessageAndZeroThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requirePositive(0, POSITIVE_ERROR));
        assertEquals(POSITIVE_ERROR, ex.getMessage());
    }

    @Test
    void testRequirePositiveWithCustomMessageAndValidValue() {
        assertEquals(NUM_7, ValidationUtils.requirePositive(NUM_7, POSITIVE_ERROR));
    }

    // --- requireRange ---

    @Test
    void testRequireRangeWithValueInRange() {
        assertEquals(NUM_5, ValidationUtils.requireRange(NUM_5, 1, NUM_10));
    }

    @Test
    void testRequireRangeWithValueAtMin() {
        assertEquals(1, ValidationUtils.requireRange(1, 1, NUM_10));
    }

    @Test
    void testRequireRangeWithValueAtMax() {
        assertEquals(NUM_10, ValidationUtils.requireRange(NUM_10, 1, NUM_10));
    }

    @Test
    void testRequireRangeWithValueBelowMinThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireRange(0, 1, NUM_10));
    }

    @Test
    void testRequireRangeWithValueAboveMaxThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireRange(NUM_11, 1, NUM_10));
    }

    @Test
    void testRequireRangeWithCustomMessageAndOutOfRangeThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireRange(NUM_20, 0, NUM_10, RANGE_ERROR));
        assertEquals(RANGE_ERROR, ex.getMessage());
    }

    @Test
    void testRequireRangeWithCustomMessageAndValidValue() {
        assertEquals(NUM_5, ValidationUtils.requireRange(NUM_5, 0, NUM_10, RANGE_ERROR));
    }

    @Test
    void testRequireRangeWithNegativeRange() {
        assertEquals(-NUM_3, ValidationUtils.requireRange(-NUM_3, -NUM_5, -1));
    }

    @Test
    void testRequireRangeWithSingleValueRange() {
        assertEquals(NUM_5, ValidationUtils.requireRange(NUM_5, NUM_5, NUM_5));
    }

    @Test
    void testRequireRangeWithSingleValueRangeAndOutOfRangeThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireRange(NUM_6, NUM_5, NUM_5));
    }

    // --- requireValidIndex ---

    @Test
    void testRequireValidIndexWithFirstIndex() {
        final List<String> list = List.of(STR_A, STR_B, STR_C);
        assertEquals(0, ValidationUtils.requireValidIndex(0, list));
    }

    @Test
    void testRequireValidIndexWithMiddleIndex() {
        final List<String> list = List.of(STR_A, STR_B, STR_C);
        assertEquals(1, ValidationUtils.requireValidIndex(1, list));
    }

    @Test
    void testRequireValidIndexWithLastIndex() {
        final List<String> list = List.of(STR_A, STR_B, STR_C);
        assertEquals(2, ValidationUtils.requireValidIndex(2, list));
    }

    @Test
    void testRequireValidIndexWithNegativeIndexThrows() {
        final List<String> list = List.of(STR_A, STR_B, STR_C);
        assertThrows(IndexOutOfBoundsException.class, () -> ValidationUtils.requireValidIndex(-1, list));
    }

    @Test
    void testRequireValidIndexWithIndexEqualToSizeThrows() {
        final List<String> list = List.of(STR_A, STR_B, STR_C);
        assertThrows(IndexOutOfBoundsException.class, () -> ValidationUtils.requireValidIndex(NUM_3, list));
    }

    @Test
    void testRequireValidIndexWithIndexGreaterThanSizeThrows() {
        final List<String> list = List.of(STR_A, STR_B);
        assertThrows(IndexOutOfBoundsException.class, () -> ValidationUtils.requireValidIndex(NUM_10, list));
    }

    @Test
    void testRequireValidIndexWithEmptyCollectionThrows() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> ValidationUtils.requireValidIndex(0, Collections.emptyList()));
    }

    @Test
    void testRequireValidIndexWithCustomMessageThrows() {
        final List<Integer> list = List.of(1, 2);
        final IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class,
                // Assert that the value matches itself; this consumes the return value for
                // SpotBugs
                // but throws immediately, satisfying the test logic.
                () -> assertEquals(NUM_5, ValidationUtils.requireValidIndex(NUM_5, list, INDEX_ERROR)));
        assertEquals(INDEX_ERROR, ex.getMessage());
    }

    @Test
    void testRequireValidIndexWithCustomMessageAndValidIndex() {
        final List<Integer> list = List.of(1, 2, NUM_3);
        assertEquals(1, ValidationUtils.requireValidIndex(1, list, INDEX_ERROR));
    }

    // --- requireAtLeast ---

    @Test
    void testRequireAtLeastWithValueAboveMin() {
        assertEquals(NUM_10, ValidationUtils.requireAtLeast(NUM_10, NUM_5));
    }

    @Test
    void testRequireAtLeastWithValueEqualToMin() {
        assertEquals(NUM_5, ValidationUtils.requireAtLeast(NUM_5, NUM_5));
    }

    @Test
    void testRequireAtLeastWithValueBelowMinThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireAtLeast(NUM_3, NUM_5));
    }

    @Test
    void testRequireAtLeastWithCustomMessageAndBelowMinThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireAtLeast(1, NUM_5, AT_LEAST_ERROR));
        assertEquals(AT_LEAST_ERROR, ex.getMessage());
    }

    @Test
    void testRequireAtLeastWithCustomMessageAndValidValue() {
        assertEquals(NUM_10, ValidationUtils.requireAtLeast(NUM_10, NUM_5, AT_LEAST_ERROR));
    }

    // --- requireAtMost ---

    @Test
    void testRequireAtMostWithValueBelowMax() {
        assertEquals(NUM_3, ValidationUtils.requireAtMost(NUM_3, NUM_10));
    }

    @Test
    void testRequireAtMostWithValueEqualToMax() {
        assertEquals(NUM_10, ValidationUtils.requireAtMost(NUM_10, NUM_10));
    }

    @Test
    void testRequireAtMostWithValueAboveMaxThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireAtMost(NUM_15, NUM_10));
    }

    @Test
    void testRequireAtMostWithCustomMessageAndAboveMaxThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireAtMost(NUM_20, NUM_10, AT_MOST_ERROR));
        assertEquals(AT_MOST_ERROR, ex.getMessage());
    }

    @Test
    void testRequireAtMostWithCustomMessageAndValidValue() {
        assertEquals(NUM_5, ValidationUtils.requireAtMost(NUM_5, NUM_10, AT_MOST_ERROR));
    }

    // --- requireExpected ---

    @Test
    void testRequireExpectedWithMatchingValues() {
        assertEquals(HELLO, ValidationUtils.requireExpected(HELLO, HELLO));
    }

    @Test
    void testRequireExpectedWithMatchingIntegers() {
        assertEquals(NUM_42, ValidationUtils.requireExpected(NUM_42, NUM_42));
    }

    @Test
    void testRequireExpectedWithBothNull() {
        assertDoesNotThrow(() -> ValidationUtils.requireExpected(null, null));
    }

    @Test
    void testRequireExpectedWithMismatchThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireExpected(STR_A, STR_B));
    }

    @Test
    void testRequireExpectedWithNullValueAndNonNullExpectedThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireExpected(null, "expected"));
    }

    @Test
    void testRequireExpectedWithNonNullValueAndNullExpectedThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireExpected(VALUE_STR, null));
    }

    @Test
    void testRequireExpectedWithCustomMessageAndMismatchThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireExpected(1, 2, EXPECTED_ERROR));
        assertEquals(EXPECTED_ERROR, ex.getMessage());
    }

    @Test
    void testRequireExpectedWithCustomMessageAndMatchingValues() {
        assertEquals(OK_STR, ValidationUtils.requireExpected(OK_STR, OK_STR, EXPECTED_ERROR));
    }

    // --- requireNotExpected ---

    @Test
    void testRequireNotExpectedWithDifferentValues() {
        assertEquals(STR_A, ValidationUtils.requireNotExpected(STR_A, STR_B));
    }

    @Test
    void testRequireNotExpectedWithMatchingValuesThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNotExpected(SAME_STR, SAME_STR));
    }

    @Test
    void testRequireNotExpectedWithBothNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNotExpected(null, null));
    }

    @Test
    void testRequireNotExpectedWithNullValueAndNonNullNotExpected() {
        assertDoesNotThrow(() -> ValidationUtils.requireNotExpected(null, "something"));
    }

    @Test
    void testRequireNotExpectedWithNonNullValueAndNullNotExpected() {
        assertEquals(VALUE_STR, ValidationUtils.requireNotExpected(VALUE_STR, null));
    }

    @Test
    void testRequireNotExpectedWithCustomMessageAndMatchingThrows() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNotExpected(NUM_10, NUM_10, NOT_EXPECTED_ERROR));
        assertEquals(NOT_EXPECTED_ERROR, ex.getMessage());
    }

    @Test
    void testRequireNotExpectedWithCustomMessageAndDifferentValues() {
        assertEquals(NUM_5, ValidationUtils.requireNotExpected(NUM_5, NUM_10, NOT_EXPECTED_ERROR));
    }
}
