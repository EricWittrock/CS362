import java.util.*;

public class WorkerManager implements Actor {

    public WorkerManager() {
        BenefitsPlansService.createDefaultPlans();
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
            System.out.println("6) Enroll Worker in Benefits Plan");
            System.out.println("7) View Worker Benefits Summary");
            System.out.println("8) Manage Benefits Plans");
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 8);

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
            } else if (choice == 6) {
                enrollWorkerInPlan();
            } else if (choice == 7) {
                viewWorkerBenefitsSummary();
            } else if (choice == 8) {
                manageBenefitsPlans();
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

    private void enrollWorkerInPlan() {
        System.out.println("\n--- Enroll Worker in Benefits Plan ---");

        List<Worker> workers = DataCache.getAll(Worker::new);
        if (workers.isEmpty()) {
            System.out.println("No workers in system.");
            return;
        }

        System.out.println("\nWorkers:");
        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            BenefitsPlan currentPlan = BenefitsPlansService.getWorkerPlan(w.getWorkerId());
            String status = currentPlan != null ? "Enrolled in " + currentPlan.getPlanName() : "No Plan";
            System.out.println((i + 1) + ". " + w.getName() + " - " + status);
        }

        System.out.print("\nSelect worker (1-" + workers.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, workers.size());
        if (choice == 0)
            return;

        Worker worker = workers.get(choice - 1);

        // Check if already enrolled
        BenefitsPlan currentPlan = BenefitsPlansService.getWorkerPlan(worker.getWorkerId());
        if (currentPlan != null) {
            System.out.println("\nWorker is currently enrolled in: " + currentPlan.getPlanName());
            System.out.print("Unenroll and choose new plan? (yes/no): ");
            String response = UserInput.getStringInput();
            if (!response.equalsIgnoreCase("yes")) {
                return;
            }
            BenefitsPlansService.unenrollWorker(worker.getWorkerId());
        }

        // Show available plans
        List<BenefitsPlan> plans = BenefitsPlansService.getActivePlans();
        if (plans.isEmpty()) {
            System.out.println("\nNo benefits plans available. Creating defaults...");
            BenefitsPlansService.createDefaultPlans();
            plans = BenefitsPlansService.getActivePlans();
        }

        System.out.println("\nAvailable Benefits Plans:");
        for (int i = 0; i < plans.size(); i++) {
            BenefitsPlan plan = plans.get(i);
            System.out.println("\n" + (i + 1) + ". " + plan.getPlanName());
            System.out.println("   Employee Cost: $" + plan.getMonthlyEmployeeCost() + "/month");
            System.out.println("   Employer Pays: $" + plan.getMonthlyEmployerCost() + "/month");
            System.out.println("   Benefits: " + plan.getIncludedBenefits().size() + " items");
        }

        System.out.print("\nSelect plan (1-" + plans.size() + ", 0 to cancel): ");
        int planChoice = UserInput.getIntInput(0, plans.size());
        if (planChoice == 0)
            return;

        BenefitsPlan selectedPlan = plans.get(planChoice - 1);
        selectedPlan.print();

        System.out.print("\nConfirm enrollment? (yes/no): ");
        String confirm = UserInput.getStringInput();
        if (!confirm.equalsIgnoreCase("yes")) {
            return;
        }

        selectedPlan.enrollWorker(worker.getWorkerId());

        System.out.println("\n✓ Worker enrolled in " + selectedPlan.getPlanName());
        System.out.println("  Employee: " + worker.getName());
        System.out.println("  Monthly Cost: $" + selectedPlan.getMonthlyEmployeeCost());
        System.out.println("  Benefits Active: " + selectedPlan.getIncludedBenefits().size());
    }

    private void manageBenefitsPlans() {
        while (true) {
            System.out.println("\n--- Manage Benefits Plans ---");
            System.out.println("0) Back");
            System.out.println("1) View All Plans");
            System.out.println("2) Create New Plan");
            System.out.println("3) Deactivate Plan");
            System.out.print("\nChoice: ");
            int choice = UserInput.getIntInput(0, 3);

            if (choice == 0)
                break;
            else if (choice == 1)
                viewAllPlans();
            else if (choice == 2)
                createNewPlan();
            else if (choice == 3)
                deactivatePlan();
        }
    }

    private void viewAllPlans() {
        List<BenefitsPlan> plans = DataCache.getAll(BenefitsPlan::new);

        if (plans.isEmpty()) {
            System.out.println("\nNo plans found. Creating defaults...");
            BenefitsPlansService.createDefaultPlans();
            plans = DataCache.getAll(BenefitsPlan::new);
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("BENEFITS PLANS");
        System.out.println("=".repeat(80));

        for (BenefitsPlan plan : plans) {
            plan.print();
            System.out.println();
        }
    }

    private void createNewPlan() {
        System.out.println("\n--- Create New Benefits Plan ---");

        System.out.print("Plan name: ");
        String name = UserInput.getStringInput();

        System.out.print("Description: ");
        String description = UserInput.getStringInput();

        System.out.print("Monthly employee cost ($): ");
        int employeeCost = UserInput.getIntInput(0, 10000);

        System.out.print("Monthly employer contribution ($): ");
        int employerCost = UserInput.getIntInput(0, 10000);

        BenefitsPlan plan = new BenefitsPlan(name, description, employeeCost, employerCost);

        // Add benefits
        System.out.println("\nAdd benefits to this plan:");
        while (true) {
            System.out.print("Add another benefit? (yes/no): ");
            String response = UserInput.getStringInput();
            if (!response.equalsIgnoreCase("yes"))
                break;

            BenefitsDeduction.DeductionType[] types = BenefitsDeduction.DeductionType.values();
            for (int i = 0; i < types.length; i++) {
                if (types[i].isDefaultPreTax()) {
                    System.out.println((i + 1) + ". " + types[i].getDisplayName());
                }
            }

            System.out.print("Select benefit type (1-6): ");
            int choice = UserInput.getIntInput(1, 6);
            BenefitsDeduction.DeductionType type = types[choice - 1];

            System.out.print("Monthly amount for " + type.getDisplayName() + " ($): ");
            int amount = UserInput.getIntInput(0, 1000);

            plan.addBenefit(type, amount);
        }

        System.out.println("\n✓ Benefits plan created: " + name);
    }

    private void deactivatePlan() {
        List<BenefitsPlan> plans = BenefitsPlansService.getActivePlans();

        if (plans.isEmpty()) {
            System.out.println("No active plans.");
            return;
        }

        System.out.println("\nActive Plans:");
        for (int i = 0; i < plans.size(); i++) {
            System.out.println((i + 1) + ". " + plans.get(i).getPlanName());
        }

        System.out.print("\nSelect plan to deactivate (1-" + plans.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, plans.size());
        if (choice == 0)
            return;

        BenefitsPlan plan = plans.get(choice - 1);
        plan.setActive(false);

        System.out.println("\n✓ Plan deactivated: " + plan.getPlanName());
    }

    private void manageWorkerBenefits() {
        System.out.println("\n--- Manage Worker Benefits ---");

        List<Worker> workers = DataCache.getAll(Worker::new);
        if (workers.isEmpty()) {
            System.out.println("No workers in system.");
            return;
        }

        System.out.println("\nWorkers:");
        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            int deductionCount = BenefitsService.getActiveDeductions(w.getWorkerId()).size();
            System.out.println((i + 1) + ". " + w.getName() + " - " +
                    deductionCount + " active benefit(s)");
        }

        System.out.print("\nSelect worker (1-" + workers.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, workers.size());
        if (choice == 0)
            return;

        Worker worker = workers.get(choice - 1);
        manageBenefitsForWorker(worker);
    }

    private void manageBenefitsForWorker(Worker worker) {
        while (true) {
            System.out.println("\n--- Benefits for " + worker.getName() + " ---");

            List<BenefitsDeduction> deductions = BenefitsService.getActiveDeductions(worker.getWorkerId());
            if (deductions.isEmpty()) {
                System.out.println("No active benefits.");
            } else {
                for (int i = 0; i < deductions.size(); i++) {
                    BenefitsDeduction d = deductions.get(i);
                    System.out.println((i + 1) + ". " + d.getType().getDisplayName() +
                            " - $" + d.getAmountPerPayPeriod() +
                            " (" + (d.isPreTax() ? "Pre-Tax" : "Post-Tax") + ")");
                }
            }

            System.out.println("\n0) Back");
            System.out.println("1) Add Benefit");
            System.out.println("2) Remove Benefit");
            System.out.print("\nChoice: ");
            int choice = UserInput.getIntInput(0, 2);

            if (choice == 0)
                break;
            else if (choice == 1)
                addBenefit(worker);
            else if (choice == 2)
                removeBenefit(worker, deductions);
        }
    }

    private void addBenefit(Worker worker) {
        System.out.println("\n--- Add Benefit ---");
        System.out.println("Available Benefits:");

        BenefitsDeduction.DeductionType[] types = BenefitsDeduction.DeductionType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName() +
                    " (" + (types[i].isDefaultPreTax() ? "Pre-Tax" : "Post-Tax") + ")");
        }

        System.out.print("\nSelect benefit type (1-" + types.length + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, types.length);
        if (choice == 0)
            return;

        BenefitsDeduction.DeductionType type = types[choice - 1];

        System.out.print("Enter amount per pay period ($): ");
        int amount = UserInput.getIntInput(1, 10000);

        new BenefitsDeduction(worker.getWorkerId(), type, amount);

        System.out.println("\n✓ Benefit added successfully!");
        System.out.println("  Type: " + type.getDisplayName());
        System.out.println("  Amount: $" + amount + " per pay period");
        System.out.println(
                "  Tax Treatment: " + (type.isDefaultPreTax() ? "Pre-Tax (reduces taxable income)" : "Post-Tax"));
    }

    private void removeBenefit(Worker worker, List<BenefitsDeduction> deductions) {
        if (deductions.isEmpty()) {
            System.out.println("No benefits to remove.");
            return;
        }

        System.out.print("\nSelect benefit to remove (1-" + deductions.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, deductions.size());
        if (choice == 0)
            return;

        BenefitsDeduction deduction = deductions.get(choice - 1);
        deduction.setActive(false);

        System.out.println("\n✓ Benefit removed: " + deduction.getType().getDisplayName());
    }

    private void viewWorkerBenefitsSummary() {
        System.out.println("\n--- Worker Benefits Summary ---");

        List<Worker> workers = DataCache.getAll(Worker::new);
        if (workers.isEmpty()) {
            System.out.println("No workers in system.");
            return;
        }

        System.out.println("\n" + "=".repeat(90));
        System.out.println(String.format("%-20s %-15s %15s %15s %15s",
                "Worker", "Department", "Pre-Tax", "Post-Tax", "Total"));
        System.out.println("=".repeat(90));

        for (Worker worker : workers) {
            int preTax = BenefitsService.calculateTotalPreTaxDeductions(worker.getWorkerId());
            int postTax = BenefitsService.calculateTotalPostTaxDeductions(worker.getWorkerId());
            int total = preTax + postTax;

            if (total > 0) {
                System.out.println(String.format("%-20s %-15s $%14d $%14d $%14d",
                        worker.getName(),
                        worker.getDepartment(),
                        preTax,
                        postTax,
                        total));
            }
        }

        System.out.println("=".repeat(90));
    }
}
