package ru.otus.java.basic.homeworks.hw7;

class AllTerrainVehicle implements Transport {
    private Human driver;
    private double fuel;

    public AllTerrainVehicle(double fuel) {
        this.fuel = fuel;
    }

    @Override
    public String getTransportName() {
        return "Вездеход";
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (fuel <= 0) {
            System.out.println("Недостаточно бензина.");
            return false;
        }
        fuel -= distance * 0.2;
        System.out.println(driver.getName() + " проехал на вездеходе " + distance + " километров по " + terrain.getTitle());
        return true;
    }

    @Override
    public void setDriver(Human human) {
        this.driver = human;
    }

    public void getInfo() {
        System.out.println("Количество топлива у вездехода: " + fuel);
    }
}
