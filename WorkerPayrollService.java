import java.util.*;
import java.util.stream.Collectors;

public class WorkerPayrollService {

    public void processWorkerPayroll() {
        processWorkerPayrollWithReturn();
    }

    public FinanceManager.WorkerPayrollResult processWorkerPayrollWithReturn() {
        System.out.println("\n=== Process Worker Payroll ===");

        List<Worker> workers = DataCache.getAll(Worker::new);
        if (workers.isEmpty()) {
            System.out.println("\nNo workers in system.");
            return new FinanceManager.WorkerPayrollResult(0, 0);
        }

        String period = selectPayrollPeriod();
        WorkerPayrollBatch batch = calculatePayroll(workers, period);

        if (batch.paymentsByDept.isEmpty()) {
            System.out.println("\nNo workers with hours worked in this period.");
            return new FinanceManager.WorkerPayrollResult(0, 0);
        }

        displayPayrollBreakdown(batch, period);

        if (confirmPayroll()) {
            savePayroll(batch, period);
            displaySuccess(batch);
            return new FinanceManager.WorkerPayrollResult(batch.grandTotal, getTotalWorkerCount(batch));
        }

        return new FinanceManager.WorkerPayrollResult(0, 0);
    }

    private int getTotalWorkerCount(WorkerPayrollBatch batch) {
        return batch.paymentsByDept.values().stream()
                .mapToInt(dept -> dept.size())
                .sum();
    }

    private String selectPayrollPeriod() {
        System.out.println("\nSelect payroll period:");
        System.out.println("1. Current Month");
        System.out.println("2. Custom Date Range");
        System.out.print("\nChoice: ");
        int periodChoice = UserInput.getIntInput(1, 2);

        if (periodChoice == 1) {
            Calendar cal = Calendar.getInstance();
            return String.format("%tB %tY", cal, cal);
        } else {
            System.out.print("\nEnter start date (YYYY-MM-DD): ");
            String startStr = UserInput.getStringInput();
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endStr = UserInput.getStringInput();
            return startStr + " to " + endStr;
        }
    }

    private WorkerPayrollBatch calculatePayroll(List<Worker> workers, String period) {
        WorkerPayrollBatch batch = new WorkerPayrollBatch();

        for (Worker worker : workers) {
            if (isWorkerPaidForPeriod(worker.getWorkerId(), period)) {
                System.out.println("  ⚠ " + worker.getName() + " already paid for " + period);
                continue;
            }

            WorkerPaymentInfo paymentInfo = calculateWorkerPayment(worker, period);
            if (paymentInfo == null)
                continue; // No hours worked

            batch.paymentsByDept
                    .computeIfAbsent(worker.getDepartment(), k -> new ArrayList<>())
                    .add(paymentInfo);
            batch.grandTotal += paymentInfo.totalPay;
        }

        return batch;
    }

    private WorkerPaymentInfo calculateWorkerPayment(Worker worker, String period) {
        List<WorkerAssignment> assignments = DataCache.getAll(WorkerAssignment::new).stream()
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

        if (totalHours == 0)
            return null;

        int hourlyRate = worker.getHourlyRate();
        int regularHours = Math.min(totalHours, 160);
        int overtimeHours = Math.max(0, totalHours - 160);

        int basePay = regularHours * hourlyRate;
        int overtimePay = PaymentCalculator.calculateOvertimePay(overtimeHours, hourlyRate);
        int hazardPay = PaymentCalculator.calculateHazardPay(hazardousHours, hourlyRate);
        int totalPay = basePay + overtimePay + hazardPay;

        return new WorkerPaymentInfo(worker, basePay, overtimePay, hazardPay,
                totalPay, totalHours, overtimeHours, hazardousHours);
    }

    private void displayPayrollBreakdown(WorkerPayrollBatch batch, String period) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("PAYROLL BREAKDOWN - " + period);
        System.out.println("=".repeat(100));

        for (Map.Entry<String, List<WorkerPaymentInfo>> entry : batch.paymentsByDept.entrySet()) {
            String dept = entry.getKey();
            List<WorkerPaymentInfo> deptPayments = entry.getValue();
            int deptTotal = deptPayments.stream().mapToInt(p -> p.totalPay).sum();

            System.out.println("\n--- " + dept + " Department ---");
            System.out.println(String.format("%-20s %8s %10s %10s %10s %12s",
                    "Name", "Hours", "Base Pay", "Overtime", "Hazard", "Total"));
            System.out.println("-".repeat(80));

            for (WorkerPaymentInfo info : deptPayments) {
                System.out.println(String.format("%-20s %8d $%9d $%9d $%9d $%11d",
                        info.worker.getName(), info.totalHours, info.basePay,
                        info.overtimePay, info.hazardPay, info.totalPay));
            }

            System.out.println("-".repeat(80));
            System.out.println(String.format("%61s $%11d", "Department Total:", deptTotal));
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println(String.format("%61s $%11d", "GRAND TOTAL:", batch.grandTotal));
        System.out.println("=".repeat(100));
    }

    private boolean confirmPayroll() {
        System.out.print("\nConfirm payroll processing? (yes/no): ");
        return UserInput.getStringInput().equalsIgnoreCase("yes");
    }

    private void savePayroll(WorkerPayrollBatch batch, String period) {
        for (List<WorkerPaymentInfo> deptPayments : batch.paymentsByDept.values()) {
            for (WorkerPaymentInfo info : deptPayments) {
                new WorkerPayment(info.worker.getWorkerId(), info.basePay, info.overtimePay,
                        info.hazardPay, info.totalHours, period);
            }
        }
    }

    private void displaySuccess(WorkerPayrollBatch batch) {
        int count = batch.paymentsByDept.values().stream()
                .mapToInt(List::size).sum();

        System.out.println("\n✓ Payroll processed successfully!");
        System.out.println("  " + count + " workers paid");
        System.out.println("  Total disbursed: $" + batch.grandTotal);
        System.out.println("\n  Note: Budget allocation handled separately");
    }

    private boolean isWorkerPaidForPeriod(int workerId, String period) {
        return DataCache.getAll(WorkerPayment::new).stream()
                .anyMatch(p -> p.getWorkerId() == workerId &&
                        p.getPaymentPeriod().equals(period));
    }

    // Inner classes
    private static class WorkerPayrollBatch {
        Map<String, List<WorkerPaymentInfo>> paymentsByDept = new HashMap<>();
        int grandTotal = 0;
    }

    static class WorkerPaymentInfo {
        Worker worker;
        int basePay;
        int overtimePay;
        int hazardPay;
        int totalPay;
        int totalHours;
        int overtimeHours;
        int hazardousHours;

        WorkerPaymentInfo(Worker worker, int basePay, int overtimePay, int hazardPay,
                int totalPay, int totalHours, int overtimeHours, int hazardousHours) {
            this.worker = worker;
            this.basePay = basePay;
            this.overtimePay = overtimePay;
            this.hazardPay = hazardPay;
            this.totalPay = totalPay;
            this.totalHours = totalHours;
            this.overtimeHours = overtimeHours;
            this.hazardousHours = hazardousHours;
        }
    }
}
