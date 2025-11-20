import java.util.Random;

public class MerchandiseOrder implements DatabaseObject
{
    private int id;
    private Merchandise merchandise;
    private int quantity;
    private Venue venue;

    public MerchandiseOrder(){}

    public MerchandiseOrder(Merchandise merchandise, int quantity, Venue venue)
    {
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.merchandise = merchandise;
        this.quantity = quantity;
        this.venue = venue;
        DataCache.addObject(this);
    }

    public Merchandise getMerchandise() {
        return merchandise;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public Venue getVenue()
    {
        return venue;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + merchandise.getName()
        + "," + quantity + "," + venue.getLocation(); 
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.merchandise = DataCache.getByFilter(m -> m.getName()
            .equalsIgnoreCase(parts[1]), Merchandise::new);
        this.quantity = Integer.parseInt(parts[2]);
        this.venue = DataCache.getByFilter(v -> v.getLocation()
            .equalsIgnoreCase(parts[3]), Venue::new);
    }
    
}