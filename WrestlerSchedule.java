import java.util.*;

public class WrestlerSchedule implements DatabaseObject {
    private int id;
    private int eventId;
    private int wrestlerId;
    private int rehearsalId;  // -1 if this is for an event, otherwise rehearsal ID
    private String type;      // "EVENT" or "REHEARSAL"

    public WrestlerSchedule() {}

    public WrestlerSchedule(int eventId, int wrestlerId) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.eventId = eventId;
        this.wrestlerId = wrestlerId;
        this.rehearsalId = -1;
        this.type = "EVENT";

        System.out.println("Creating WrestlerSchedule: " + this.wrestlerId);

        DataCache.addObject(this);
    }

    // Constructor for rehearsal schedule
    public WrestlerSchedule(int wrestlerId, int rehearsalId, String type) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.wrestlerId = wrestlerId;
        this.rehearsalId = rehearsalId;
        this.eventId = -1;
        this.type = type;

        DataCache.addObject(this);
    }

    public int getEventId() {
        return eventId;
    }

    public int getWrestlerId() {
        return wrestlerId;
    }

    public int getRehearsalId() {
        return rehearsalId;
    }

    public String getType() {
        return type;
    }

    public boolean isRehearsal() {
        return "REHEARSAL".equals(type);
    }

    public boolean isEvent() {
        return "EVENT".equals(type);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + eventId + "," + wrestlerId + "," + rehearsalId + "," + type;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 5);
        this.id = Integer.parseInt(parts[0]);
        this.eventId = Integer.parseInt(parts[1]);
        this.wrestlerId = Integer.parseInt(parts[2]);
        this.rehearsalId = Integer.parseInt(parts[3]);
        this.type = parts[4];
    }
}