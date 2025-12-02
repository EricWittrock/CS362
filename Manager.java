import java.util.*;

class Manager implements Actor {
    private static final String WRESTLER_FILE = "wrestlers.txt";
    private static final String SCHEDULE_FILE = "wrestlerSchedules.txt";

    public Manager() {
    }

    @Override
    public void showOptions() {
        System.out.println("\n=== Wrestler Manager Menu ===");

        while (true) {
            System.out.println("\n0) Exit");
            System.out.println("1) Add Wrestler");
            System.out.println("2) View All Wrestlers");
            System.out.println("3) Schedule Wrestler for Event");
            System.out.println("4) View Event Schedule");
            System.out.println("5) Add Wrestler Insurance");
            System.out.println("6) Add Wrestler Contract");
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 6);

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
            } else if (choice == 5) {
                addWrestlerInsurance();
            } else if (choice == 6) {
                addWrestlerContract();
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
        DataCache.addObject(wrestler);
        System.out.println("Wrestler added successfully with ID: " + wrestler.getId());
    }

    private void viewAllWrestlers() {
        System.out.println("\n--- All Wrestlers ---");
        List<Wrestler> wrestlers = DataCache.getAll(Wrestler::new);

        if (wrestlers.isEmpty()) {
            System.out.println("No wrestlers in system.");
        } else {
            for (Wrestler w : wrestlers) {
                System.out
                        .println("ID: " + w.getId() + " | Name: " + w.getName() + " | Specialty: " + w.getSpecialty());
            }
        }
    }

    private void scheduleWrestler() {
        System.out.println("\n--- Schedule Wrestler for Event ---");

        // Show available events
        List<Event> events = DataCache.getAll(Event::new);
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
        Event event = DataCache.getById(eventId, Event::new);
        if (event == null) {
            System.out.println("Error: Event not found.");
            return;
        }

        // Show available wrestlers
        viewAllWrestlers();

        System.out.print("\nEnter Wrestler ID: ");
        int wrestlerId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // Check if wrestler exists
        Wrestler wrestler = DataCache.getById(wrestlerId, Wrestler::new);

        if (wrestler == null) {
            System.out.println("Error: Wrestler not found.");
            return;
        }

        // Check if already scheduled
        List<WrestlerSchedule> schedules = DataCache.getAll(WrestlerSchedule::new);
        for (WrestlerSchedule ws : schedules) {
            if (ws.getEventId() == eventId && ws.getWrestlerId() == wrestlerId) {
                System.out.println("Error: Wrestler already scheduled for this event.");
                return;
            }
        }

        WrestlerSchedule schedule = new WrestlerSchedule(eventId, wrestlerId);
        DataCache.addObject(schedule);
        System.out
                .println("Wrestler " + wrestler.getName() + " scheduled successfully for event on " + event.getDate());
    }

    private void viewEventSchedule() {
        System.out.println("\n--- View Event Schedule ---");
        System.out.print("Enter Event ID: ");
        int eventId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // Check if event exists
        Event event = DataCache.getById(eventId, Event::new);
        if (event == null) {
            System.out.println("Error: Event not found.");
            return;
        }

        System.out.println("\nEvent: " + event.getDate() + " at " + event.getLocationName());
        System.out.println("Scheduled Wrestlers:");

        List<WrestlerSchedule> schedules = DataCache.getAll(WrestlerSchedule::new);
        boolean found = false;

        for (WrestlerSchedule ws : schedules) {
            if (ws.getEventId() == eventId) {
                Wrestler wrestler = DataCache.getById(ws.getWrestlerId(), Wrestler::new);
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

    private void addWrestlerInsurance() {
        System.out.println("\n--- Add Wrestler Insurance ---");

        List<Wrestler> wrestlers = DataCache.getAll(Wrestler::new);
        if (wrestlers.isEmpty()) {
            System.out.println("No wrestlers in system.");
            return;
        }

        System.out.println("\nWrestlers:");
        for (int i = 0; i < wrestlers.size(); i++) {
            Wrestler w = wrestlers.get(i);
            WrestlerInsurance existing = DataCache.getByFilter(
                    in -> in.getWrestlerId() == w.getId(),
                    WrestlerInsurance::new);
            String status = (existing != null && !existing.isExpired()) ? " Insured" : "No Insurance";
            System.out.println((i + 1) + ". " + w.getName() + " - " + status);
        }

        System.out.println("\nSelect wrestler (1-" + wrestlers.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, wrestlers.size());

        if (choice == 0) {
            return;
        }

        Wrestler wrestler = wrestlers.get(choice - 1);

        // Check existing insurance
        WrestlerInsurance existing = DataCache.getByFilter(
                i -> i.getWrestlerId() == wrestler.getId(),
                WrestlerInsurance::new);
        if (existing != null && !existing.isExpired()) {
            System.out.println("Error: Wrestler already has active insurance.");
            System.out.println("    Coverage: $" + existing.getCoverageAmount());
            System.out.println("    Max Danger: " + existing.getMaxDangerRating());
            System.out.println("    Expires on: " + new Date(existing.getExpirationDate()));
            return;
        }

        System.out.println("\n--- Insurance Policy Setup ---");
        System.out.print("Maximum danger rating to cover (1-10): ");
        int maxDanger = UserInput.getIntInput(1, 10);

        // Calculate coverage amount and expiration date
        int coverageAmount = maxDanger * 10000; // Higher danger = more coverage
        long oneYear = 365L * 24 * 60 * 60 * 1000;
        long expirationDate = System.currentTimeMillis() + oneYear;

        // cover all action types by default
        List<ActionType> coveredTypes = new ArrayList<>(Arrays.asList(ActionType.values()));

        // Create and store insurance
        new WrestlerInsurance(
                wrestler.getId(),
                coverageAmount,
                expirationDate,
                maxDanger,
                coveredTypes);

        System.out.println("\nInsurance policy created successfully!");
        System.out.println("  Wrestler: " + wrestler.getName());
        System.out.println("  Coverage Amount: $" + coverageAmount);
        System.out.println("  Max Danger Rating: " + maxDanger + "/10");
        System.out.println("  Valid for: 1 year");
        System.out.println("  Covers: All action types");
    }

    private void addWrestlerContract() {
        System.out.println("\n--- Add Wrestler Contract ---");

        List<Wrestler> wrestlers = DataCache.getAll(Wrestler::new);
        if (wrestlers.isEmpty()) {
            System.out.println("No wrestlers in system.");
            return;
        }

        System.out.println("\nWrestlers:");
        for (int i = 0; i < wrestlers.size(); i++) {
            Wrestler w = wrestlers.get(i);
            Contract existing = DataCache.getByFilter(
                    c -> c.getWrestlerId() == w.getId() && c.isActive() && !c.isExpired(),
                    Contract::new);
            String status = (existing != null) ? "Under Contract ($" + existing.getBasePay() + "/event)"
                    : "No Contract";
            System.out.println((i + 1) + ". " + w.getName() + " - " + status);
        }

        System.out.print("\nSelect wrestler (1-" + wrestlers.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, wrestlers.size());

        if (choice == 0)
            return;

        Wrestler wrestler = wrestlers.get(choice - 1);

        // Check existing contract
        Contract existing = DataCache.getByFilter(
                c -> c.getWrestlerId() == wrestler.getId() && c.isActive() && !c.isExpired(),
                Contract::new);
        if (existing != null) {
            System.out.println("Wrestler already has an active contract.");
            System.out.println("  Base Pay: $" + existing.getBasePay());
            System.out.println("  Expires: " + new java.util.Date(existing.getEndDate()));
            return;
        }

        System.out.print("Enter base pay per event ($): ");
        int basePay = UserInput.getIntInput(1, Integer.MAX_VALUE);

        System.out.print("Contract length in years (1-5): ");
        int years = UserInput.getIntInput(1, 5);

        long startDate = System.currentTimeMillis();
        long endDate = startDate + (years * 365L * 24 * 60 * 60 * 1000);

        Contract contract = new Contract(wrestler.getId(), basePay, startDate, endDate);
        DataCache.addObject(contract);

        System.out.println("\nContract created successfully!");
        System.out.println("  Wrestler: " + wrestler.getName());
        System.out.println("  Base Pay: $" + basePay + " per event");
        System.out.println("  Duration: " + years + " year(s)");
    }
}
