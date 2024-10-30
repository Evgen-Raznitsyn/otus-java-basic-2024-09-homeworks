package ru.otus.java.basic.homeworks.hw3;

public class MainApp {
    public static void main(String[] args) {
        //System.out.println(sumOfPositiveElements(new int[][]{{1, 2, 3}, {4, 0, 6}, {7, 8, 9}}));
        //square(20);
        //zeroingDiagonals(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        //System.out.println(findMax(new int[][]{{1, 2, 3}, {4, 0, 6}, {7, 8, 9}}));
        //System.out.println(sumOfTheSecondLine(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
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
        char[][] array = new char[size][size];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (i == 0 || j == 0 || i == array[0].length - 1 || j == array[0].length - 1) {
                    System.out.print((array[i][j] = '*') + "  ");
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
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i][j] > max) {
                    max = array[i][j];
                }
            }
        }
        return max;
    }


//    public static int sumOfTheSecondLine(int[][] array) {
//        int sum = 0;
//        for (int i = 0; i < array.length; i++) {
//            for (int j = 0; j < array.length; j++) {
//                if (j == 2) {
//                    sum += array[i][j];
//                }
//                return -1;
//            }
//        }
//        return sum;
//    }
}
