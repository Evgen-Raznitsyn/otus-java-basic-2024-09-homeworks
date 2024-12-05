package ru.otus.java.basic.homeworks.hw9;

import java.util.*;

import static ru.otus.java.basic.homeworks.hw9.Employee.*;

public class MainApp {
    public static void main(String[] args) {

        ArrayList<Integer> result = range(-10, 10);
        System.out.println("Диапазон значений: " + result);
        System.out.println();

        ArrayList<Integer> array = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        int sum = sumFive(array);
        System.out.println("Сумма чисел, которые больше 5: " + sum);
        System.out.println();

        ArrayList<Integer> numberList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("Список до изменений: " + numberList);
        rewriteList(10, numberList);
        System.out.println("Список после изменений: " + numberList);
        System.out.println();

        ArrayList<Integer> numberList2 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println("Список до изменений: " + numberList2);
        rewritePlusValue(10, numberList2);
        System.out.println("Список после изменений: " + numberList2);
        System.out.println();

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Татьяна", 30));
        employees.add(new Employee("Евгений", 25));
        employees.add(new Employee("Пётр", 28));
        employees.add(new Employee("Мария", 32));

        getEmployeeNames(employees);
        getEmployeeAge(employees, 30);
        isAverageAge(employees, 20);
        getEmployeeYoung(employees);
    }

    public static ArrayList<Integer> range(int min, int max) {
        ArrayList<Integer> rangeList = new ArrayList<>();
        if (min <= max) {
            for (int i = min; i <= max; i++) {
                rangeList.add(i);
            }
            return rangeList;
        }
        return rangeList;
    }

    public static int sumFive(ArrayList<Integer> numbers) {
        int sum = 0;
        for (int number : numbers) {
            if (number > 5) {
                sum += number;
            }
        }
        return sum;
    }

    public static void rewriteList(int value, ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, value);
        }
    }

    public static void rewritePlusValue(int value, ArrayList<Integer> list) {
        list.replaceAll(integer -> integer + value);
    }
}


