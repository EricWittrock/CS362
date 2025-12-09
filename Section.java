import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Section implements DatabaseObject {
    private int id;
    private String name;
    private int numSeats;
    private int price;
    private ArrayList<Boolean> takenSeats;

    public Section() {}

    public Section(String name, int numSeats, int price) {
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.numSeats = numSeats;
        this.price = price;
        this.takenSeats = new ArrayList<Boolean>(numSeats);
        for (int i = 0; i < numSeats; i++) {
            takenSeats.add(false);
        }

        DataCache.addObject(this);
    }

    public String getName() {
        return name;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public int getPrice() {
        return price;
    }

    public boolean tryTakeSeat(int seatNum) {
        if(takenSeats.get(seatNum - 1))
            return false;
        takenSeats.set(seatNum - 1, true);
        return true;
    }

    @Override
    public String serialize() {
        return id + "," + name + "," + numSeats + "," + price + "," + String.join(",", takenSeats.stream().map(Object::toString).toArray(String[]::new));
    }

    @Override
    public void deserialize(String data) {   
        String[] parts = data.split(",");
        id = Integer.parseInt(parts[0]);
        name = parts[1];
        numSeats = Integer.parseInt(parts[2]);
        price = Integer.parseInt(parts[3]);
        takenSeats = new ArrayList<Boolean>(numSeats);
        for (int i = 0; i < numSeats; i++) {
            takenSeats.add(parts[4 + i].equals("true"));
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
