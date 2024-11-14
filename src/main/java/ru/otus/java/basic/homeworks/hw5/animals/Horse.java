package ru.otus.java.basic.homeworks.hw5.animals;

import ru.otus.java.basic.homeworks.hw5.Animal;

public class Horse extends Animal {
    public Horse(String name, double runSpeed, double swimSpeed, int endurance) {
        super(name, runSpeed, swimSpeed, endurance);
    }

    @Override
    public double swim(int distance) {
        swimEndurancePerMeter = 4;
        return super.swim(distance);
    }
}
