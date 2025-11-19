import java.util.Random;

public class ConcessionOrder implements DatabaseObject {
    private int id;
    private Concession concession;
    private float unitPrice;
    private String orderDate;
    private String ordererName;
    private String supplier;

    public ConcessionOrder() {}

    public ConcessionOrder(Concession concession, int unitPrice, String orderDate, String ordererName, String supplier) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.concession = concession;
        this.orderDate = orderDate;
        this.ordererName = ordererName;
        this.supplier = supplier;
        DataCache.addConcessionOrder(this);
    }

    public void print() {
        System.out.println("Orderer: " + ordererName);
        System.out.println("Supplier: " + supplier);
        System.out.println("Order Date: " + orderDate);
        System.out.println("Concession: " + concession.getName());
        System.out.println("Expiration: " + concession.getExpirationDate());
        System.out.println("Unit Price: " + unitPrice);
        System.out.println("Units: " + concession.getAmount());
        System.out.println("Total: $" + concession.getAmount()*unitPrice);
        System.out.println("---------------------------\n");
    }

    public String getOrderer() {
        return ordererName;
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + concession.getId() + "," + orderDate + "," + ordererName + "," + supplier;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        int concessionId = Integer.parseInt(parts[1]);
        this.concession = DataCache.getConcessionById(concessionId);
        this.orderDate = parts[2];
        this.ordererName = parts[3];
        this.supplier = parts[4];
    }
}