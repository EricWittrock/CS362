import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class StaticDataHandler {
    private static final String CityDataPath = "./StaticData/CityData.txt";
    
    private static ArrayList<City> cities = null;

    public static City getCityByName(String name) {
        if (cities == null) {
            loadCities();
        }
        for (City city : cities) {
            if (city.getName().equalsIgnoreCase(name)) {
                return city;
            }
        }
        return null;
    }

    public static City getCityById(int id) {
        if (cities == null) {
            loadCities();
        }
        for (City city : cities) {
            if (city.getId() == id) {
                return city;
            }
        }
        return null;
    }

    public static ArrayList<City> fuzzySearchCityNames(String name, int maxResults) {
        if (cities == null) {
            loadCities();
        }

        // sort cities by similarity to name
        ArrayList<String> cityNames = new ArrayList<>();
        for (City city : cities) {
            double similarity = getSimilarity(city.getName(), name);
            cityNames.add(city.getName() + "," + similarity);
        }

        cityNames.sort((a, b) -> {
            double simA = Double.parseDouble(a.split(",")[1]);
            double simB = Double.parseDouble(b.split(",")[1]);
            if (simA > simB) return -1;
            else if (simA < simB) return 1;
            else return 0;
        });

        ArrayList<City> results = new ArrayList<>();
        for (int i = 0; i < Math.min(maxResults, cityNames.size()); i++) {
            String cityName = cityNames.get(i).split(",")[0];
            City city = getCityByName(cityName);
            if (city != null) {
                results.add(city);
            }
        }

        return results;
    }

    public static double getSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        double[] kernel1 = new double[maxLen];
        double[] kernel2 = new double[maxLen];

        for (int i = 0; i < maxLen; i++) {
            kernel1[i] = ((double) i / maxLen) * 0.2 + 0.8;
            kernel2[i] = 1.0 - ((double) i / maxLen) * 0.2;
        }

        double cos1 = cosSimilarity(s1, s2, kernel1);
        double cos2 = cosSimilarity(s1, s2, kernel2);

        return (cos1 + cos2) / 2.0;
    }

    private static double cosSimilarity(String s1, String s2, double[] kernel) {
        CharVector v1 = new CharVector(new double[26]);
        CharVector v2 = new CharVector(new double[26]);

        for (int i = 0; i < s1.length(); i++) {
            char c = s1.charAt(i);
            CharVector cv = new CharVector(c);
            cv.scale(kernel[i]);
            v1.add(cv);
        }

        for (int i = 0; i < s2.length(); i++) {
            char c = s2.charAt(i);
            CharVector cv = new CharVector(c);
            cv.scale(kernel[i]);
            v2.add(cv);
        }

        double dot = v1.dot(v2);
        double cos = dot / (v1.magnitude() * v2.magnitude());

        return cos;
    }

    private static void loadCities() {
        cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CityDataPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines
                City city = parseCity(line);
                if (city != null) {
                    cities.add(city);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static City parseCity(String line) {
        String[] parts = line.split(",");
        if (parts.length != 6) {
            return null;
        }
        String name = parts[0].trim();
        String country = parts[1].trim();
        double lat = Double.parseDouble(parts[2].trim());
        double lng = Double.parseDouble(parts[3].trim());
        int population = Integer.parseInt(parts[4].trim());
        int id = Integer.parseInt(parts[5].trim());
        return new City(name, country, population, lat, lng, id);
    }
}
