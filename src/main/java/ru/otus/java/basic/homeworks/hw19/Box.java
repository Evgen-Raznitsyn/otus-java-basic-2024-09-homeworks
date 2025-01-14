package ru.otus.java.basic.homeworks.hw19;

import java.util.*;

public class Box<T extends Fruit> {
    private final List<T> fruits = new ArrayList<>();

    public void add(T fruit) {
        fruits.add(fruit);
    }

    public void addFruit(List<? extends T> fruitsList) {
        fruits.addAll(fruitsList);
    }

    public double weight() {
        double totalWeight = 0;
        for (T fruit : fruits) {
            totalWeight += fruit.getWeight();
        }
        return totalWeight;
    }

    public boolean compare(Box<?> otherBox) {
        return Math.abs(this.weight() - otherBox.weight()) < 0.0001;
    }

    public void pourTo(Box<? super T> otherBox) {
        if (otherBox == this) {
            System.out.println("Коробку нельзя пересыпать саму в себя");
            return;
        }
        Collections.reverse(fruits);
        otherBox.addFruit(fruits);
        fruits.clear();
    }
}