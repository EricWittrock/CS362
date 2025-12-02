import java.util.ArrayList;
import java.util.List;

class LogisticsDep implements Actor {
    private int budget = 1000000;
    private String headquartersCity = "New York";

    LogisticsDep() {
        
    }

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.add("Schedule Event", this::scheduleEventQuery);
        options.add("Set Budget", this::setBudgetQuery);
        options.add("Rent Venue", this::rentVenueQuery);
        options.add("Create/Update Transportation Plan", this::OrganizeTransportation);
        options.add("Create/Update Logging Plan", this::OrganizeLogging);
        options.add("Change Headquarters City", this::changeHeadquartersCity);
        options.addExitOption("Exit");
        options.loopDisplayAndSelect("\nLogistics Department Options: ");
    }

    private void scheduleEventQuery() {
        System.out.println("Enter event date (YYYY-MM-DD): ");
        String date = UserInput.getStringInput();

        City city = UIFuzzySelectCity();
        if (city == null) {
            System.out.println("Event scheduling cancelled.");
            return;
        }

        Venue venue = new Venue("Empty", city.getName(), 0);
        Event newEvent = new Event(date, venue);
        
        DataCache.addObject(newEvent);
    }

    private Event UISelectEvent() {
        ArrayList<Event> events = DataCache.getAll(Event::new);

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return null;
        }else {
            System.out.println("Select and event.");
        }

        System.out.println("0) Cancel");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println((i + 1) + ") Event ID: " + e.getId() + ", Date: " + e.getDate() + ", Location: " + e.getVenue().getLocation());
        }

        int choice = UserInput.getIntInput(0, events.size()) - 1;

        if (choice < 0) {
            return null;
        }
        return events.get(choice);
    }

    private City UIFuzzySelectCity() {
        System.out.println("Enter city location name: ");
        String locationName = UserInput.getStringInput();

        City city = StaticDataHandler.getCityByName(locationName);
        if (city == null) {
            ArrayList<City> cities = StaticDataHandler.fuzzySearchCityNames(locationName, 7);
            OptionList options = new OptionList();

            while (true) {
                System.out.println("Did not find exact city match. Did you mean one of the following?");
                System.out.println("0) Cancel");
                for (int i = 0; i < cities.size(); i++) {
                    System.out.println((i + 1) + ") " + cities.get(i).getName());
                }
                int choice = UserInput.getIntInput(0, cities.size());
                if (choice == 0) {
                    System.out.println("cancelled.");
                    return null;
                } else {
                    city = cities.get(choice - 1);
                    locationName = city.getName();
                    System.out.println("Selected city: " + city.getName());
                    break;
                }
            }
        }
        return city;
    }

    private void rentVenueQuery() {
        Event selectedEvent = UISelectEvent();

        if (selectedEvent == null) {
            System.out.println("Venue rental cancelled.");
            return;
        }

        Venue currentVenue = selectedEvent.getVenue();

        System.out.print("Event is taking place in " + currentVenue.getLocation() + ". ");
        System.out.println("Enter venue name: ");
        String venueName = UserInput.getStringInput();
        System.out.println("Enter rental cost: ");
        int cost = UserInput.getIntInput(0, Integer.MAX_VALUE);

        if (cost > budget) {
            System.out.println("Error: Not enough budget to rent this venue.");
            scheduleEventQuery();
            return;
        }

        currentVenue.setName(venueName);
        budget -= cost;

        System.out.println("Venue rented: " + currentVenue.getName() + " in " + currentVenue.getLocation() + " for $" + cost);
    }

    private void setBudgetQuery() {
        System.out.print("Enter new budget: ");
        int newBudget = UserInput.getIntInput(0, Integer.MAX_VALUE);
        budget = newBudget;
        System.out.println("Budget set to " + budget);
    }

    private void OrganizeTransportation() {
        Event selectedEvent = UISelectEvent();

        if (selectedEvent == null) {
            System.out.println("Organize transportation cancelled.");
            return;
        }

        Venue v = selectedEvent.getVenue();

        City destinationCity = StaticDataHandler.getCityByName(v.getLocation());
        if (destinationCity == null) {
            System.out.println("Error: Could not find city " + v.getLocation());
            return;
        }
        City headquarters = StaticDataHandler.getCityByName(headquartersCity);
        if (headquarters == null) {
            System.out.println("Error: Could not find headquarters city " + headquartersCity);
            return;
        }
        
        double distance = headquarters.distanceTo(destinationCity);

        System.out.println(
            "You are traveling from " + headquarters.getName() +
            " to " + destinationCity.getName() +
            ", a distance of " + String.format("%.2f", distance) +
            " miles."
        );

        int eventId = selectedEvent.getId();
        TravelPlan tp = DataCache.getByFilter(t -> t.getEventId() == eventId, TravelPlan::new);
        if (tp == null) {
            System.out.println("No existing travel plan found for this event. Creating new travel plan.");
            tp = new TravelPlan(eventId);
            tp.setDepartureCityName(headquartersCity);
            tp.setDestinationCityName(selectedEvent.getVenue().getLocation());
        }

        System.out.println("Enter departure date (YYYY-MM-DD): ");
        String departureDate = UserInput.getStringInput();
        tp.setDepartureDate(departureDate);
        System.out.println("Travel by bus? (y/n): ");
        String byBusInput = UserInput.getStringInput().toLowerCase();
        boolean byBus = byBusInput.startsWith("y");
        tp.setByBus(byBus);

        int cost = tp.calculateTravelCost();
        System.out.println("Total travel cost: $" + cost);
        
        if (cost > budget) {
            System.out.println("Not enough budget to cover travel costs. Deleteing travel plan.");
            tp.removeEventId();
        }else {
            budget -= cost;
        }
    }

    private void OrganizeLogging() {
        Event selectedEvent = UISelectEvent();
        int eventId = selectedEvent.getId();
        TravelPlan tp = DataCache.getByFilter(t -> t.getEventId() == eventId, TravelPlan::new);

        if (tp == null) {
            System.out.println("No existing travel plan found for this event. Creating new travel plan.");
            tp = new TravelPlan(eventId);
            tp.setDepartureCityName(headquartersCity);
            tp.setDestinationCityName(selectedEvent.getVenue().getLocation());
        }

        int numWrestlersScheduled = 0;
        ArrayList<WrestlerSchedule> schedules = DataCache.getAll(WrestlerSchedule::new);
        for (WrestlerSchedule ws : schedules) {
            if (ws.getEventId() == eventId) {
                numWrestlersScheduled++;
            }
        }
        System.out.println("Number of wrestlers scheduled for this event: " + numWrestlersScheduled);

        System.out.println("Enter hotel name: ");
        String hotelName = UserInput.getStringInput();
        tp.setHotelName(hotelName);
        tp.setNumHotelRooms(numWrestlersScheduled);
        System.out.println("Enter hotel check-in date (YYYY-MM-DD): ");
        String date = UserInput.getStringInput();
        tp.setHotelDate(date);

        int cost = 0;
        do {
            System.out.println("Enter number of nights to stay: ");
            int numNights = UserInput.getIntInput(0, 365);
            tp.setNumNights(numNights);

            System.out.println(
                numWrestlersScheduled + " hotel rooms reserved at"
                + hotelName + " for "+ numNights + " nights"
            );

            cost = tp.calculateHotelCost();
            System.out.println("Total hotel cost: $" + cost);
            if (cost > budget) {
                System.out.println("Not enough budget to cover hotel costs. Please re-enter number of nights.");
            }
        } while (cost > budget);

        budget -= cost;
    }

    private void changeHeadquartersCity() {
        City city = UIFuzzySelectCity();
        if (city == null) {
            System.out.println("Change headquarters cancelled.");
            return;
        }

        headquartersCity = city.getName();
        System.out.println("Headquarters city changed to " + headquartersCity);
    }
}