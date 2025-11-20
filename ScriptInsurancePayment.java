import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ScriptInsurancePayment implements DatabaseObject {
    private int paymentId;
    private int scriptId;
    private int eventId;
    private int amount;
    private long paymentDate;
    private String paidBy; // Finance manager who processed it
    private String budgetName; // Which budget was charged

    public ScriptInsurancePayment() {
    }

    public ScriptInsurancePayment(int scriptId, int eventId, int amount, String paidBy, String budgetName) {
        this.paymentId = new Random().nextInt(Integer.MAX_VALUE);
        this.scriptId = scriptId;
        this.eventId = eventId;
        this.amount = amount;
        this.paidBy = paidBy;
        this.budgetName = budgetName;
        this.paymentDate = System.currentTimeMillis();
        DataCache.addObject(this);
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getScriptId() {
        return scriptId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getAmount() {
        return amount;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public String getBudgetName() {
        return budgetName;
    }

    @Override
    public int getId() {
        return paymentId;
    }

    @Override
    public String serialize() {
        return paymentId + "," +
                scriptId + "," +
                eventId + "," +
                amount + "," +
                paymentDate + "," +
                paidBy + "," +
                budgetName;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 7);
        this.paymentId = Integer.parseInt(parts[0]);
        this.scriptId = Integer.parseInt(parts[1]);
        this.eventId = Integer.parseInt(parts[2]);
        this.amount = Integer.parseInt(parts[3]);
        this.paymentDate = Long.parseLong(parts[4]);
        this.paidBy = parts[5];
        this.budgetName = parts[6];
    }

    public void print() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("  Script ID: " + scriptId);
        System.out.println("  Event ID: " + eventId);
        System.out.println("  Amount: $" + amount);
        System.out.println("  Payment Date: " + sdf.format(new Date(paymentDate)));
        System.out.println("  Paid By: " + paidBy);
        System.out.println("  Budget: " + budgetName);
        System.out.println();
    }
}
