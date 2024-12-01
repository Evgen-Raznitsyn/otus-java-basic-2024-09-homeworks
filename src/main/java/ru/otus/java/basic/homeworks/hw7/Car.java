package ru.otus.java.basic.homeworks.hw7;

public class Car implements Transport {
    private Human driver;
    private double fuel;

    public Car(double fuel) {
        this.fuel = fuel;
    }

    public String getTransportName() {
        return "Машина";
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (fuel <= 0) {
            System.out.println("Недостаточно бензина.");
            return false;
        }
        if (terrain == Terrain.GROUND_FOREST) {
            System.out.println("Машина не может ехать по густому лесу");
            return false;
        }
        if (terrain == Terrain.SWAMP) {
            System.out.println("Машина не может ехать по болоту");
            return false;
        }
        fuel -= distance * 0.06;
        System.out.println(driver.getName() + " на машине проехал " + distance + " километров по " + terrain.getTitle());
        return true;
    }

    @Override
    public void setDriver(Human human) {
        this.driver = human;
    }

    public void getInfo() {
        System.out.println("Количество топлива у машины: " + fuel);
    }
}
