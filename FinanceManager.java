import java.util.*;
import java.util.stream.Collectors;

public class FinanceManager implements DatabaseObject {
    private static final double HIGH_RISK_BONUS_PERCENT = 0.20;
    private static final int HIGH_RISK_THRESHOLD = 8;
    private static final int OVERTIME_HOURS_THRESHOLD = 160;
    private static final double OVERTIME_MULTIPLIER = 1.5;
    private static final double HAZARD_MULTIPLIER = 1.2;
    private static final int DANGER_THRESHOLD_FOR_HAZARD = 7;


    @Override
    public void showOptions(){
        System.out.println("\n=== Finance Manager Menue ===");

        while (true) {
            System.out.println("\n0: Exit");
            System.out.println("1: Pay Wrestlers for Event");
            System.out.println("2: Process Worker Payroll");
            System.out.println("3: View Wrestler Payment History");
            System.out.println("4: View Worker Payment History");
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 4);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                payWrestlersForEvent();
            } else if (choice == 2) {
                processWorkerPayroll();
            } else if (choice == 3) {
                viewWrestlerPaymentHistory();
            } else if (choice == 4) {
                viewWorkerPaymentHistory();
            }
        }
    }


    private void payWrestlersForEvent() {
        System.out.println("\n=== Pay Wrestlers for Event ===");

         List<Event> completedEvents = getCompletedEventsWithUnpaidWrestlers();

        if (completedEvents.isEmpty()) {
            System.out.println("\nNo completed events with unpaid wrestlers.");
            return;
        }

        System.out.println("\nCompleted Events:");
        for (int i = 0; i < completedEvents.size(); i++) {
            Event e = completedEvents.get(i);
            System.out.println((i + 1) + ". Event ID: " + e.getId() + 
                             " | Date: " + e.getDate() + 
                             " | Location: " + e.getLocationName());
        }

        System.out.print("\nSelect event (1-" + completedEvents.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, completedEvents.size());

        if (choice == 0) return;

        Event selectedEvent = completedEvents.get(choice - 1);
        processEventPayments(selectedEvent);
    }

    private void processEventPayments(Event event){
        System.out.println("\n--- Processing Payments for Event: " + event.getDate() + " ---");

        List<WrestlerSchedule> schedules = DataCache.getAllWrestlerSchedules().stream()
            .filter(ws -> ws.getEventId() == event.getId())
            .collect(Collectors.toList());

        if (schedules.isEmpty()) {
            System.out.println("\nNo wrestlers scheduled for this event.");
            return;
        }

        Script script = DataCache.getAllScripts().stream()
            .filter(s -> s.getEventId().equals(String.valueOf(event.getId())))
            .filter(s -> s.getStatus() == ScriptStatus.APPROVED)
            .findFirst()
            .orElse(null);

        if (script == null) {
            System.out.println("\nError: No approved script found for this event.");
            return;
        }

        List<WrestlerPaymentInfo> paymentInfos = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalCost = 0;

        for (WrestlerSchedule schedule : schedules) {
            int wrestlerId = schedule.getWrestlerId();
            Wrestler wrestler = DataCache.getWrestlerById(wrestlerId);

            if (wrestler == null) {
                errors.add("Wrestler ID " + wrestlerId + "not found");
            }

            if (isWrestlerPaidForEvent(wrestlerId, event.getId())) {
                System.out.println("  ⚠ " + wrestler.getName() + " already paid for this event (skipping)");
                continue;
            }

            //Contract
            Contract contract = DataCache.getContractByWrestlerId(wrestlerId);
            if (contract == null || !contract.isActive() || contract.isExpired()) {
                errors.add(wrestler.getName() + ": No active contract");
                continue;
            }

            //Insurance 
            WrestlerInsurance insurance = DataCache.getInsuranceByWrestlerId(wrestlerId);
            if (insurance == null) {
                errors.add(wrestler.getName() + ": No insurance");
                continue;
            }

            if (insurance.isExpired()) {
                errors.add(wrestler.getName() + ": Insurance expired on " + new Date(insurance.getExpirationDate()));
                continue;
            }

            //Insurance covers everything
            List<ScriptAction> wrestlerActions = getWrestlerActionsInScript(wrestlerId, script);
            boolean insuranceValid = true;
            for (ScriptAction action : wrestlerActions) {
                if (!insurance.coversAction(action)) {
                    errors.add(wrestler.getName() + ": Insurance doesn't cover " + 
                             action.getActionType() + " (danger: " + action.getDangerRating() + ")");
                    insuranceValid = false;
                    break;
                }
            }

            if (!insuranceValid) continue;

            //payment
            int basePay = contract.getBasePay();
            int highRiskCount = 0;
            int bonusAmount = 0;

            for (ScriptAction action : wrestlerActions) {
                if (action.getDangerRating() >= HIGH_RISK_THRESHOLD) {
                    highRiskCount++;
                    bonusAmount += (int)(basePay * HIGH_RISK_BONUS_PERCENT);
                }
            }

            int totalPay = basePay + bonusAmount;
            totalCost += totalPay;

            paymentInfos.add(new WrestlerPaymentInfo(wrestler, basePay, bonusAmount, 
                                                totalPay, highRiskCount, wrestlerActions.size()));

        }


        //display

        System.out.println("\n" + "=".repeat(80));
        System.out.println("PAYMENT BREAKDOWN");
        System.out.println("=".repeat(80));

        for (WrestlerPaymentInfo info : paymentInfos) {
            System.out.println("\n" + info.wrestler.getName());
            System.out.println("  Base Pay: $" + String.format("%d", info.basePay));
            System.out.println("  Bonuses: $" + String.format("%d", info.bonusAmount) + 
                             " (" + info.highRiskCount + " high-risk actions)");
            System.out.println("  Total Actions: " + info.totalActions);
            System.out.println("  TOTAL: $" + String.format("%d", info.totalPay));
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("GRAND TOTAL: $" + String.format("%d", totalCost));
        System.out.println("=".repeat(80));

        if (!errors.isEmpty()) {
            System.out.println("\n⚠ ERRORS - The following wrestlers cannot be paid:");
            for (String error : errors) {
                System.out.println("  - " + error);
            }
        }

        if (paymentInfos.isEmpty()) {
            System.out.println("\nNo wrestlers can be paid at this time.");
            return;
        }

        System.out.print("\nConfirm payment? (yes/no): ");
        String confirm = UserInput.getStringInput();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("\nPayment cancelled.");
            return;
        }

        for (WrestlerPaymentInfo info : paymentInfos) {
            new WrestlerPayment(info.wrestler.getId(), event.getId(), 
                              info.basePay, info.bonusAmount, info.highRiskCount);
        }

        System.out.println("\n✓ Payments processed successfully!");
        System.out.println("  " + paymentInfos.size() + " wrestlers paid");
        System.out.println("  Total disbursed: $" + String.format("%d", totalCost));
        System.out.println("\n  Note: Budget allocation handled separately");
        
    }


    private void processWorkerPayroll() {
        System.out.println("\n=== Process Worker Payroll ===");

        List<Worker> workers = DataCache.getAllWorkers();
        if (workers.isEmpty()) {
            System.out.println("\nNo workers in system.");
            return;
        }

        System.out.println("\nSelect payroll period:");
        System.out.println("1. Current Month");
        System.out.println("2. Custom Date Range");
        System.out.print("\nChoice: ");
        int periodChoice = UserInput.getIntInput(1, 2);

        String period;
        long startTime, endTime;

        if (periodChoice == 1) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTimeInMillis();
            endTime = System.currentTimeMillis();
            period = String.format("%tB %tY", cal, cal);
        } else {
            System.out.print("\nEnter start date (YYYY-MM-DD): ");
            String startStr = UserInput.getStringInput();
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endStr = UserInput.getStringInput();
            
            period = startStr + " to " + endStr;
            startTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
            endTime = System.currentTimeMillis();
        }

        Map<String, List<WorkerPaymentInfo>> paymentsByDept = new HashMap<>();
        int grandTotal = 0;

        for (Worker worker : workers) {
            // already paid for this period?
            if (isWorkerPaidForPeriod(worker.getWorkerId(), period)) {
                System.out.println("  ⚠ " + worker.getName() + " already paid for " + period + " (skipping)");
                continue;
            }

            List<WorkerAssignment> assignments = DataCache.getAllWorkerAssignments().stream()
                .filter(wa -> wa.getWorkerId() == worker.getWorkerId())
                .collect(Collectors.toList());

            int totalHours = 0;
            int hazardousHours = 0;

            for (WorkerAssignment assignment : assignments) {
                totalHours += assignment.getHoursWorked();
                if (assignment.isHazardous()) {
                    hazardousHours += assignment.getHoursWorked();
                }
            }

            if (totalHours == 0) continue;

            int hourlyRate = worker.getHourlyRate(); 
            int regularHours = Math.min(totalHours, OVERTIME_HOURS_THRESHOLD);
            int overtimeHours = Math.max(0, totalHours - OVERTIME_HOURS_THRESHOLD);

            int basePay = regularHours * hourlyRate;
            int overtimePay = (int)(overtimeHours * hourlyRate * OVERTIME_MULTIPLIER);
            int hazardPay = (int)(hazardousHours * hourlyRate * 0.2);
            int totalPay = basePay + overtimePay + hazardPay;
            
            WorkerPaymentInfo info = new WorkerPaymentInfo(worker, basePay, overtimePay, 
                                                          hazardPay, totalPay, totalHours, 
                                                          overtimeHours, hazardousHours);

            paymentsByDept.computeIfAbsent(worker.getDepartment(), k -> new ArrayList<>()).add(info);
            grandTotal += totalPay;
        }
    }
}