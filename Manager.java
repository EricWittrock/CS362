import java.util.*;

class Manager implements Actor {
    private static final String WRESTLER_FILE = "wrestlers.txt";
    private static final String SCHEDULE_FILE = "wrestlerSchedules.txt";

    public Manager() {
    }

    @Override
    public void showOptions() {
        System.out.println("\nManager Options:");

        while (true) {
            System.out.println("0) Exit");
            System.out.println("1) Add Wrestler");
            System.out.println("2) View All Wrestlers");
            System.out.println("3) Schedule Wrestler for Event");
            System.out.println("4) View Event Schedule");
            System.out.print("Enter choice: ");
            int choice = UserInput.getIntInput(0, 4);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                addWrestler();
            } else if (choice == 2) {
                viewAllWrestlers();
            } else if (choice == 3) {
                scheduleWrestler();
            } else if (choice == 4) {
                viewEventSchedule();
            }
        }
    }

    private void addWrestler() {
        System.out.println("\n--- Add Wrestler ---");
        System.out.print("Enter wrestler name: ");
        String name = UserInput.getStringInput();
        System.out.print("Enter specialty (e.g., High-flyer, Powerhouse, Technical): ");
        String specialty = UserInput.getStringInput();

        if (name == null || name.isEmpty()) {
            System.out.println("Error: Wrestler name cannot be empty.");
            return;
        }

        Wrestler wrestler = new Wrestler(name, specialty);
        DataCache.addWrestler(wrestler);
        System.out.println("Wrestler added successfully with ID: " + wrestler.getId());
    }

    private void viewAllWrestlers() {
        System.out.println("\n--- All Wrestlers ---");
        List<Wrestler> wrestlers = DataCache.getAllWrestlers();
        
        if (wrestlers.isEmpty()) {
            System.out.println("No wrestlers in system.");
        } else {
            for (Wrestler w : wrestlers) {
                System.out.println("ID: " + w.getId() + " | Name: " + w.getName() + " | Specialty: " + w.getSpecialty());
            }
        }
    }

    private void scheduleWrestler() {
        System.out.println("\n--- Schedule Wrestler for Event ---");
        
        // Show available events
        List<Event> events = DataCache.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found in the system.");
            return;
        }

        System.out.println("\nAvailable Events:");
        for (Event e : events) {
            System.out.println("ID: " + e.getId() + " | Date: " + e.getDate() + " | Location: " + e.getLocationName());
        }

        System.out.print("\nEnter Event ID: ");
        int eventId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // Check if event exists
        Event event = DataCache.getEventById(eventId);
        if (event == null) {
            System.out.println("Error: Event not found.");
            return;
        }

        // Show available wrestlers
        viewAllWrestlers();

        System.out.print("\nEnter Wrestler ID: ");
        int wrestlerId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // Check if wrestler exists
        Wrestler wrestler = DataCache.getWrestlerById(wrestlerId);
        if (wrestler == null) {
            System.out.println("Error: Wrestler not found.");
            return;
        }

        // Check if already scheduled
        List<WrestlerSchedule> schedules = DataCache.getAllWrestlerSchedules();
        for (WrestlerSchedule ws : schedules) {
            if (ws.getEventId() == eventId && ws.getWrestlerId() == wrestlerId) {
                System.out.println("Error: Wrestler already scheduled for this event.");
                return;
            }
        }

        WrestlerSchedule schedule = new WrestlerSchedule(eventId, wrestlerId);
        DataCache.addWrestlerSchedule(schedule);
        System.out.println("Wrestler " + wrestler.getName() + " scheduled successfully for event on " + event.getDate());
    }

    private void viewEventSchedule() {
        System.out.println("\n--- View Event Schedule ---");
        System.out.print("Enter Event ID: ");
        int eventId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // Check if event exists
        Event event = DataCache.getEventById(eventId);
        if (event == null) {
            System.out.println("Error: Event not found.");
            return;
        }

        System.out.println("\nEvent: " + event.getDate() + " at " + event.getLocationName());
        System.out.println("Scheduled Wrestlers:");

        List<WrestlerSchedule> schedules = DataCache.getAllWrestlerSchedules();
        boolean found = false;

        for (WrestlerSchedule ws : schedules) {
            if (ws.getEventId() == eventId) {
                Wrestler wrestler = DataCache.getWrestlerById(ws.getWrestlerId());
                if (wrestler != null) {
                    System.out.println("  - " + wrestler.getName() + " (" + wrestler.getSpecialty() + ")");
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("  No wrestlers scheduled for this event.");
        }
    }
}