package ru.otus.java.basic.homeworks.hw1;

import java.util.Random;
import java.util.Scanner;


public class MainApp {
    public static void main(String[] args) {
        numberSelection();
    }

    //(1) Реализуйте метод greetings(), который при вызове должен отпечатать в столбец  слова: Hello, World, from, Java
    public static void greetings() {
        System.out.println("Hello");
        System.out.println("World");
        System.out.println("from");
        System.out.println("Java");
    }

    /*
    (2) Реализуйте метод checkSign(..), принимающий в качестве аргументов 3 int переменные a, b и c.
    Метод должен посчитать их сумму, и если она больше или равна 0, то вывести в консоль сообщение "Сумма положительная",
    в противном случае - "Сумма отрицательная".
    */
    public static void checkSign(int a, int b, int c) {
        int sum = a + b + c;
        if (sum >= 0) {
            System.out.println("Сумма положительна");
        } else {
            System.out.println("Сумма отрицательная");
        }
    }

    /*
    (3) Реализуйте метод selectColor() в теле которого задайте int переменную data с любым начальным значением.
    Если data меньше 10 включительно, то в консоль должно быть выведено сообщение "Красный", если от 10 до 20 включительно,
    то "Желтый", если больше 20 - "Зеленый".
    */
    public static void selectColor() {
        int data = new Random().nextInt(30) + 1;
        if (data <= 10) {
            System.out.println("Красный");
        } else if (data <= 20) {
            System.out.println("Желтый");
        } else {
            System.out.println("Зеленый");
        }
    }

    /*
    (4) Реализуйте метод compareNumbers(), в теле которого объявите две int переменные a и b с любыми начальными значениями.
    Если a больше или равно b, то необходимо вывести в консоль сообщение "a>=b", в противном случае "a<b"
    */
    public static void compareNumbers() {
        int a = new Random().nextInt(100) + 1;
        int b = new Random().nextInt(100) + 1;
        if (a >= b) {
            System.out.println("a >= b");
        } else {
            System.out.println("a < b");
        }
    }

    /*
    (5) Создайте метод addOrSubtractAndPrint(int initValue, int delta, boolean increment).
    Если increment = true, то метод должен к initValue прибавить delta и отпечатать в консоль, в противном случае - вычесть.
    */
    public static void addOrSubtractAndPrint(int initValue, int delta, boolean increment) {
        if (increment == true) {
            System.out.println(initValue + delta);
        } else {
            System.out.println(initValue - delta);
        }
    }

    /*
    При запуске приложения, запросить у пользователя число от 1 до 5, после ввода которого выполнить метод,
    соответствующий указанному номеру
    */
    public static void numberSelection() {
        System.out.print("Введите число от 1 до 5, чтобы выбрать метод: ");
        Scanner keyboard = new Scanner(System.in);
        int inputNumber, a, b, c, initValue, delta;
        boolean increment;
        inputNumber = keyboard.nextInt();
        a = (int) (Math.random() * 20 - 10);
        b = (int) (Math.random() * 20 - 10);
        c = (int) (Math.random() * 20 - 10);
        initValue = (int) (Math.random() * 10);
        delta = (int) (Math.random() * 10);
        increment = new Random().nextBoolean();

        switch (inputNumber) {
            case 1:
                greetings();
                break;
            case 2:
                checkSign(a, b, c);
                break;
            case 3:
                selectColor();
                break;
            case 4:
                compareNumbers();
                break;
            case 5:
                addOrSubtractAndPrint(initValue, delta, increment);
                break;
            default:
                System.out.println("Упс, что-то пошло не так!");
        }
        numberSelection();
    }
}


