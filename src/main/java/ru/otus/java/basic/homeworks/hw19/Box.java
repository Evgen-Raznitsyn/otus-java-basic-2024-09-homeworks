package ru.otus.java.basic.homeworks.hw19;

import java.util.ArrayList;

public class Box <T extends Fruit> {
    private ArrayList<T> fruits;
    private String type;

    public Box(String type) {
        this.fruits = new ArrayList<>();
        this.type = type;
    }

    public void addFruit(T fruit) {
        if (type.equals("Apple") && fruit instanceof Apple) {
            fruits.add(fruit);
        } else if (type.equals("Orange") && fruit instanceof Orange) {
            fruits.add(fruit);
        } else if (type.equals("Mixed")) {
            fruits.add(fruit);
        } else {
            System.out.println("Нельзя добавить фрукт этого типа в коробку " + type);
        }
    }

    public double weight() {
        double totalWeight = 0;
        for (T fruit : fruits) {
            totalWeight += fruit.getWeight();
        }
        return totalWeight;
    }

    public boolean compare(Box<?> otherBox) {
        return Math.abs(this.weight() - otherBox.weight()) < 0.0001 ;
    }

    public void pourTo(Box<T> otherBox) {
        for (T fruit : fruits) {
            otherBox.addFruit(fruit);
        }
        fruits.clear();
    }

    public String getType() {
        return type;
    }
}