package ru.otus.java.basic.homeworks.hw4;

public class Box {
    private final int width, height, depth;
    private String color, item;
    private boolean isOpen;

    // Конструктор класса Box
    public Box(int width, int height, int depth, String color) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.color = color;
        this.isOpen = false;
        this.item = null;
    }

    // Метод для открытия коробки
    public void open() {
        isOpen = true;
        System.out.println("Коробка открыта.");
    }

    // Метод для закрытия коробки
    public void close() {
        isOpen = false;
        System.out.println("Коробка закрыта.");
    }

    // Метод для перекраски коробки
    public void repaint(String newColor) {
        color = newColor;
        System.out.println("Коробка перекрашена в цвет: " + color);
    }

    // Метод для подсчета информации о коробке
    public void printInfo() {
        System.out.println("Коробка: размер [" + width + "x" + height + "x" + depth + "], цвет: " + color + ", открыта: " + (isOpen ? "да" : "нет") + ", предмет: " + (item != null ? item : "отсутствует"));
    }

    // Метод для добавления предмета в коробку
    public void putItem(String newItem) {
        if (!isOpen) {
            System.out.println("Коробка закрыта. Откройте её, чтобы положить предмет.");
            return;
        }
        if (item != null) {
            System.out.println("Коробка уже содержит предмет: " + item);
            return;
        }
        item = newItem;
        System.out.println("Предмет \"" + newItem + "\" помещён в коробку.");
    }


    // Метод для удаления предмета из коробки
    public void takeOutItem() {
        if (!isOpen) {
            System.out.println("Коробка закрыта. Откройте её, чтобы вытащить предмет.");
            return;
        }
        if (item == null) {
            System.out.println("Коробка пуста, нет предмета для извлечения.");
            return;
        }
        System.out.println("Предмет \"" + item + "\" вытащен из коробки.");
        item = null;
    }
}



