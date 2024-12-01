package ru.otus.java.basic.homeworks.hw7;

public class MainApp {
    public static void main(String[] args) {
        Human human = new Human("Евген", 50);
        Car car = new Car(60);
        Horse horse = new Horse(100);
        Bicycle bicycle = new Bicycle();
        AllTerrainVehicle atv = new AllTerrainVehicle(60);

        human.sit(car);
        human.move(100, Terrain.PLAINS);
        human.sit(horse);
        human.move(10, Terrain.SWAMP);
        human.stand();
        human.move(5, Terrain.PLAINS);
        human.sit(bicycle);
        human.move(5, Terrain.PLAINS);
        human.sit(horse);
        human.move(15, Terrain.PLAINS);
        human.sit(atv);
        human.move(45, Terrain.SWAMP);

        human.getInfo();
        horse.getInfo();
        atv.getInfo();
        car.getInfo();
    }
}
