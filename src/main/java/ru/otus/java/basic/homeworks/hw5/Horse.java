package ru.otus.java.basic.homeworks.hw5;

public class Horse extends Animal {
    public Horse(String name, double runSpeed, double swimSpeed, int endurance) {
        super(name, runSpeed, swimSpeed, endurance);
    }

    @Override
    public double swim(int distance) {
        enduranceCost = distance * 4;
        return super.swim(distance);
    }
}
