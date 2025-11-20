import java.util.*;

public class Worker implements DatabaseObject {
    private int workerId;
    private String name;
    private String department;
    private int hourlyRate;

    public Worker() {
    }

    public Worker(String name, String department, int hourlyRate) {
        this.workerId = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.department = department;
        this.hourlyRate = hourlyRate;

        DataCache.addObject(this);
    }

    public int getWorkerId() {
        return workerId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public int getId() {
        return workerId;
    }

    @Override
    public String serialize() {
        return workerId + "," + name + "," + department + "," + hourlyRate;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 4);
        this.workerId = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.department = parts[2];
        this.hourlyRate = Integer.parseInt(parts[3]);
    }
}
