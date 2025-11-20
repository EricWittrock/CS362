import java.util.Random;

public class TravelPlan implements DatabaseObject {
    private int id;
    private int eventId;
    private String destinationCityName;
    private String departureCityName;
    private String departureDate;
    private boolean byBus;
    private String hotelName;
    private int numHotelRooms;
    private String hotelDate;
    private int numNights;

    public TravelPlan() {}

    public TravelPlan(int eventId) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.eventId = eventId;
        DataCache.addObject(this);
    }

    public int getEventId() {
        return eventId;
    }

    public void removeEventId() {
        this.eventId = -1;
    }

    public void setDestinationCityName(String name) {
        this.destinationCityName = name;
    }

    public void setDepartureCityName(String name) {
        this.departureCityName = name;
    }

    public void setDepartureDate(String date) {
        this.departureDate = date;
    }

    public void setByBus(boolean byBus) {
        this.byBus = byBus;
    }

    public void setHotelName(String name) {
        this.hotelName = name;
    }

    public void setNumHotelRooms(int numRooms) {
        this.numHotelRooms = numRooms;
    }

    public void setHotelDate(String date) {
        this.hotelDate = date;
    }

    public void setNumNights(int nights) {
        this.numNights = nights;
    }

    @Override
    public int getId() {
        return id;
    }
    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
          .append(eventId).append(",")
          .append(destinationCityName).append(",")
          .append(departureCityName).append(",")
          .append(departureDate).append(",")
          .append(byBus).append(",")
          .append(hotelName).append(",")
          .append(numHotelRooms).append(",")
          .append(hotelDate).append(",")
          .append(numNights).append("\n");
        return sb.toString();
    }
    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 10);
        this.id = Integer.parseInt(parts[0].trim());
        this.eventId = Integer.parseInt(parts[1].trim());
        this.destinationCityName = parts[2].trim();
        this.departureCityName = parts[3].trim();
        this.departureDate = parts[4].trim();
        this.byBus = Boolean.parseBoolean(parts[5].trim());
        this.hotelName = parts[6].trim();
        this.numHotelRooms = Integer.parseInt(parts[7].trim());
        this.hotelDate = parts[8].trim();
        this.numNights = Integer.parseInt(parts[9].trim());
    }

    public int calculateTravelCost() {
        if (destinationCityName == null || departureCityName == null) {
            return 0;
        }

        City destinationCity = StaticDataHandler.getCityByName(destinationCityName);
        if (destinationCity == null) {
            System.out.println("Error: Could not find city " + destinationCityName);
            return 0;
        }
        City headquarters = StaticDataHandler.getCityByName(departureCityName);
        if (headquarters == null) {
            System.out.println("Error: Could not find headquarters city " + departureCityName);
            return 0;
        }
        
        double distance = headquarters.distanceTo(destinationCity);

        if(byBus) {
            return (int)(distance * 0.5) + 1000; // bus cost
        } else {
            return (int)(distance * 0.7) + 5000; // flight cost
        }
    }

    public int calculateHotelCost() {
        if (destinationCityName == null || hotelName == null || numHotelRooms <= 0 || numNights <= 0) {
            return 0;
        }

        City destinationCity = StaticDataHandler.getCityByName(destinationCityName);
        if (destinationCity == null) {
            System.out.println("Error: Could not find city " + destinationCityName);
            return 0;
        }

        // bigger cities are more expensive
        int cityPopulation = destinationCity.getPopulation();

        int cost = (int)(Math.log((double)cityPopulation) * 10.0 * numHotelRooms * numNights);
        return cost;
    }
}
