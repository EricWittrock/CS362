import ../DatabaseObject;
import Venues.Venue;

public class Concession implements DatabaseObject {
    private int id;
    private String name;
    private int amount;
    private String expirationDate;
    private Venue venue;

    public Concession(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + name + "," + amount + "," + expirationDate;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.amount = Integer.parseInt(parts[2]);
        this.expirationDate = parts[3];
    }
}