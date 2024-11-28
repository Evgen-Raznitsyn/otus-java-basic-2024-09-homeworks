package ru.otus.java.basic.homeworks.hw7;

public enum Terrain {
    GROUND_FOREST("Густому лесу"),
    PLAINS("Равнине"),
    SWAMP("Болоту");

    private final String title;

    Terrain(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}


