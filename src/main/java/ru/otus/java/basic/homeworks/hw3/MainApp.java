package ru.otus.java.basic.homeworks.hw3;

public class MainApp {
    public static void main(String[] args) {
        //System.out.println(sumOfPositiveElements(new int[][]{{1, 2, 3}, {4, 0, 6}, {7, 8, 9}}));
        //square(5);
        //zeroingDiagonals(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        //System.out.println(findMax(new int[][]{{-10, 2, 3}, {4, 0, 6}, {7, 8, 9}}));
        //System.out.println(sumOfTheSecondLine(new int[][]{{-1, 1, 1, 1}, {1, 2, 3, 1}, {4, 5, 6, 1}, {1, 1, -1, 1}}));
    }

    /**
     * @param array входной двумерный массив
     * @return сумма элементов, которые больше 0
     */
    public static int sumOfPositiveElements(int[][] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i][j] > 0) {
                    sum += array[i][j];
                }
            }
        }
        return sum;
    }

    /**
     * @param size размер квадрата
     */
    public static void square(int size) {

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == 0 || y == 0 || x == size - 1 || y == size - 1) {
                    System.out.print("*  ");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    /*
    Реализовать метод, принимающий в качестве аргумента квадратный двумерный массив,
    и обнуляющий его диагональные элементы
     */
    public static void zeroingDiagonals(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (i == j || i + 1 == array.length - j) {
                    array[i][j] = 0;
                }
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * @param array входной двумерный массив
     * @return максимальный элемент массива
     */
    public static int findMax(int[][] array) {
        int max = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i][j] > max) {
                    max = array[i][j];
                }
            }
        }
        return max;
    }

    /**
     * @param array входной двумерный массив
     * @return сумма элементов второй строки
     */
    public static int sumOfTheSecondLine(int[][] array) {
        if (array.length < 2) {
            return -1;
        }
        int sum = 0;
        for (int j = 0; j < array.length; j++) {
            sum += array[1][j];
        }
        return sum;

    }
}
