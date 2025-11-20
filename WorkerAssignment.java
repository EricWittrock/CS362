import java.util.*;

public class WorkerAssignment implements DatabaseObject{
    private int assignmentId;
    private int workerId;
    private int eventId;
    private int hoursWorked;
    private boolean isHazardous;

    public WorkerAssignment() {}

    public WorkerAssignment(int workerId, int eventId, int hoursWorked, boolean isHazardous) {
        this.assignmentId = new Random().nextInt(Integer.MAX_VALUE);
        this.workerId = workerId;
        this.eventId = eventId;
        this.hoursWorked = hoursWorked;
        this.isHazardous = isHazardous;

        DataCache.addObject(this);
    }

    public int getAssignmentId() { return assignmentId; }
    public int getWorkerId() { return workerId; }
    public int getEventId() { return eventId; }
    public int getHoursWorked() { return hoursWorked; }
    public boolean isHazardous() { return isHazardous; }

    @Override
    public int getId() {
        return assignmentId;
    }

    @Override
    public String serialize() {
        return assignmentId + "," + workerId + "," + eventId + "," + 
               hoursWorked + "," + isHazardous;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 5);
        this.assignmentId = Integer.parseInt(parts[0]);
        this.workerId = Integer.parseInt(parts[1]);
        this.eventId = Integer.parseInt(parts[2]);
        this.hoursWorked = Integer.parseInt(parts[3]);
        this.isHazardous = Boolean.parseBoolean(parts[4]);
    }

}