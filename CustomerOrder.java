import java.util.*;

public class CustomerOrder implements DatabaseObject {
    private int id;
    private boolean paid;
    private String customerName;
    private int quantity;
    private int itemId;

    public CustomerOrder(){}

    public CustomerOrder(String customerName, int quantity, int itemId)
    {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.customerName = customerName;
        this.quantity = quantity;
        this.paid = false;
        this.itemId = itemId;
        DataCache.addObject(this);
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public boolean getPaid()
    {
        return paid;
    }

    public void setPaid(boolean paid)
    {
        this.paid = paid;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public int getItemId()
    {
        return itemId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + customerName + "," + quantity + "," + itemId + "," + paid;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.customerName = parts[1];
        this.quantity = Integer.parseInt(parts[2]);
        this.itemId = Integer.parseInt(parts[3]);
    }   
}
