import java.util.Random;

public class MerchandiseOrder implements DatabaseObject
{
    private int id;
    private Merchandise merchandise;

    public MerchandiseOrder(){}

    public MerchandiseOrder(Merchandise merchandise)
    {
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.merchandise = merchandise;
        DataCache.addObject(this);
    }

    public Merchandise getMerchandise() {
        return merchandise;
    }

    public void print()
    {
        System.out.println("Merchandise: " + merchandise.getName());
        System.out.println("Unit Price: $" + merchandise.getUnitPrice());
        System.out.println("Merchandise Type: " + merchandise.getType());
        System.out.println("Venue: " + DataCache.getById(merchandise.getVenueId(), Venue::new).getName());
        System.out.println("Quantity: " + merchandise.getQuantity());
        System.out.println("Total: $" + merchandise.getQuantity() * merchandise.getUnitPrice());
        System.out.println("---------------------------\n");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + merchandise.getName(); 
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.merchandise = DataCache.getByFilter(m -> m.getName()
            .equalsIgnoreCase(parts[1].trim()), Merchandise::new);
    }
    
}