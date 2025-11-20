import java.util.Random;

public class Merchandise implements DatabaseObject
{
    private int id;
    private String name;
    private MerchType type;
    private int unitPrice;
    private String supplier;

    public Merchandise(){}
    
    public Merchandise(String name, MerchType type, int unitPrice)
    {
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.type = type;
        this.unitPrice = unitPrice;
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

    public String getSupplier(){
        return supplier;
    }

    public void setSupplier(String supplier)
    {
        this.supplier = supplier;
    }

    public String print()
    {
        return id + " " + name + "\n" + unitPrice;
    }

    public static MerchType getMerchTypeFromString(String name)
    {
        for (MerchType type : MerchType.values())
        {
            if(type.toString().equalsIgnoreCase(name.trim()))
            {
                return type;
            }
        }
        return null;
    }

    @Override
    public int getId(){
        return id;
    };

    @Override
    public String serialize(){
        return id + "," + name  + "," + type.toString() + "," + unitPrice + "," + supplier; 
    };

    @Override
    public void deserialize(String data){
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.type = getMerchTypeFromString(parts[2]);
        this.unitPrice = Integer.parseInt(parts[3]);
        this.supplier = parts[4];
    }
}