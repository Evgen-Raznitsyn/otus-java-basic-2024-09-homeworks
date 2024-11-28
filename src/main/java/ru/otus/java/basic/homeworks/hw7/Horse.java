package ru.otus.java.basic.homeworks.hw7;

class Horse implements Transport {
    private Human driver;
    private double endurance;

    public Horse(double endurance) {
        this.endurance = endurance;
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (terrain == Terrain.SWAMP) {
            System.out.println("Лошадь не может перемещаться по болоту");
            return false;
        }
        if (endurance < distance * 5) {
            System.out.println("У лошади не хватит выносливости преодолеть такую дистанцию");
            return false;
        }
        endurance -= distance * 5;
        System.out.println(driver.name + " верхом на лошади проскакал " + distance + " километров по " + terrain.getTitle());
        return true;
    }

    @Override
    public void setDriver(Human human) {
        this.driver = human;
    }

    public void getInfo() {
        System.out.println("Выносливость лошади: " + endurance);
    }
}
