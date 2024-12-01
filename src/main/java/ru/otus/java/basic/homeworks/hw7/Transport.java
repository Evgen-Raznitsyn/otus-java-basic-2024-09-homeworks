package ru.otus.java.basic.homeworks.hw7;

public interface Transport {
    boolean move(double distance, Terrain terrain);
    void setDriver(Human human);
    String getTransportName();
}
