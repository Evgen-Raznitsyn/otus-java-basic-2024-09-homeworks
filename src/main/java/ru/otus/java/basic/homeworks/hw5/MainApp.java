package ru.otus.java.basic.homeworks.hw5;

import ru.otus.java.basic.homeworks.hw5.animals.Cat;
import ru.otus.java.basic.homeworks.hw5.animals.Dog;
import ru.otus.java.basic.homeworks.hw5.animals.Horse;

public class MainApp {
    public static void main(String[] args) {
        Cat cat = new Cat("Кузя", 3, 0, 20);
        Dog dog = new Dog("Шарик", 5, 1, 50);
        Horse horse = new Horse("Молния", 13, 2, 70);

        cat.info();
        cat.run(10);
        cat.swim(5);
        cat.run(10);
        cat.info();
        System.out.println();

        dog.info();
        dog.run(10);
        dog.swim(5);
        dog.run(10);
        dog.info();
        System.out.println();

        horse.info();
        horse.run(10);
        horse.swim(5);
        horse.run(10);
        horse.info();
    }
}
