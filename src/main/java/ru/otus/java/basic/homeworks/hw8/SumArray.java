package ru.otus.java.basic.homeworks.hw8;

class AppArraySizeException extends Exception {
    public AppArraySizeException(String message) {
        super(message);
    }
}

class AppArrayDataException extends Exception {
    public AppArrayDataException(String message) {
        super(message);
    }
}

public class SumArray {
    public static int sumArray(String[][] array) throws AppArraySizeException, AppArrayDataException {
        if (array.length != 4) {
            throw new AppArraySizeException("Некорректный размер массива: должно быть 4 строки.");
        }
        for (String[] row : array) {
            if (row.length != 4) {
                throw new AppArraySizeException("Некорректный размер массива: должно быть 4 колонки.");
            }
        }
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                try {
                    sum += Integer.parseInt(array[i][j]);
                } catch (NumberFormatException e) {
                    throw new AppArrayDataException("Неверные данные в ячейке [" + i + "][" + j + "]: " + array[i][j]);
                }
            }
        }
        return sum;
    }
}
