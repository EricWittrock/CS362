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
    private int netPay; 
    private int taxBreakdownId;


    public WorkerPayment() {}

    public WorkerPayment(int workerId, int basePay, int overtimePay,
                        int hazardPay, int totalHours, String period) {

        this.paymentId = new Random().nextInt(Integer.MAX_VALUE);
        this.workerId = workerId;
        this.basePay = basePay;
        this.overtimePay = overtimePay;
        this.hazardPay = hazardPay;
        this.totalPay = basePay + overtimePay + hazardPay;
        this.paymentDate = System.currentTimeMillis();
        this.paymentPeriod = period;
        this.totalHours = totalHours;


        DataCache.addObject(this);
    }

    public int getPaymentId() { return paymentId; }
    public int getWorkerId() { return workerId; }
    public int getBasePay() { return basePay; }
    public int getOvertimePay() { return overtimePay; }
    public int getHazardPay() { return hazardPay; }
    public int getTotalPay() { return totalPay; }
    public long getPaymentDate() { return paymentDate; }
    public String getPaymentPeriod() { return paymentPeriod; }
    public int getTotalHours() { return totalHours; }
    public int getNetPay() { return netPay; }
    public int getTaxBreakdownId() { return taxBreakdownId; }

    public void setTaxBreakdown(int taxBreakdownId, int netPay) {
        this.taxBreakdownId = taxBreakdownId;
        this.netPay = netPay;
        DataCache.addObject(this);
    }

    @Override
    public int getId() {
        return paymentId;
    }

    @Override
    public String serialize() {
        return paymentId + "," + workerId + "," + basePay + "," + 
               overtimePay + "," + hazardPay + "," + totalPay + "," + 
               paymentDate + "," + paymentPeriod + "," + totalHours + "," +
               netPay + "," + taxBreakdownId;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 11);
        this.paymentId = Integer.parseInt(parts[0]);
        this.workerId = Integer.parseInt(parts[1]);
        this.basePay = Integer.parseInt(parts[2]);
        this.overtimePay = Integer.parseInt(parts[3]);
        this.hazardPay = Integer.parseInt(parts[4]);
        this.totalPay = Integer.parseInt(parts[5]);
        this.paymentDate = Long.parseLong(parts[6]);
        this.paymentPeriod = parts[7];
        this.totalHours = Integer.parseInt(parts[8]);
        this.netPay = parts.length > 9 ? Integer.parseInt(parts[9]) : totalPay;
        this.taxBreakdownId = parts.length > 10 ? Integer.parseInt(parts[10]) : 0;
    }
}
