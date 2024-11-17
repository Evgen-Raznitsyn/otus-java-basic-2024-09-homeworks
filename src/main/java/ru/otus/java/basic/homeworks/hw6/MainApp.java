package ru.otus.java.basic.homeworks.hw6;

public class MainApp {
    public static void main(String[] args) {
        Plate plate = new Plate(50);
        Cat[] cats = {
                new Cat("Мурзик", 20),
                new Cat("Барсик", 25),
                new Cat("Кузя", 30),
                new Cat("Василий", 12)
        };

        for (Cat cat : cats) {
            String state = cat.isFull() ? " сыт" : " голоден";
            System.out.println(cat.getName() + state);
        }
        System.out.println();

        plate.info();

        for (Cat cat : cats) {
            cat.eat(plate);
        }
        System.out.println("Коты поели");
        System.out.println();

        for (Cat cat : cats) {
            String state = cat.isFull() ? " сыт" : " голоден";
            System.out.println(cat.getName() + state);
        }
        System.out.println();

        plate.info();

        plate.addFood(-100);
        plate.info();

        for (Cat cat : cats) {
            cat.eat(plate);
        }
        System.out.println("Коты поели");
        System.out.println();

        for (Cat cat : cats) {
            String state = cat.isFull() ? " сыт" : " голоден";
            System.out.println(cat.getName() + state);
        }
        System.out.println();

        plate.info();
    }
}
