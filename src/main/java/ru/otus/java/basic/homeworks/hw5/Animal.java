package ru.otus.java.basic.homeworks.hw5;

public class Animal {
    String name;
    double runSpeed;
    double swimSpeed;
    int endurance;
    int enduranceCost;

    public Animal(String name, double runSpeed, double swimSpeed, int endurance) {
        this.name = name;
        this.runSpeed = runSpeed;
        this.swimSpeed = swimSpeed;
        this.endurance = endurance;
    }

    public double run(int distance) {
        double time = -1;
        int newDistance = endurance;
        if (endurance == 0) {
            System.out.println(name + " устал(а) и не может бежать.");
            return time;
        }
        if (endurance < distance) {
            time = endurance / runSpeed;
            System.out.println(name + " смог(ла) пробежать только " + newDistance + " метров из " + distance + " за " + time + " секунд. Закончилась выносливость");
            endurance = 0;
        } else {
            time = distance / runSpeed;
            endurance -= distance;
            System.out.println(name + " пробежал(а) " + distance + " метров за " + time + " секунд.");
        }
        return time;
    }

    public double swim(int distance) {
        double time = -1;
        if (endurance == 0) {
            System.out.println(name + " устал(а) и не может плыть.");
            return time;
        }
        if (endurance < enduranceCost) {
            System.out.println(name + " не может плыть такую дистанцию. Не хватает выносливости.");
            return time;
        }
        time = distance / swimSpeed;
        endurance -= enduranceCost;
        System.out.println(name + " проплыл(а) " + distance + " метров за " + time + " секунд. Было потрачено " + enduranceCost + " выносливости.");
        return time;
    }

    public void info() {
        System.out.println("Имя: " + name);
        System.out.println("Выносливость: " + endurance);
    }
}
