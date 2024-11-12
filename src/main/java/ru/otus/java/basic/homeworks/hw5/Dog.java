package ru.otus.java.basic.homeworks.hw5;

public class Dog extends Animal {
    public Dog(String name, double runSpeed, double swimSpeed, int endurance) {
        super(name, runSpeed, swimSpeed, endurance);
    }

    @Override
    public double swim(int distance) {
        enduranceCost = distance * 2;
        return super.swim(distance);
    }
}
