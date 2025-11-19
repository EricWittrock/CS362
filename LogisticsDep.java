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
            System.out.print("Choose option: ");
            int choice = UserInput.getIntInput(0, 3);

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

        Venue venue = new Venue("Dummy Venue", locationName, 0);
        Event newEvent = new Event(date, venue);
        
        DataCache.addEvent(newEvent);
    }

    private void rentVenueQuery() {
        System.out.println("below are the existing events. Select one to rent a venue for:");
        List<Event> events = DataCache.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        
        System.out.println("0) Cancel");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println((i + 1) + ") Event ID: " + e.getId() + ", Date: " + e.getDate() + ", Location: " + e.getVenue().getLocation());
        }
        
        int choice = UserInput.getIntInput(0, events.size()) - 1;

        if (choice < 0) {
            return;
        }

        Event selectedEvent = events.get(choice);

        System.out.println("Enter venue name: ");
        String venueName = UserInput.getStringInput();
        System.out.println("Enter city location name: ");
        String locationName = UserInput.getStringInput();
        System.out.println("Enter rental cost: ");
        int cost = UserInput.getIntInput(0, Integer.MAX_VALUE);

        if (cost > budget) {
            System.out.println("Error: Not enough budget to rent this venue.");
            scheduleEventQuery();
            return;
        }

        Venue venue = new Venue(venueName, locationName, cost);
        budget -= cost;
        selectedEvent.setVenue(venue);

        System.out.println("Venue rented: " + venue.getName() + " in " + locationName + " for $" + cost);

    }

    private void setBudgetQuery() {
        System.out.print("Enter new budget: ");
        int newBudget = UserInput.getIntInput(0, Integer.MAX_VALUE);
        budget = newBudget;
        System.out.println("Budget set to " + budget);
    }

    private void OrganizeTransportation() {

    }

    private void OrganizeLogging() {

    }
}