package ru.otus.java.basic.homeworks.hw6;

public class Cat {
    private String name;
    private int appetite;
    public boolean isFull;

    public Cat(String name, int appetite) {
        this.name = name;
        this.appetite = appetite;
        this.isFull = false;
    }

    public void eat(Plate plate) {
        if (plate.decreaseFood(appetite)) {
            isFull = true;
            appetite = 0;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isFull() {
        return isFull;
    }
}
