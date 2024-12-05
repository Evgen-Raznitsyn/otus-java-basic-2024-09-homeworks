package ru.otus.java.basic.homeworks.hw9;

import java.util.*;

public class Employee {
    private final String name;
    private final int age;

    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public static void getEmployeeNames(List<Employee> employees) {
        List<String> names = new ArrayList<>();
        for (Employee employee : employees) {
            names.add(employee.getName());
        }
        if (employees.isEmpty()) {
            System.out.println("Список сотрудников пуст");
        } else {
            System.out.println("Имена сотрудников: " + names);
        }
    }

    public static void getEmployeeAge(List<Employee> employees, int minAge) {
        List<String> names = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getAge() >= minAge) {
                names.add(employee.getName());
            }
        }
        if (employees.isEmpty()) {
            System.out.println("Список сотрудников пуст");
        } else {
            System.out.printf("Имена сотрудников, старше %d: %s\n", minAge, names);
        }

    }

    public static void isAverageAge(List<Employee> employees, double minAverageAge) {
        double sumAge = 0;
        for (Employee employee : employees) {
            sumAge += employee.getAge();
        }
        if (employees.isEmpty()) {
            System.out.println("Список сотрудников пуст");
            return;
        }
        double averageAge = sumAge / employees.size();
        if (averageAge < minAverageAge) {
            System.out.printf("Средний возраст сотрудников меньше %.2f. Т.к равен: %.2f\n", minAverageAge, averageAge);
            return;
        }
        System.out.printf("Средний возраст сотрудников превышает %.2f. И равен: %.2f\n", minAverageAge, averageAge);
    }

    public static void getEmployeeYoung(List<Employee> employees) {
        if (employees.isEmpty()){
            System.out.println("Список сотрудников пуст");
            return;
        }
        Employee youngest = employees.get(0);
        for (Employee employee : employees) {
            if (employee.getAge() < youngest.getAge()) {
                youngest = employee;
            }
        }
        System.out.printf("Самый молодой сотрудник: %s. Возраст: %s лет\n", youngest.getName(), youngest.getAge());
    }
}
