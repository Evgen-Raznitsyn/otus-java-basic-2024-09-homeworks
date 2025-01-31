package ru.otus.java.basic.homeworks.hw22;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilsTest {
    private ArrayUtils arrayUtils;

    @BeforeEach
    public void setUp() {
        arrayUtils = new ArrayUtils();
    }

    @ParameterizedTest
    @CsvSource({
            "'1, 2, 1, 2, 2', '2, 2'",
            "'1', ''",
            "'2, 2, 2, 2', exception"
    })
    void getElementsAfterLastOne(String input, String expected) {
        int[] inputArray = Arrays.stream(input.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();

        if ("exception".equals(expected)) {
            assertThrows(RuntimeException.class, () -> arrayUtils.getElementsAfterLastOne(inputArray));
        } else {
            int[] expectedArray = expected.isEmpty() ? new int[0] :
                    Arrays.stream(expected.split(", "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            assertArrayEquals(expectedArray, arrayUtils.getElementsAfterLastOne(inputArray));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, true",
            "1, 1, false",
            "1, 3, false",
            "1, 2, 2, 1, true",
            "3, 4, 5, false",
            "1, 1, 2, true",
            "2, 2, 1, true",
            "2, 3, false",
            "0, 1, 2, false",
            "1, 1, 1, false",
            "2, 2, 2, false",
            "2, 2, 2, 1, true"
    })
    void isArrayValid(String input, boolean expected) {
        int[] inputArray = Arrays.stream(input.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
        assertEquals(expected, arrayUtils.isArrayValid(inputArray));
    }
}