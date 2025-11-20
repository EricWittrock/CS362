import java.util.*;
import java.util.stream.Collectors;

public class WrestlerPaymentService {
    
    public void payWrestlersForEvent() {
        System.out.println("\n=== Pay Wrestlers for Event ===");

        List<Event> completedEvents = getCompletedEventsWithUnpaidWrestlers();

        if (completedEvents.isEmpty()) {
            System.out.println("\nNo completed events with unpaid wrestlers.");
            return;
        }

        displayEventList(completedEvents);

        System.out.print("\nSelect event (1-" + completedEvents.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, completedEvents.size());

        if (choice == 0) return;

        Event selectedEvent = completedEvents.get(choice - 1);
        processEventPayments(selectedEvent);
    }

    private void displayEventList(List<Event> events) {
        System.out.println("\nCompleted Events:");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println((i + 1) + ". Event ID: " + e.getId() +
                    " | Date: " + e.getDate() +
                    " | Location: " + e.getLocationName());
        }
    }

    private void processEventPayments(Event event) {
        System.out.println("\n--- Processing Payments for Event: " + event.getDate() + " ---");

        List<WrestlerSchedule> schedules = getSchedulesForEvent(event.getId());
        if (schedules.isEmpty()) {
            System.out.println("\nNo wrestlers scheduled for this event.");
            return;
        }

        Script script = getApprovedScript(event.getId());
        if (script == null) {
            System.out.println("\nError: No approved script found for this event.");
            return;
        }

        WrestlerPaymentBatch batch = calculatePayments(schedules, script, event.getId());

        displayPaymentBreakdown(batch);

        if (batch.errors.size() > 0) {
            displayErrors(batch.errors);
        }

        if (batch.payments.isEmpty()) {
            System.out.println("\nNo wrestlers can be paid at this time.");
            return;
        }

        if (confirmPayment()) {
            savePayments(batch.payments, event.getId());
            displaySuccess(batch);
        }
    }

    private WrestlerPaymentBatch calculatePayments(List<WrestlerSchedule> schedules, 
                                                   Script script, int eventId) {
        WrestlerPaymentBatch batch = new WrestlerPaymentBatch();

        for (WrestlerSchedule schedule : schedules) {
            int wrestlerId = schedule.getWrestlerId();
            Wrestler wrestler = DataCache.getById(wrestlerId, Wrestler::new);

            if (wrestler == null) {
                batch.errors.add("Wrestler ID " + wrestlerId + " not found");
                continue;
            }

            if (isWrestlerPaidForEvent(wrestlerId, eventId)) {
                System.out.println("  ⚠ " + wrestler.getName() + " already paid (skipping)");
                continue;
            }

            String validationError = validateWrestler(wrestler, script);
            if (validationError != null) {
                batch.errors.add(validationError);
                continue;
            }

            WrestlerPaymentInfo payment = calculateWrestlerPayment(wrestler, script);
            batch.payments.add(payment);
            batch.totalCost += payment.totalPay;
        }

        return batch;
    }

    private String validateWrestler(Wrestler wrestler, Script script) {
        // Validate contract
        Contract contract = DataCache.getByFilter(
            c -> c.getWrestlerId() == wrestler.getId(), 
            Contract::new
        );
        if (contract == null || !contract.isActive() || contract.isExpired()) {
            return wrestler.getName() + ": No active contract";
        }

        // Validate insurance
        WrestlerInsurance insurance = DataCache.getByFilter(
            i -> i.getWrestlerId() == wrestler.getId(),
            WrestlerInsurance::new
        );
        if (insurance == null) {
            return wrestler.getName() + ": No insurance";
        }

        if (insurance.isExpired()) {
            return wrestler.getName() + ": Insurance expired on " + 
                   new Date(insurance.getExpirationDate());
        }

        // Check insurance coverage
        List<ScriptAction> wrestlerActions = getWrestlerActionsInScript(wrestler.getId(), script);
        for (ScriptAction action : wrestlerActions) {
            if (!insurance.coversAction(action)) {
                return wrestler.getName() + ": Insurance doesn't cover " + 
                       action.getActionType() + " (danger: " + action.getDangerRating() + ")";
            }
        }

        return null; // Valid
    }

    private WrestlerPaymentInfo calculateWrestlerPayment(Wrestler wrestler, Script script) {
        Contract contract = DataCache.getByFilter(
            i -> i.getWrestlerId() == wrestler.getId(),
            Contract::new
        );
        List<ScriptAction> actions = getWrestlerActionsInScript(wrestler.getId(), script);

        int basePay = contract.getBasePay();
        int bonusAmount = PaymentCalculator.calculateWrestlerBonus(basePay, actions);
        int highRiskCount = PaymentCalculator.countHighRiskActions(actions);
        int totalPay = basePay + bonusAmount;

        return new WrestlerPaymentInfo(wrestler, basePay, bonusAmount, 
                                      totalPay, highRiskCount, actions.size());
    }

    private void displayPaymentBreakdown(WrestlerPaymentBatch batch) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PAYMENT BREAKDOWN");
        System.out.println("=".repeat(80));

        for (WrestlerPaymentInfo info : batch.payments) {
            System.out.println("\n" + info.wrestler.getName());
            System.out.println("  Base Pay: $" + info.basePay);
            System.out.println("  Bonuses: $" + info.bonusAmount +
                    " (" + info.highRiskCount + " high-risk actions)");
            System.out.println("  Total Actions: " + info.totalActions);
            System.out.println("  TOTAL: $" + info.totalPay);
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("GRAND TOTAL: $" + batch.totalCost);
        System.out.println("=".repeat(80));
    }

    private void displayErrors(List<String> errors) {
        System.out.println("\n⚠ ERRORS - The following wrestlers cannot be paid:");
        for (String error : errors) {
            System.out.println("  - " + error);
        }
    }

    private boolean confirmPayment() {
        System.out.print("\nConfirm payment? (yes/no): ");
        String confirm = UserInput.getStringInput();
        return confirm.equalsIgnoreCase("yes");
    }

    private void savePayments(List<WrestlerPaymentInfo> payments, int eventId) {
        for (WrestlerPaymentInfo info : payments) {
            new WrestlerPayment(info.wrestler.getId(), eventId,
                    info.basePay, info.bonusAmount, info.highRiskCount);
        }
    }

    private void displaySuccess(WrestlerPaymentBatch batch) {
        System.out.println("\n✓ Payments processed successfully!");
        System.out.println("  " + batch.payments.size() + " wrestlers paid");
        System.out.println("  Total disbursed: $" + batch.totalCost);
        System.out.println("\n  Note: Budget allocation handled separately");
    }

    // Helper methods
    private List<Event> getCompletedEventsWithUnpaidWrestlers() {
        return DataCache.getAll(Event::new);
    }

    private List<WrestlerSchedule> getSchedulesForEvent(int eventId) {
        return DataCache.getAll(WrestlerSchedule::new).stream()
                .filter(ws -> ws.getEventId() == eventId)
                .collect(Collectors.toList());
    }

    private Script getApprovedScript(int eventId) {
        return DataCache.getAll(Script::new).stream()
                .filter(s -> s.getEventId() == eventId)
                .filter(s -> s.getStatus() == ScriptStatus.APPROVED)
                .findFirst()
                .orElse(null);
    }

    private boolean isWrestlerPaidForEvent(int wrestlerId, int eventId) {
        return DataCache.getAll(WrestlerPayment::new).stream()
                .anyMatch(p -> p.getWrestlerId() == wrestlerId && p.getEventId() == eventId);
    }

    private List<ScriptAction> getWrestlerActionsInScript(int wrestlerId, Script script) {
        List<ScriptAction> result = new ArrayList<>();
        for (Integer actionId : script.getActionIds()) {
            ScriptAction action = DataCache.getById(actionId, ScriptAction::new);
            if (action != null && action.getWrestlerIds().contains(wrestlerId)) {
                result.add(action);
            }
        }
        return result;
    }

    // Inner classes
    private static class WrestlerPaymentBatch {
        List<WrestlerPaymentInfo> payments = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalCost = 0;
    }

    static class WrestlerPaymentInfo {
        Wrestler wrestler;
        int basePay;
        int bonusAmount;
        int totalPay;
        int highRiskCount;
        int totalActions;

        WrestlerPaymentInfo(Wrestler wrestler, int basePay, int bonusAmount,
                int totalPay, int highRiskCount, int totalActions) {
            this.wrestler = wrestler;
            this.basePay = basePay;
            this.bonusAmount = bonusAmount;
            this.totalPay = totalPay;
            this.highRiskCount = highRiskCount;
            this.totalActions = totalActions;
        }
    }
}