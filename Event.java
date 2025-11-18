import java.util.*;

public class Event implements DatabaseObject {
    private int eventId;
    private String date;
    private Venue venue;

    public Event() {}

    public Event(String date, Venue venue) {
        this.eventId = new Random().nextInt(Integer.MAX_VALUE);
        this.date = date;
        this.venue = venue;

        DataCache.addEvent(this);
    }

    public String getDate() {
        return date.toString();
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List<Section> getSections() {
        return venue.getSections();
    }

    public int getEventId() {
        return eventId;
    }

    public String getLocationName() {
        return venue != null ? venue.getName() : "Unknown";
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(eventId).append(",").append(date).append(",").append(venue.getId()).append("\n");
        return sb.toString();
    }

    @Override
    public void deserialize(String data) { 
        String[] parts = data.split(",", 3);
        this.eventId = Integer.parseInt(parts[0]);
        this.date = parts[1];
        int venueId = Integer.parseInt(parts[2]);
        this.venue = DataCache.getVenueById(venueId);
        System.out.println(venueId);
        System.out.println(this.venue.getName());
    }

    @Override
    public int getId() {
        return eventId;
    }
}
