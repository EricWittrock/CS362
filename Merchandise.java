public class Merchandise implements DatabaseObject
{
    private int id = 0;
    private String name;
    private int price;
    private int quantity;

    public Merchandise(String name, int price)
    {
        id++;
        this.name = name;
        this.price = price;
    }

    public int getPrice()
    {
        return price;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int getId(){
        return id;
    };

    @Override
    public String serialize(){
        return id + "," + name + "," + price; 
    };

    @Override
    public void deserialize(String data){
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.price = Integer.parseInt(parts[2]);
    };

}