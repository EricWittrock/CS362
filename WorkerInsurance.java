import java.util.*;

public class WorkerInsurance implements DatabaseObject {
    private int insuranceId;
    private int workerId;
    private int coverageAmount;
    private long expirationDate;
    private boolean coversHazardous;

    public WorkerInsurance() {
    }

    public WorkerInsurance(int workerId, int coverageAmount, long expirationDate, boolean coversHazardous) {
        this.insuranceId = new Random().nextInt(Integer.MAX_VALUE);
        this.workerId = workerId;
        this.coverageAmount = coverageAmount;
        this.expirationDate = expirationDate;
        this.coversHazardous = coversHazardous;

        DataCache.addObject(this);
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public int getCoverageAmount() {
        return coverageAmount;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public boolean coversHazardous() {
        return coversHazardous;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationDate;
    }

    @Override
    public int getId() {
        return insuranceId;
    }

    @Override
    public String serialize() {
        return insuranceId + "," + workerId + "," + coverageAmount + "," +
                expirationDate + "," + coversHazardous;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 5);
        this.insuranceId = Integer.parseInt(parts[0]);
        this.workerId = Integer.parseInt(parts[1]);
        this.coverageAmount = Integer.parseInt(parts[2]);
        this.expirationDate = Long.parseLong(parts[3]);
        this.coversHazardous = Boolean.parseBoolean(parts[4]);
    }
}
