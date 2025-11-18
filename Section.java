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

        DataCache.addSection(this);
    }

    public String getName() {
        return name;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public double getPrice() {
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
        return name + "," + numSeats + "," + price + "," + String.join(",", takenSeats.stream().map(Object::toString).toArray(String[]::new));
    }

    @Override
    public void deserialize(String data) {   
        String[] parts = data.split(",");
        name = parts[0];
        numSeats = Integer.parseInt(parts[1]);
        price = Integer.parseInt(parts[2]);
        takenSeats = new ArrayList<Boolean>(numSeats);
        for (int i = 0; i < numSeats; i++) {
            takenSeats.add(parts[3 + i].equals("true"));
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
