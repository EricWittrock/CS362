import java.util.Random;

public class Budget implements DatabaseObject {
    private int id;
    private String name;
    private int totalBudget = 0;
    private int remainingBudget = 0;
    private int profits = 0;

    public Budget() {}

    public Budget(String name, int startingBudget) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.totalBudget = startingBudget;
        this.remainingBudget = startingBudget;
        DataCache.addObject(this);
    }

    public static Budget get(String name) {
        return DataCache.getByFilter(
            b -> b.getName().equalsIgnoreCase(name),
            Budget::new
        );
    }

    public String getName() {
        return name;
    }

    public int getTotalBudget()
    {
        return totalBudget;
    }

    public void profit(int amount) {
        profits += amount;
    }

    public int profits() {
        return profits;
    }

    public void fund(int amount) {
        totalBudget += amount;
        remainingBudget += amount;
    }

    public int funds() {
        return remainingBudget;
    }

    public void charge(int amount) {
        remainingBudget -= amount;
    }

    public void print() {
        System.out.println(name + " Budget:");
        System.out.println("Total Allocated Budget: " + totalBudget);
        System.out.println("Remaining Budget: " + remainingBudget);
        System.out.println("Profits: " + profits);
        System.out.println("-----------------------");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + name + "," + totalBudget + "," + remainingBudget + "," + profits;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.totalBudget = Integer.parseInt(parts[2]);
        this.remainingBudget = Integer.parseInt(parts[3]);
        this.profits = Integer.parseInt(parts[4]);
    }
}