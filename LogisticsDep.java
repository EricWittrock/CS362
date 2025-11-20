import java.util.ArrayList;
import java.util.List;

class LogisticsDep implements Actor {
    private int budget = 1000000;

    LogisticsDep() {
        
    }

    @Override
    public void showOptions() {
        System.out.println("\nLogistics Department Options:");
        
        while (true) {
			System.out.println("0) Exit");
            System.out.println("1) Schedule Event");
			System.out.println("2) Set Budget");
            System.out.println("3) Rent Venue");
            System.out.println("4) Organize Transportation");
            System.out.println("5) Organize Logging");
            System.out.print("Choose option: ");
            int choice = UserInput.getIntInput(0, 5);

            if(choice == 0) {
            	break;
            } else if(choice == 1) {
                scheduleEventQuery();
            } else if(choice == 2) {
                setBudgetQuery();
            } else if(choice == 3) {
                rentVenueQuery();
            }
        }
    }

    private void scheduleEventQuery() {
        System.out.println("Enter event date (YYYY-MM-DD): ");
        String date = UserInput.getStringInput();
        System.out.println("Enter city location name: ");
        String locationName = UserInput.getStringInput();

        City city = StaticDataHandler.getCityByName(locationName);
        if (city == null) {
            ArrayList<City> cities = StaticDataHandler.fuzzySearchCityNames(locationName, 5);
            OptionList options = new OptionList();

            while (true) {
                System.out.println("Did not find exact city match. Did you mean one of the following?");
                System.out.println("0) Exit");
                for (int i = 0; i < cities.size(); i++) {
                    System.out.println((i + 1) + ") " + cities.get(i).getName());
                }
                int choice = UserInput.getIntInput(0, cities.size());
                if (choice == 0) {
                    System.out.println("Event scheduling cancelled.");
                    return;
                } else {
                    city = cities.get(choice - 1);
                    locationName = city.getName();
                    System.out.println("Selected city: " + city.getName());
                    break;
                }
            }
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
            System.out.println("Venue rental cancelled.");
            return;
        }

    }

    private void OrganizeLogging() {

    }

}