package ru.otus.java.basic.homeworks.hw22;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilsTest {

    @Test
    void getElementsAfterLastOne() {
        assertArrayEquals(new int[]{2, 2}, ArrayUtils.getElementsAfterLastOne(new int[]{1, 2, 1, 2, 2}));
        assertArrayEquals(new int[]{}, ArrayUtils.getElementsAfterLastOne(new int[]{1}));
        assertThrows(RuntimeException.class, () -> ArrayUtils.getElementsAfterLastOne(new int[]{2, 2, 2, 2}));
    }

    @Test
    void isArrayValid() {
        assertTrue(ArrayUtils.isArrayValid(new int[]{1, 2}));
        assertFalse(ArrayUtils.isArrayValid(new int[]{1, 1}));
        assertFalse(ArrayUtils.isArrayValid(new int[]{1, 3}));
        assertTrue(ArrayUtils.isArrayValid(new int[]{1, 2, 2, 1}));
        assertFalse(ArrayUtils.isArrayValid(new int[]{3, 4, 5}));
    }
}