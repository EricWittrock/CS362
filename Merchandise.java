import java.util.Random;

public class Merchandise implements DatabaseObject
{
    private int id;
    private String name;
    private MerchType type;
    private int unitPrice;
    private Venue venue;
    private int quantity;

    public Merchandise(){}
    
    public Merchandise(String name, MerchType type, int unitPrice, int quantity, Venue venue)
    {
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.type = type;
        this.unitPrice = unitPrice;
        this.venue = venue;
        this.quantity = quantity;
        DataCache.addObject(this);
    }

    public int getUnitPrice(){
        return unitPrice;
    }
    
    public String getName()
    {
        return name;
    }

    public MerchType getType()
    {
        return type;
    }

    public int getVenueId()
    {
        return venue.getId();
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void print()
    {
        System.out.println("Merchandise: " + name );
        System.out.println("Unit Price: $" + unitPrice);
        System.out.println("Merchandise Type: " + type);
        System.out.println("---------------------------\n");
    }

    @Override
    public int getId(){
        return id;
    };

    @Override
    public String serialize(){
        return id + "," + name  + "," + type.toString() + "," + quantity + "," + unitPrice + "," + venue.getId(); 
    };

    @Override
    public void deserialize(String data){
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.type = MerchType.valueOf(parts[2]);
        this.quantity = Integer.parseInt(parts[3]);
        this.unitPrice = Integer.parseInt(parts[4]);
        this.venue = DataCache.getById(Integer.parseInt(parts[5].trim()), Venue::new);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        DataCache.addObject(this);
    }
}