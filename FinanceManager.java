public class FinanceManager implements Actor {
    private WrestlerPaymentService wrestlerPaymentService;
    private WorkerPayrollService workerPayrollService;
    private PaymentHistoryService historyService;

    public FinanceManager() {
        this.wrestlerPaymentService = new WrestlerPaymentService();
        this.workerPayrollService = new WorkerPayrollService();
        this.historyService = new PaymentHistoryService();
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
            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 4);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                wrestlerPaymentService.payWrestlersForEvent();
            } else if (choice == 2) {
                workerPayrollService.processWorkerPayroll();
            } else if (choice == 3) {
                historyService.viewWrestlerPaymentHistory();
            } else if (choice == 4) {
                historyService.viewWorkerPaymentHistory();
            }
        }
    }
}