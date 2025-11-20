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
            System.out.println("5: Manage Funds");
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
                fundsController.showOptions();
            }
        }
    }

    private void payWrestlersForEvent() {
        wrestlerPaymentService.payWrestlersForEvent();
    }

    private void processWorkerPayroll() {
        workerPayrollService.processWorkerPayroll();
    }

    // Helper classes for payment services
    public static class WrestlerPaymentResult {
        public int totalPaid;
        public int wrestlerCount;

        public WrestlerPaymentResult(int totalPaid, int wrestlerCount) {
            this.totalPaid = totalPaid;
            this.wrestlerCount = wrestlerCount;
        }
    }

    public static class WorkerPayrollResult {
        public int totalPaid;
        public int workerCount;

        public WorkerPayrollResult(int totalPaid, int workerCount) {
            this.totalPaid = totalPaid;
            this.workerCount = workerCount;
        }
    }
}
