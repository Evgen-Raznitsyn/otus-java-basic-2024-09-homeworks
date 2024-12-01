package ru.otus.java.basic.homeworks.hw7;

class Human {
    private String name;
    private Transport currentTransport;
    private double endurance;

    public Human(String name, double endurance) {
        this.name = name;
        this.endurance = endurance;
    }

    public String getName() {
        return name;
    }

    public double getEndurance() {
        return endurance;
    }

    public void setEndurance(double endurance) {
        this.endurance = endurance;
    }

    public void sit(Transport transport) {
        if (currentTransport != null) {
            currentTransport.setDriver(null);
            System.out.println(name + " пересел с " + currentTransport.getTransportName() + " на " + transport.getTransportName());
        } else {
            System.out.println(name + " сел на " + transport.getTransportName());
        }
        this.currentTransport = transport;
        transport.setDriver(this);
    }

    public void stand() {
        currentTransport.setDriver(null);
        System.out.println(name + " спешился");
        this.currentTransport = null;
    }

    public boolean move(double distance, Terrain terrain) {
        if (currentTransport != null) {
            return currentTransport.move(distance, terrain);
        }
        endurance -= distance;
        System.out.println(name + " идет пешком " + distance + " километров по " + terrain.getTitle());
        return true;
    }

    public void getInfo() {
        System.out.println("Выносливость " + name + ": " + endurance);
    }
}
