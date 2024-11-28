package ru.otus.java.basic.homeworks.hw7;

class Human {
    String name;
    private Transport currentTransport;
    double endurance;

    public Human(String name, double endurance) {
        this.name = name;
        this.endurance = endurance;
    }

    public void sit(Transport transport) {
        if (currentTransport != null){
            this.currentTransport = transport;
            transport.setDriver(this);
            switch (currentTransport.getClass().getSimpleName()) {
                case "Car" -> System.out.println(name + " пересел в машину");
                case "Horse" -> System.out.println(name + " пересел на лошадь");
                case "Bicycle" -> System.out.println(name + " пересел на велосипед");
                case "AllTerrainVehicle" -> System.out.println(name + " пересел в вездеход");
                default -> throw new IllegalStateException("Unexpected value: " + currentTransport.getClass().getSimpleName());
            }
            return;
        }
        this.currentTransport = transport;
        transport.setDriver(this);
        switch (currentTransport.getClass().getSimpleName()) {
            case "Car" -> System.out.println(name + " сел в машину");
            case "Horse" -> System.out.println(name + " сел на лошадь");
            case "Bicycle" -> System.out.println(name + " сел на велосипед");
            case "AllTerrainVehicle" -> System.out.println(name + " сел в вездеход");
            default -> throw new IllegalStateException("Unexpected value: " + currentTransport.getClass().getSimpleName());
        }
    }

    public void stand() {
        System.out.println(name + " спешился");
        this.currentTransport = null;
    }

    public boolean move(double distance, Terrain terrain) {
        if (currentTransport != null) {
            return currentTransport.move(distance, terrain);
        } else {
            endurance -= distance;
            System.out.println(name + " идет пешком " + distance + " километров по " + terrain.getTitle());
            return true;
        }
    }

    public void getInfo() {
        System.out.println("Выносливость " + name + ": " + endurance);
    }
}
