import java.io.*;
import java.util.*;

public class Wrestler implements DatabaseObject{
    private int id;
    private String name;
    private String specialty;

    public Wrestler() {}

    public Wrestler(String name, String specialty) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.specialty = specialty;

        DataCache.addWrestler(this);
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + name + "," + specialty;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 3);
        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.specialty = parts[2];
    }

    @Override
    public String toString() {
        return "Wrestler{id=" + id + ", name='" + name + "', specialty='" + specialty + "'}";
    }
}