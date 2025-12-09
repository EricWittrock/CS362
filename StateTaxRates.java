import java.util.*;

public class StateTaxRates {
    private static Map<String, StateTaxInfo> taxRates = new HashMap<>();

    static {
        taxRates.put("NY", new StateTaxInfo("NY", "New York", 0.0685, 0.04, 0.025));
        taxRates.put("CA", new StateTaxInfo("CA", "California", 0.093, 0.01, 0.034));
        taxRates.put("TX", new StateTaxInfo("TX", "Texas", 0.0, 0.0, 0.026));
        taxRates.put("FL", new StateTaxInfo("FL", "Florida", 0.0, 0.0, 0.027));
        taxRates.put("IL", new StateTaxInfo("IL", "Illinois", 0.0495, 0.0, 0.028));
    }

    public static StateTaxInfo getStateTaxInfo(City city) {
        String state = deriveStateFromCity(city.getName());
        return taxRates.getOrDefault(state, new StateTaxInfo(state, state, 0.0, 0.0, 0.0));
    }

    private static String deriveStateFromCity(String cityName) {
        if (cityName.contains("New York") || cityName.contains("Brooklyn"))
            return "NY";
        if (cityName.contains("Los Angeles") || cityName.contains("San"))
            return "CA";
        if (cityName.contains("Houston") || cityName.contains("Dallas"))
            return "TX";
        if (cityName.contains("Miami") || cityName.contains("Orlando"))
            return "FL";
        if (cityName.contains("Chicago"))
            return "IL";
        return "XX"; // Unknown
    }
}
