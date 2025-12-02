import java.util.*;

public class WorkerManager implements Actor {

    public WorkerManager() {
    }

    @Override
    public void showOptions() {
        System.out.println("\n=== Worker Manager Menu ===");

        while (true) {
            System.out.println("\n0) Exit");
            System.out.println("1) Add Worker");
            System.out.println("2) View All Workers");
            System.out.println("3) Assign Worker to Event");
            System.out.println("4) View Worker Assignments");
            System.out.println("5) Add Worker Insurance");
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 5);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                addWorker();
            } else if (choice == 2) {
                viewAllWorkers();
            } else if (choice == 3) {
                assignWorkerToEvent();
            } else if (choice == 4) {
                viewWorkerAssignments();
            } else if (choice == 5) {
                addWorkerInsurance();
            }
        }
    }

    private void addWorker() {
        System.out.println("\n--- Add Worker ---");
        System.out.print("Enter worker name: ");
        String name = UserInput.getStringInput();

        if (name == null || name.isEmpty()) {
            System.out.println("Error: Worker name cannot be empty.");
            return;
        }

        System.out.println("\nDepartments:");
        System.out.println("1) Stage Crew");
        System.out.println("2) Security");
        System.out.println("3) Catering");
        System.out.println("4) Technical");
        System.out.println("5) Medical");
        System.out.print("Select department (1-5): ");
        int deptChoice = UserInput.getIntInput(1, 5);

        String department;
        switch (deptChoice) {
            case 1:
                department = "Stage Crew";
                break;
            case 2:
                department = "Security";
                break;
            case 3:
                department = "Catering";
                break;
            case 4:
                department = "Technical";
                break;
            case 5:
                department = "Medical";
                break;
            default:
                department = "General";
                break;
        }

        System.out.print("Enter hourly rate ($): ");
        int hourlyRate = UserInput.getIntInput(1, 1000);

        Worker worker = new Worker(name, department, hourlyRate);
        System.out.println("\nWorker added successfully!");
        System.out.println("  ID: " + worker.getWorkerId());
        System.out.println("  Name: " + name);
        System.out.println("  Department: " + department);
        System.out.println("  Hourly Rate: $" + hourlyRate);
    }

    private void viewAllWorkers() {
        System.out.println("\n--- All Workers ---");
        List<Worker> workers = DataCache.getAll(Worker::new);

        if (workers.isEmpty()) {
            System.out.println("No workers in system.");
        } else {
            for (Worker w : workers) {
                WorkerInsurance ins = DataCache.getByFilter(
                        i -> i.getWorkerId() == w.getWorkerId() && !i.isExpired(),
                        WorkerInsurance::new);
                String insStatus = (ins != null) ? "Insured" : "No Insurance";

                System.out.println("ID: " + w.getWorkerId() +
                        " | " + w.getName() +
                        " | " + w.getDepartment() +
                        " | $" + w.getHourlyRate() + "/hr" +
                        " | " + insStatus);
            }
        }
    }

    private void assignWorkerToEvent() {
        System.out.println("\n--- Assign Worker to Event ---");

        List<Event> events = DataCache.getAll(Event::new);
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        System.out.println("\nAvailable Events:");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println((i + 1) + ". " + e.getDate() + " at " + e.getLocationName());
        }

        System.out.print("\nSelect event (1-" + events.size() + ", 0 to cancel): ");
        int eventChoice = UserInput.getIntInput(0, events.size());
        if (eventChoice == 0)
            return;

        Event event = events.get(eventChoice - 1);

        List<Worker> workers = DataCache.getAll(Worker::new);
        if (workers.isEmpty()) {
            System.out.println("No workers in system. Add workers first.");
            return;
        }

        System.out.println("\nAvailable Workers:");
        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            System.out.println((i + 1) + ". " + w.getName() + " (" + w.getDepartment() + ")");
        }

        System.out.print("\nSelect worker (1-" + workers.size() + ", 0 to cancel): ");
        int workerChoice = UserInput.getIntInput(0, workers.size());
        if (workerChoice == 0)
            return;

        Worker worker = workers.get(workerChoice - 1);

        // Check if already assigned
        WorkerAssignment existing = DataCache.getByFilter(
                a -> a.getWorkerId() == worker.getWorkerId() && a.getEventId() == event.getId(),
                WorkerAssignment::new);
        if (existing != null) {
            System.out.println("Worker already assigned to this event.");
            return;
        }

        System.out.print("Hours to work: ");
        int hours = UserInput.getIntInput(1, 24);

        System.out.print("Hazardous work? (yes/no): ");
        String hazInput = UserInput.getStringInput();
        boolean isHazardous = hazInput.equalsIgnoreCase("yes");

        new WorkerAssignment(worker.getWorkerId(), event.getId(), hours, isHazardous);

        System.out.println("\nWorker assigned successfully!");
        System.out.println("  Worker: " + worker.getName());
        System.out.println("  Event: " + event.getDate() + " at " + event.getLocationName());
        System.out.println("  Hours: " + hours);
        System.out.println("  Hazardous: " + (isHazardous ? "Yes" : "No"));
    }

    private void viewWorkerAssignments() {
        System.out.println("\n--- Worker Assignments ---");

        List<Event> events = DataCache.getAll(Event::new);
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        System.out.println("\nSelect an event to view assignments:");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println((i + 1) + ". " + e.getDate() + " at " + e.getLocationName());
        }

        System.out.print("\nSelect event (1-" + events.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, events.size());
        if (choice == 0)
            return;

        Event event = events.get(choice - 1);

        List<WorkerAssignment> assignments = DataCache.getAllByFilter(
                a -> a.getEventId() == event.getId(),
                WorkerAssignment::new);

        if (assignments.isEmpty()) {
            System.out.println("\nNo workers assigned to this event.");
            return;
        }

        System.out.println("\nWorkers assigned to " + event.getDate() + ":");
        for (WorkerAssignment a : assignments) {
            Worker w = DataCache.getById(a.getWorkerId(), Worker::new);
            if (w != null) {
                System.out.println("  - " + w.getName() +
                        " (" + w.getDepartment() + ")" +
                        " | " + a.getHoursWorked() + " hrs" +
                        " | Hazardous: " + (a.isHazardous() ? "Yes" : "No"));
            }
        }
    }

    private void addWorkerInsurance() {
        System.out.println("\n--- Add Worker Insurance ---");

        List<Worker> workers = DataCache.getAll(Worker::new);
        if (workers.isEmpty()) {
            System.out.println("No workers in system.");
            return;
        }

        System.out.println("\nWorkers:");
        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            WorkerInsurance existing = DataCache.getByFilter(
                    ins -> ins.getWorkerId() == w.getWorkerId(),
                    WorkerInsurance::new);
            String status = (existing != null && !existing.isExpired()) ? "Insured" : "No Insurance";
            System.out.println((i + 1) + ". " + w.getName() + " (" + w.getDepartment() + ") - " + status);
        }

        System.out.print("\nSelect worker (1-" + workers.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, workers.size());
        if (choice == 0)
            return;

        Worker worker = workers.get(choice - 1);

        // Check existing insurance
        WorkerInsurance existing = DataCache.getByFilter(
                ins -> ins.getWorkerId() == worker.getWorkerId(),
                WorkerInsurance::new);
        if (existing != null && !existing.isExpired()) {
            System.out.println("Worker already has active insurance.");
            System.out.println("  Coverage: $" + existing.getCoverageAmount());
            System.out.println("  Hazardous Coverage: " + (existing.coversHazardous() ? "Yes" : "No"));
            System.out.println("  Expires: " + new Date(existing.getExpirationDate()));
            return;
        }

        System.out.print("Include hazardous work coverage? (yes/no): ");
        String hazInput = UserInput.getStringInput();
        boolean coversHazardous = hazInput.equalsIgnoreCase("yes");

        // Calculate coverage
        int coverageAmount = coversHazardous ? 50000 : 25000;
        long oneYear = 365L * 24 * 60 * 60 * 1000;
        long expirationDate = System.currentTimeMillis() + oneYear;

        new WorkerInsurance(worker.getWorkerId(), coverageAmount, expirationDate, coversHazardous);

        System.out.println("\nInsurance policy created successfully!");
        System.out.println("  Worker: " + worker.getName());
        System.out.println("  Coverage Amount: $" + coverageAmount);
        System.out.println("  Hazardous Coverage: " + (coversHazardous ? "Yes" : "No"));
        System.out.println("  Valid for: 1 year");
    }
}
