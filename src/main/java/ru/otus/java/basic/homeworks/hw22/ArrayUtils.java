package ru.otus.java.basic.homeworks.hw22;

import java.util.Arrays;

public class ArrayUtils {
    public static int[] getElementsAfterLastOne(int[] array) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == 1) {
                return Arrays.copyOfRange(array, i+1, array.length);
            }
        }
        throw new RuntimeException("Array does not contain the number 1");
    }

    public static boolean isArrayValid(int[] array) {
        boolean hasOne = false;
        boolean hasTwo = false;

        for (int num : array) {
            if (num == 1) {
                hasOne = true;
            } else if (num == 2) {
                hasTwo = true;
            } else {
                return false;
            }
        }
        return hasOne && hasTwo;
    }
}
