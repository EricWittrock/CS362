import java.util.*;

public class WorkerPayment implements DatabaseObject {
    private int paymentId;
    private int workerId;
    private int basePay;
    private int overtimePay;
    private int hazardPay;
    private int totalPay;
    private long paymentDate;
    private String paymentPeriod;
    private int totalHours;


    public WorkerPayment() {}

    public WorkerPayment(int workerId, int basePay, int overtimePay,
                        int hazardPay, int totalHours, String period) {

        this.paymentId = new Random().nextInt(Integer.MAX_VALUE);
        this.workerId = workerId;
        this.basePay = basePay;
        this.overtimePay = overtimePay;
        this.hazardPay = hazardPay;
        this.totalPay = totalPay;
        this.paymentDate = System.currentTimeMillis();
        this.paymentPeriod = period;
        this.totalHours = totalHours;


        DataCache.addWorkerPayment(this);
    }

    public int getPaymentId() { return paymentId; }
    public int getWorkerId() { return workerId; }
    public double getBasePay() { return basePay; }
    public double getOvertimePay() { return overtimePay; }
    public double getHazardPay() { return hazardPay; }
    public double getTotalPay() { return totalPay; }
    public long getPaymentDate() { return paymentDate; }
    public String getPaymentPeriod() { return paymentPeriod; }
    public int getTotalHours() { return totalHours; }

    @Override
    public int getId() {
        return paymentId;
    }

    @Override
    public String serialize() {
        return paymentId + "|" + workerId + "|" + basePay + "|" + 
               overtimePay + "|" + hazardPay + "|" + totalPay + "|" + 
               paymentDate + "|" + paymentPeriod + "|" + totalHours;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split("\\|", 9);
        this.paymentId = Integer.parseInt(parts[0]);
        this.workerId = Integer.parseInt(parts[1]);
        this.basePay = Integer.parseInt(parts[2]);
        this.overtimePay = Integer.parseInt(parts[3]);
        this.hazardPay = Integer.parseInt(parts[4]);
        this.totalPay = Integer.parseInt(parts[5]);
        this.paymentDate = Long.parseLong(parts[6]);
        this.paymentPeriod = parts[7];
        this.totalHours = Integer.parseInt(parts[8]);
    }
}