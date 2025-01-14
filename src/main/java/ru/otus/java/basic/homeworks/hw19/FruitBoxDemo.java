package ru.otus.java.basic.homeworks.hw19;

public class FruitBoxDemo {
    public static void main(String[] args) {
        Box<Apple> appleBox = new Box<>("Apple");
        appleBox.addFruit(new Apple(1.0));
        appleBox.addFruit(new Apple(1.2));

        Box<Orange> orangeBox = new Box<>("Orange");
        orangeBox.addFruit(new Orange(1.5));
        orangeBox.addFruit(new Orange(1.3));

        System.out.println("Вес коробки с яблоками: " + appleBox.weight());
        System.out.println("Вес коробки с апельсинами: " + orangeBox.weight());

        System.out.println("Коробки равны по весу? - " + appleBox.compare(orangeBox));

        Box<Apple> appleBox2 = new Box<>("Apple");
        appleBox.pourTo(appleBox2);
        System.out.println("Вес новой коробки с яблоками после пересыпания: " + appleBox2.weight());
    }
}
