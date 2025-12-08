import java.util.Random;

public class ConcessionOrder implements DatabaseObject {
    private int id;
    private Concession concession;
    private int unitPrice;
    private String orderDate;
    private String buyer;
    private String supplier;

    public ConcessionOrder() {}

    public ConcessionOrder(Concession concession, int unitPrice, String orderDate, String ordererName, String supplier) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.concession = concession;
        this.unitPrice = unitPrice;
        this.orderDate = orderDate;
        this.buyer = ordererName;
        this.supplier = supplier;
        DataCache.addObject(this);
    }

    public void print() {
        System.out.println("Orderer: " + buyer);
        System.out.println("Supplier: " + supplier);
        System.out.println("Order Date: " + orderDate);
        System.out.println("Concession: " + concession.getName());
        System.out.println("Expiration: " + concession.getExpirationDate());
        System.out.println("Unit Price: $" + unitPrice);
        System.out.println("Units: " + concession.getAmount());
        System.out.println("Total: $" + concession.getAmount()*unitPrice);
        System.out.println("---------------------------\n");
    }

    public String getBuyer() {
        return buyer;
    }

    public String getDate() {
        return orderDate;
    }

    public Concession getConcession() {
        return concession;
    }

    public String getSupplier() {
        return supplier;
    }

    public int getUnitPrice()
    {
        return unitPrice;
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + concession.getId() + "," + orderDate + "," + buyer + "," + supplier;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        int concessionId = Integer.parseInt(parts[1]);
        this.concession = DataCache.getAll(Concession::new).stream()
            .filter(c -> c.getId() == concessionId)
            .findFirst()
            .orElse(null);
        this.orderDate = parts[2];
        this.buyer = parts[3];
        this.supplier = parts[4];
    }
}