import java.util.*;

public class WrestlerPayment implements DatabaseObject {
    private int paymentId;
    private int wrestlerId;
    private int eventId;
    private int basePay;
    private int bonusAmount;
    private int totalPay;
    private long paymentDate;
    private String paymentPeriod;
    private int numHighRiskActions;

    public WrestlerPayment() {
    }

    public WrestlerPayment(int wrestlerId, int eventId, int basePay, int bonusAmount,
            int totalPay, int numHighRiskActions) {
        this.paymentId = new Random().nextInt(Integer.MAX_VALUE);
        this.wrestlerId = wrestlerId;
        this.eventId = eventId;
        this.basePay = basePay;
        this.bonusAmount = bonusAmount;
        this.totalPay = totalPay;
        this.paymentDate = System.currentTimeMillis();
        this.numHighRiskActions = numHighRiskActions;

        Event event = DataCache.getById(eventId, Event::new);
        this.paymentPeriod = event != null ? "Event on " + event.getDate() : "Event ID " + eventId;

        DataCache.addObject(this);
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getWrestlerId() {
        return wrestlerId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getBasePay() {
        return basePay;
    }

    public int getBonusAmount() {
        return bonusAmount;
    }

    public int getTotalPay() {
        return totalPay;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentPeriod() {
        return paymentPeriod;
    }

    public int getNumHighRiskActions() {
        return numHighRiskActions;
    }

    @Override
    public int getId() {
        return paymentId;
    }

    @Override
    public String serialize() {
        return paymentId + "," + wrestlerId + "," + eventId + "," +
                basePay + "," + bonusAmount + "," + totalPay + "," +
                paymentDate + "," + paymentPeriod + "," + numHighRiskActions;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 9);
        this.paymentId = Integer.parseInt(parts[0]);
        this.wrestlerId = Integer.parseInt(parts[1]);
        this.eventId = Integer.parseInt(parts[2]);
        this.basePay = Integer.parseInt(parts[3]);
        this.bonusAmount = Integer.parseInt(parts[4]);
        this.totalPay = Integer.parseInt(parts[5]);
        this.paymentDate = Long.parseLong(parts[6]);
        this.paymentPeriod = parts[7];
        this.numHighRiskActions = Integer.parseInt(parts[8]);
    }

}
