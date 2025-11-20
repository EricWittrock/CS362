import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.crypto.Data;

class Venue implements DatabaseObject {
    private String name;
    private String location;
    private int cost;
    private int id;
    private List<Section> sections = new ArrayList<>();

    public Venue() {
        Section vip = new Section("VIP", 50, 200);
        Section general = new Section("General Admission", 200, 80);

        sections.add(vip);
        sections.add(general);
    }

    public Venue(String name, String location, int cost) {
        this.name = name;
        this.location = location;
        this.cost = cost;

        this.id = new Random().nextInt(Integer.MAX_VALUE);

        Section vip = new Section("VIP", 50, 200);
        Section general = new Section("General Admission", 200, 80);

        sections.add(vip);
        sections.add(general);

        DataCache.addObject(this);
    }

    public List<Section> getSections() {
        return sections;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        String s = id + "," + name + "," + location + "," + cost;
        return s;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 4);
        int eventId = Integer.parseInt(parts[0]);
        String name = parts[1];
        int cost = Integer.parseInt(parts[3]);
        this.id = eventId;
        this.name = name;
        this.location = parts[2];
        this.cost = cost;
    }

}