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
            System.out.println("\n0: Exit");
            System.out.println("1: Pay Wrestlers for Event");
            System.out.println("2: Process Worker Payroll");
            System.out.println("3: View Wrestler Payment History");
            System.out.println("4: View Worker Payment History");
            System.out.println("5: View Payment Budgets");
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 5);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                payWrestlersForEvent();
            } else if (choice == 2) {
                processWorkerPayroll();
            } else if (choice == 3) {
                historyService.viewWrestlerPaymentHistory();
            } else if (choice == 4) {
                historyService.viewWorkerPaymentHistory();
            } else if (choice == 5) {
                viewPaymentBudgets();
            }
        }
    }

    private void payWrestlersForEvent() {
        Budget wrestlerBudget = Budget.get("Wrestler");
        if (wrestlerBudget == null) {
            System.out.println("(Note: No 'Wrestler' budget set up)");
        }
        wrestlerPaymentService.payWrestlersForEvent();
    }

    private void processWorkerPayroll() {
        Budget workerBudget = Budget.get("Worker");
        if (workerBudget == null) {
            System.out.println("(Note: No 'Worker' budget set up)");
        }
        workerPayrollService.processWorkerPayroll();
    }

    private void viewPaymentBudgets() {
        System.out.println("\n=== Payment Budgets ===");
        Budget wrestlerBudget = Budget.get("Wrestler");
        Budget workerBudget = Budget.get("Worker");
        if (wrestlerBudget != null) {
            wrestlerBudget.print();
        } else {
            System.out.println("Wrestler Budget: Not configured");
        }
        if (workerBudget != null) {
            workerBudget.print();
        } else {
            System.out.println("Worker Budget: Not configured");
        }
    }
}
