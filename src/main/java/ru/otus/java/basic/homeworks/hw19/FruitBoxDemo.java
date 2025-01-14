package ru.otus.java.basic.homeworks.hw19;

public class FruitBoxDemo {
    public static void main(String[] args) {
        Box<Apple> appleBox = new Box<>();
        Box<Orange> orangeBox = new Box<>();
        Box<Fruit> fruitBox = new Box<>();

        appleBox.add(new Apple(0.29));
        appleBox.add(new Apple(0.33));
        appleBox.add(new Apple(0.38));
        System.out.println("Вес appleBox = " + appleBox.weight());

        orangeBox.add(new Orange(0.16));
        orangeBox.add(new Orange(0.35));
        orangeBox.add(new Orange(0.26));
        System.out.println("Вес orangeBox = " + orangeBox.weight());

        fruitBox.add(new Apple(0.35));
        fruitBox.add(new Apple(0.22));
        fruitBox.add(new Orange(0.32));
        fruitBox.add(new Orange(0.34));
        System.out.println("Вес fruitBox = " + fruitBox.weight());

        System.out.println("Вес fruitBox = appleBox? : " + fruitBox.compare(appleBox));
        System.out.println("Вес appleBox = orangeBox? : " + appleBox.compare(orangeBox));

        System.out.println("Пересыпаем appleBox -> fruitBox");
        appleBox.pourTo(fruitBox);
        System.out.println("Вес appleBox = " + appleBox.weight());
        System.out.println("Вес orangeBox = " + orangeBox.weight());
        System.out.println("Вес fruitBox = " + fruitBox.weight());

        System.out.println("Пересыпаем orangeBox -> fruitBox");
        orangeBox.pourTo(fruitBox);
        System.out.println("Вес appleBox = " + appleBox.weight());
        System.out.println("Вес orangeBox = " + orangeBox.weight());
        System.out.println("Вес fruitBox = " + fruitBox.weight());
    }
}
