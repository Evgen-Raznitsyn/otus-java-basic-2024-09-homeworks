package ru.otus.java.basic.homeworks.hw2;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        int[] testArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        printTheString(5, "Hello");
        sum(1, 2, 3, 4, 5, 6, 7, 8, 9);
        certainValue(testArr, 5);
        plusValue(testArr, 1);
        sumСomparison(testArr);
        reverse(testArr);


    }

    /*
    Реализуйте метод, принимающий в качестве аргументов целое число и строку,
    и печатающий в консоль строку указанное количество раз
    */
    public static void printTheString(int n, String... message) {
        String output = Arrays.toString(message);
        for (int i = 0; i < n; i++) {
            System.out.println(output);
        }
    }

    /*
    Реализуйте метод, принимающий в качестве аргумента целочисленный массив, суммирующий все элементы,
    значение которых больше 5, и печатающий полученную сумму в консоль
    */
    public static void sum(int... arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 5) {
                sum += arr[i];
            }
        }
        System.out.println(sum);
    }

    /*
    Реализуйте метод, принимающий в качестве аргумента целое число и ссылку на целочисленный массив,
    метод должен заполнить каждую ячейку массива указанным числом
    */
    public static void certainValue(int[] testArr, int num) {
        for (int i = 0; i < testArr.length; i++) {
            testArr[i] = num;
        }
        System.out.println(Arrays.toString(testArr));
    }

    /*
    Реализуйте метод, принимающий в качестве аргумента целое число и ссылку на целочисленный массив,
    увеличивающий каждый элемент массива на указанное число
    */
    public static void plusValue(int[] testArr, int num) {
        for (int i = 0; i < testArr.length; i++) {
            testArr[i] += num;
        }
        System.out.println(Arrays.toString(testArr));
    }

    /*
    Реализуйте метод, принимающий в качестве аргумента целочисленный массив,
    и печатающий в консоль информацию о том, сумма элементов какой из половин массива больше
     */
    public static void sumСomparison(int[] arr) {
        int sumleft = 0;
        int sumright = 0;
        for (int i = 0; i < arr.length / 2; i++) {
            sumleft += arr[i];
        }
        for (int i = arr.length / 2; i < arr.length; i++) {
            sumright += arr[i];
        }
        if (sumleft > sumright) {
            System.out.println("Левая половина больше чем правая и равна " + sumleft);
        } else if (sumleft < sumright) {
            System.out.println("Правая половина больше чем левая и равна " + sumright);
        } else {
            System.out.println("суммы половин равны " + sumleft + "=" + sumright);
        }
    }

    // Реализуйте метод, переворачивающийся входящий массив
    private static void reverse(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int tmp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = tmp;
        }
        System.out.println(Arrays.toString(arr));

    }


}