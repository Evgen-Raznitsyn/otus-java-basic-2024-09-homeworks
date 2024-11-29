package ru.otus.java.basic.homeworks.hw8;

import static ru.otus.java.basic.homeworks.hw8.SumArray.sumArray;

public class MainApp {
    public static void main(String[] args) {
        String[][] array = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}
        };
        try {
            int result = sumArray(array);
            System.out.println("Сумма элементов массива: " + result);
        } catch (AppArraySizeException | AppArrayDataException e) {
            System.err.println(e.getMessage());
        }
    }
}


