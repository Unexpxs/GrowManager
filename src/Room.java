package src;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String name;
    private final List<Plant> plants;

    public Room(String name) {
        this.name = name;
        this.plants = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Plant> getPlants() {
        return plants;
    }
}
