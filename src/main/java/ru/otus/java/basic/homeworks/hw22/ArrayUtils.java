package ru.otus.java.basic.homeworks.hw22;

public class ArrayUtils {
    public static int[] getElementsAfterLastOne(int[] array) {
        int lastIndex = -1;

        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                lastIndex = i;
            }
        }

        if (lastIndex == -1) {
            throw new RuntimeException("Array does not contain the number 1");
        }

        int[] result = new int[array.length - lastIndex - 1];
        System.arraycopy(array, lastIndex + 1, result, 0, result.length);

        return result;
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
