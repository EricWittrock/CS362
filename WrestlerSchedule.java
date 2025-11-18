import java.io.*;
import java.util.*;

public class WrestlerSchedule implements DatabaseObject {
    private int id;
    private int eventId;
    private int wrestlerId;

    public WrestlerSchedule() {}

    public WrestlerSchedule(int eventId, int wrestlerId) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.eventId = eventId;
        this.wrestlerId = wrestlerId;

        System.out.println("Creating WrestlerSchedule: " + this.wrestlerId);

        DataCache.addWrestlerSchedule(this);
    }

    public int getEventId() {
        return eventId;
    }

    public int getWrestlerId() {
        return wrestlerId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + eventId + "," + wrestlerId;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 3);
        this.id = Integer.parseInt(parts[0]);
        this.eventId = Integer.parseInt(parts[1]);
        this.wrestlerId = Integer.parseInt(parts[2]);
    }
}