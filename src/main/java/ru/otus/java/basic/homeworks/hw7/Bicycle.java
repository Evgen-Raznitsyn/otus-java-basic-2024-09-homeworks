package ru.otus.java.basic.homeworks.hw7;

class Bicycle implements Transport {
    private Human driver;

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (terrain == Terrain.SWAMP) {
            System.out.println("Велосипед не может ехать по болоту");
            return false;
        }
        if (driver.endurance < distance * 7) {
            System.out.println(driver.name + " не может проехать такую дистанцию на велосипеде, не хватает выносливости");
            return false;
        }
        driver.endurance -= distance * 7;
        System.out.println(driver.name + " на велосипеде проехал " + distance + " километров по " + terrain.getTitle() + " потратив " + distance * 7 + " выносливости");
        return true;
    }

    @Override
    public void setDriver(Human human) {
        this.driver = human;
    }
}
