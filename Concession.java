import java.util.Random;

public class Concession implements DatabaseObject {
    private int id;
    private String name;
    private int amount;
    private String expirationDate;
    private Venue venue;

    public Concession() {}

    public Concession(String name, int amount, String expirationDate, Venue venue) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.amount = amount;
        this.expirationDate = expirationDate;
        this.venue = venue;
        DataCache.addObject(this);
    }

    public void println() {
        System.out.println(
            name +
            ": Amount: " + amount +
            ", Expiration Date: " + expirationDate
        );
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public int getVenueId() {
        return venue.getId();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + name + "," + amount + "," + expirationDate + "," + venue.getId();
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0].trim());
        this.name = parts[1].trim();
        this.amount = Integer.parseInt(parts[2].trim());
        this.expirationDate = parts[3].trim();
        this.venue = DataCache.getById(Integer.parseInt(parts[4].trim()), Venue::new);
    }
}