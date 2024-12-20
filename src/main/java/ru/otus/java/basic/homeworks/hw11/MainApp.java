package ru.otus.java.basic.homeworks.hw11;

import java.util.*;

public class MainApp {
    public static void main(String[] args) {
        List<Integer> elements = Arrays.asList(5, 3, 7, 2, 4, 6, 8);
        BinarySearchTree bst = new BinarySearchTree(elements);

        Integer searchElement = 2;
        Integer result = bst.find(searchElement);
        if (result != null) {
            System.out.println("Элемент (" + searchElement + ") найден!");
        } else {
            System.out.println("Элемент (" + searchElement + ") не найден!");
        }
        System.out.println();

        List<Integer> sortedList = bst.getSortedList();
        System.out.println("Отсортированный список: " + sortedList);
    }
}
