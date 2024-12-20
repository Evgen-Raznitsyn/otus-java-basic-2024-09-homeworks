package ru.otus.java.basic.homeworks.hw11;

import java.util.*;

public interface SearchTree <Integer> {
    /**
     * @param element to find
     * @return element if exists, otherwise - null
     */
    Integer find(Integer element);

    List<Integer> getSortedList();
}
