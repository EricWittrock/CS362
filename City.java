public class City {
    private String name;
    private String country;
    private int population;
    private double lat;
    private double lng;
    private int id;

    public City(String name, String country, int population, double lat, double lng, int id) {
        this.name = name;
        this.country = country;
        this.population = population;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double distanceTo(City other) {
        double R = 3959.0; // radius of earth in miles

        double myLat = this.lat * Math.PI / 180.0;
        double myLng = this.lng * Math.PI / 180.0;
        double otherLat = other.getLat() * Math.PI / 180.0;
        double otherLng = other.getLng() * Math.PI / 180.0;

        double dLat = otherLat - myLat;
        double dLng = otherLng - myLng;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(myLat) * Math.cos(otherLat) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}