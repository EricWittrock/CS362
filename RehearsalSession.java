import java.util.*;
import java.util.stream.Collectors;

public class RehearsalSession implements DatabaseObject {
    private int rehearsalId;
    private int scriptId;
    private int eventId;
    private int venueId;
    private long scheduledDate;
    private int duration;
    private List<Integer> wrestlerIds;
    private double cost;
    private RehearsalStatus status;
    private String notes;

    public RehearsalSession() {
        this.wrestlerIds = new ArrayList<>();
        this.status = RehearsalStatus.SCHEDULED;
        this.notes = "";
    }

    public RehearsalSession(int scriptId, int eventId, int venueId, long scheduledDate,
                           int duration, List<Integer> wrestlerIds, double cost, String notes) {
        this.rehearsalId = new Random().nextInt(Integer.MAX_VALUE);
        this.scriptId = scriptId;
        this.eventId = eventId;
        this.venueId = venueId;
        this.scheduledDate = scheduledDate;
        this.duration = duration;
        this.wrestlerIds = new ArrayList<>(wrestlerIds);
        this.cost = cost;
        this.status = RehearsalStatus.SCHEDULED;
        this.notes = notes;

        DataCache.addObject(this);
    }

    // getters
    public int getRehearsalId() { return rehearsalId; }
    public int getScriptId() { return scriptId; }
    public int getEventId() { return eventId; }
    public int getVenueId() { return venueId; }
    public long getScheduledDate() { return scheduledDate; }
    public int getDuration() { return duration; }
    public List<Integer> getWrestlerIds() { return wrestlerIds; }
    public double getCost() { return cost; }
    public RehearsalStatus getStatus() { return status; }
    public String getNotes() { return notes; }

    // setters
    public void setStatus(RehearsalStatus status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int getId() {
        return rehearsalId;
    }

    @Override
    public String serialize() {
        String wrestlerIdsStr = wrestlerIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(";"));

        return rehearsalId + "," +
                scriptId + "," +
                eventId + "," +
                venueId + "," +
                scheduledDate + "," +
                duration + "," +
                wrestlerIdsStr + "," +
                cost + "," +
                status.name() + "," +
                (notes != null && !notes.isEmpty() ? notes : "NONE");
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 10);
        this.rehearsalId = Integer.parseInt(parts[0]);
        this.scriptId = Integer.parseInt(parts[1]);
        this.eventId = Integer.parseInt(parts[2]);
        this.venueId = Integer.parseInt(parts[3]);
        this.scheduledDate = Long.parseLong(parts[4]);
        this.duration = Integer.parseInt(parts[5]);

        this.wrestlerIds = new ArrayList<>();
        if (!parts[6].isEmpty()) {
            String[] wrestlerIdParts = parts[6].split(";");
            for (String idStr : wrestlerIdParts) {
                wrestlerIds.add(Integer.parseInt(idStr));
            }
        }

        this.cost = Double.parseDouble(parts[7]);
        this.status = RehearsalStatus.valueOf(parts[8]);
        this.notes = parts[9].equals("NONE") ? "" : parts[9];
    }
}
