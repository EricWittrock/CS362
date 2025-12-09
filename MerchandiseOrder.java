import java.util.Random;

public class MerchandiseOrder implements DatabaseObject
{
    private int id;
    private Merchandise merchandise;
    private int quantity;

    public MerchandiseOrder(){}

    public MerchandiseOrder(Merchandise merchandise, int quantity)
    {
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.merchandise = merchandise;
        this.quantity = quantity;
        DataCache.addObject(this);
    }

    public Merchandise getMerchandise() {
        return merchandise;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void print()
    {
        System.out.println("Merchandise: " + merchandise );
        System.out.println("Unit Price: " + merchandise.getUnitPrice());
        System.out.println("Merchandise Type: " + merchandise.getType());
        System.out.println("Venue: " + merchandise.getVenue().getName());
        System.out.println("Quantity: " + quantity);
        System.out.println("Total: $" + quantity * merchandise.getUnitPrice());
        System.out.println("---------------------------\n");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + merchandise.getName() + "," + quantity; 
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.merchandise = DataCache.getByFilter(m -> m.getName()
            .equalsIgnoreCase(parts[1]), Merchandise::new);
        this.quantity = Integer.parseInt(parts[2]);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}