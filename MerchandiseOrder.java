public class MerchandiseOrder implements DatabaseObject
{
    private int id = 0;
    private Merchandise merchandise;
    private int quantity;
    private Venue venue;

    public MerchandiseOrder(){}

    public MerchandiseOrder(Merchandise merchandise, int quantity, Venue venue)
    {
        id++;
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
        return id + "," + merchandise.getId()
        + "," + quantity + "," + venue.getId(); 
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.merchandise = DataCache.getById(Integer.parseInt(parts[1]), Merchandise::new);
        this.quantity = Integer.parseInt(parts[2]);
        this.venue = DataCache.getById(Integer.parseInt(parts[3]), Venue::new);
    }
    
}