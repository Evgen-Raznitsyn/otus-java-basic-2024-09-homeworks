package ru.otus.java.basic.homeworks.hw2;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        int[] testArr = {1,2,3,4,5,6,7,8,9};



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
    public static void certainValue(int num, int[] testArr) {
        for (int i = 0; i < testArr.length; i++) {
            testArr[i] = num;
        }
        System.out.println(Arrays.toString(testArr));
    }

    /*
    Реализуйте метод, принимающий в качестве аргумента целое число и ссылку на целочисленный массив,
    увеличивающий каждый элемент массива на указанное число
    */
    public static void plusValue(int num, int[] testArr) {
        for (int i = 0; i < testArr.length; i++) {
            testArr[i] += num;
        }
        System.out.println(Arrays.toString(testArr));
    }

    

}
