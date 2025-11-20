import java.util.*;
import java.util.stream.Collectors;

public class FundsController implements Actor {

    @Override
    public void showOptions() {
        System.out.println("\n=== Funds Controller Menu ===");

        while (true) {
            System.out.println("\n0: Exit");
            System.out.println("1: View Financial Status");
            System.out.println("2: Pay Script Insurance");
            System.out.println("3: Manage Allocations");
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 3);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                viewFinancialStatus();
            } else if (choice == 2) {
                processInsurancePayments();
            } else if (choice == 3) {
                manageAllocations();
            }
        }
    }

    public boolean validateAndCharge(String fundName, int amount, String description) {
        Budget fund = Budget.get(fundName);
        if (fund == null) {
            System.out.println("\nError: " + fundName + " fund not found.");
            return false;
        }

        if (fund.funds() < amount) {
            System.out.println("\nInsufficient funds! Required: $" + amount);
            System.out.println("Available: $" + fund.funds());
            System.out.print("\nProcess anyway? (y/n): ");
            String confirm = UserInput.getStringInput();
            if (!confirm.toLowerCase().startsWith("y")) {
                return false;
            }
        }

        fund.charge(amount);
        System.out.println("\n✓ " + description + ": $" + amount);
        System.out.println("  Remaining: $" + fund.funds());
        return true;
    }

    public boolean hasAvailableFunds(String fundName, int amount) {
        Budget fund = Budget.get(fundName);
        return fund != null && fund.funds() >= amount;
    }

    public void chargeFunds(String fundName, int amount) {
        Budget fund = Budget.get(fundName);
        if (fund != null) {
            fund.charge(amount);
        }
    }

    public int getAvailableFunds(String fundName) {
        Budget fund = Budget.get(fundName);
        return fund != null ? fund.funds() : 0;
    }

    private void viewFinancialStatus() {
        System.out.println("\n=== Financial Status ===");
        List<Budget> funds = DataCache.getAll(Budget::new);

        if (funds.isEmpty()) {
            System.out.println("\nNo funds found.");
            return;
        }

        for (Budget fund : funds) {
            fund.print();
        }

        // Show pending insurance costs
        List<Script> unpaidScripts = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getStatus() == ScriptStatus.APPROVED && !s.isInsurancePaid())
                .collect(Collectors.toList());

        if (!unpaidScripts.isEmpty()) {
            int totalUnpaidInsurance = unpaidScripts.stream()
                    .mapToInt(Script::getTotalInsuranceCost)
                    .sum();
            System.out.println("\nPending Insurance Payments: $" + totalUnpaidInsurance + " (" + unpaidScripts.size()
                    + " scripts)");
        }
    }

    private void processInsurancePayments() {
        System.out.println("\n=== Process Insurance Payments ===");

        List<Script> unpaidScripts = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getStatus() == ScriptStatus.APPROVED && !s.isInsurancePaid())
                .collect(Collectors.toList());

        if (unpaidScripts.isEmpty()) {
            System.out.println("\nNo approved scripts with unpaid insurance.");
            return;
        }

        System.out.println("\nApproved Scripts Requiring Insurance Payment:");
        for (int i = 0; i < unpaidScripts.size(); i++) {
            Script s = unpaidScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() +
                    " | Event ID: " + s.getEventId() +
                    " | Insurance Cost: $" + s.getTotalInsuranceCost());
        }

        System.out.print("\nSelect script (1-" + unpaidScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, unpaidScripts.size());

        if (choice == 0)
            return;

        Script script = unpaidScripts.get(choice - 1);

        if (validateAndCharge("Event", script.getTotalInsuranceCost(), "Insurance payment processed")) {
            new ScriptInsurancePayment(script.getScriptId(), script.getEventId(),
                    script.getTotalInsuranceCost(), "Funds Controller", "Event");
            System.out.println("  Script " + script.getScriptId() + " insurance marked as paid");
        }
    }

    private void manageAllocations() {
        System.out.println("\n=== Manage Fund Allocations ===");
        List<Budget> funds = DataCache.getAll(Budget::new);

        if (funds.isEmpty()) {
            System.out.println("\nNo funds found.");
            return;
        }

        System.out.println("\nCurrent Funds:");
        for (int i = 0; i < funds.size(); i++) {
            Budget fund = funds.get(i);
            System.out.println((i + 1) + ". " + fund.getName() + ": $" + fund.funds());
        }

        System.out.println("\n1: Add funds to existing");
        System.out.println("2: Create new fund");
        System.out.print("\nChoice: ");
        int choice = UserInput.getIntInput(1, 2);

        if (choice == 1) {
            addFundsToExisting(funds);
        } else {
            createNewFund();
        }
    }

    private void addFundsToExisting(List<Budget> funds) {
        System.out.print("\nSelect fund (1-" + funds.size() + "): ");
        int choice = UserInput.getIntInput(1, funds.size());

        Budget selectedFund = funds.get(choice - 1);
        System.out.print("Enter amount to add: $");
        int amount = UserInput.getIntInput();

        selectedFund.fund(amount);
        System.out.println("\n✓ Added $" + amount + " to " + selectedFund.getName());
        System.out.println("  New total: $" + selectedFund.funds());
    }

    private void createNewFund() {
        System.out.print("\nEnter fund name: ");
        String name = UserInput.getStringInput();
        System.out.print("Enter starting amount: $");
        int amount = UserInput.getIntInput();

        new Budget(name, amount);
        System.out.println("\n✓ Created " + name + " fund with $" + amount);
    }
}
