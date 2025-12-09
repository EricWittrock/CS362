import java.util.*;

public class FinanceManager implements Actor {
    private WrestlerPaymentService wrestlerPaymentService;
    private WorkerPayrollService workerPayrollService;
    private PaymentHistoryService historyService;
    private FundsController fundsController;

    public FinanceManager() {
        this.wrestlerPaymentService = new WrestlerPaymentService();
        this.workerPayrollService = new WorkerPayrollService();
        this.historyService = new PaymentHistoryService();
        this.fundsController = new FundsController();
    }

    @Override
    public void showOptions() {
        System.out.println("\n=== Finance Manager Menu ===");

        while (true) {
            displayMenu();
            int choice = getUserChoice();

            if (choice == 0)
                break;

            handleMenuChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println("\n0: Exit");
        System.out.println("1: Pay Wrestlers for Event");
        System.out.println("2: Process Worker Payroll");
        System.out.println("3: View Wrestler Payment History");
        System.out.println("4: View Worker Payment History");
        System.out.println("5: View Payment Budgets");
        System.out.println("6: View Employee Tax Records");
        System.out.println("7: View Tax Breakdown by Payment");
        System.out.println("8: Generate W2 Summary");
        System.out.println("9: Generate 1099 Summary");
        System.out.println("10: View Tax Liability Summary");
        System.out.print("\nEnter choice: ");
    }

    private int getUserChoice() {
        return UserInput.getIntInput(0, 10);
    }

    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 1 -> payWrestlersForEvent();
            case 2 -> processWorkerPayroll();
            case 3 -> historyService.viewWrestlerPaymentHistory();
            case 4 -> historyService.viewWorkerPaymentHistory();
            case 5 -> viewPaymentBudgets();
            case 6 -> viewEmployeeTaxRecords();
            case 7 -> viewTaxBreakdownByPayment();
            case 8 -> generateW2Summary();
            case 9 -> generate1099Summary();
            case 10 -> viewTaxLiabilitySummary();
        }
    }

    private void viewEmployeeTaxRecords() {
        TaxReportGenerator.displayEmployeeTaxRecords();
    }

    private void viewTaxBreakdownByPayment() {
        TaxReportGenerator.displayPaymentTaxBreakdowns();
    }

    private void generateW2Summary() {
        TaxReportGenerator.displayW2Summary();
    }

    private void generate1099Summary() {
        TaxReportGenerator.display1099Summary();
    }

    private void viewTaxLiabilitySummary() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        TaxLiabilitySummary summary = TaxService.calculateTotalLiability(currentYear);
        summary.print();
    }

    private void payWrestlersForEvent() {
        ensureBudgetExists("Wrestler");
        wrestlerPaymentService.payWrestlersForEvent();
    }

    private void processWorkerPayroll() {
        ensureBudgetExists("Worker");
        workerPayrollService.processWorkerPayroll();
    }

    private void viewPaymentBudgets() {
        Budget wrestlerBudget = Budget.get("Wrestler");
        Budget workerBudget = Budget.get("Worker");

        printBudget(wrestlerBudget, "Wrestler");
        printBudget(workerBudget, "Worker");
    }

    private void ensureBudgetExists(String budgetName) {
        Budget budget = Budget.get(budgetName);
        if (budget == null) {
            System.out.println("(Note: No '" + budgetName + "' budget configured)");
        }
    }

    private void printBudget(Budget budget, String name) {
        if (budget != null) {
            budget.print();
        } else {
            System.out.println(name + " Budget: Not configured");
        }
    }
}
