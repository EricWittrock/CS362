import java.util.*;

public class Contract implements DatabaseObject {
    private int contractId;
    private int wrestlerId;
    private int basePay;
    private long startDate;
    private long endDate;
    private boolean isActive;

    public Contract() {
    }

    public Contract(int wrestlerId, int basePay, long startDate, long endDate) {
        this.contractId = new Random().nextInt(Integer.MAX_VALUE);
        this.wrestlerId = wrestlerId;
        this.basePay = basePay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
    }

    public int getContractId() {
        return contractId;
    }

    public int getWrestlerId() {
        return wrestlerId;
    }

    public int getBasePay() {
        return basePay;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setBasePay(int basePay) {
        this.basePay = basePay;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endDate;
    }

    @Override
    public int getId() {
        return contractId;
    }

    @Override
    public String serialize() {
        return contractId + "|" + wrestlerId + "|" + basePay + "|"
                + startDate + "|" + endDate + "|" + isActive;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split("\\|", 6);
        this.contractId = Integer.parseInt(parts[0]);
        this.wrestlerId = Integer.parseInt(parts[1]);
        this.basePay = Integer.parseInt(parts[2]);
        this.startDate = Long.parseLong(parts[3]);
        this.endDate = Long.parseLong(parts[4]);
        this.isActive = Boolean.parseBoolean(parts[5]);
    }

}