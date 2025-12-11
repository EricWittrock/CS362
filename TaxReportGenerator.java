import java.util.*;

public class TaxReportGenerator {

    public static void displayEmployeeTaxRecords() {
        List<EmployeeTaxRecord> records = DataCache.getAll(EmployeeTaxRecord::new);

        if (records.isEmpty()) {
            displayNoRecordsMessage();
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("EMPLOYEE TAX RECORDS (YEAR-TO-DATE)");
        System.out.println("=".repeat(80));

        for (EmployeeTaxRecord record : records) {
            displayTaxRecord(record);
        }

        System.out.println("=".repeat(80));
    }

    private static void displayTaxRecord(EmployeeTaxRecord record) {
        String employeeName = "Unknown";
        String employeeType = "";

        Worker worker = DataCache.getById(record.getEmployeeId(), Worker::new);
        if (worker != null) {
            employeeName = worker.getName();
            employeeType = " (Worker)";
        } else {
            Wrestler wrestler = DataCache.getById(record.getWrestlerId(), Wrestler::new);
            if (wrestler != null) {
                employeeName = wrestler.getName();
                employeeType = " (Wrestler)";
            } else {
                employeeName = "Deleted Employee";
                employeeType = " (ID: " + record.getEmployeeId() + ")";
                System.err.println("Warning: Employee " + record.getEmployeeId() + " not found");
            }
        }

        System.out.println("\nEmployee: " + employeeName + employeeType);
        System.out.println("Employee ID: " + record.getEmployeeId());
        System.out.println("  Tax Year: " + record.getTaxYear());
        System.out.println("  YTD Gross Pay: $" + String.format("%,d", record.getYtdGrossPay()));

        // Show benefits deductions if worker
        if (worker != null) {
            int preTaxDeductions = BenefitsService.calculateTotalPreTaxDeductions(record.getEmployeeId());
            int postTaxDeductions = BenefitsService.calculateTotalPostTaxDeductions(record.getEmployeeId());

            if (preTaxDeductions > 0 || postTaxDeductions > 0) {
                System.out.println("  YTD Pre-Tax Deductions: $" + String.format("%,d", preTaxDeductions * 12)); // Approximate
                                                                                                                 // annual
                System.out.println("  YTD Post-Tax Deductions: $" + String.format("%,d", postTaxDeductions * 12));
            }
        }

        System.out.println("  YTD Social Security: $" + String.format("%,d", record.getYtdSocialSecurity()));
        System.out.println("  YTD Medicare: $" + String.format("%,d", record.getYtdMedicare()));
        System.out.println("  YTD Federal: $" + String.format("%,d", record.getYtdFederal()));
        System.out.println("  YTD State: $" + String.format("%,d", record.getYtdState()));
        System.out.println("  " + "-".repeat(40));
        System.out.println("  YTD Total Taxes: $" + String.format("%,d", record.getTotalYtdTaxes()));
        System.out.println("  YTD Net Income: $" + String.format("%,d", record.getYtdNetIncome()));

        if (record.hasSocialSecurityCapBeenReached()) {
            System.out.println("  [Social Security wage cap reached]");
        }

        // Show active benefits
        if (worker != null) {
            BenefitsService.displayDeductions(record.getEmployeeId());
        }
    }

    public static void displayPaymentTaxBreakdowns() {
        List<TaxBreakdown> breakdowns = DataCache.getAll(TaxBreakdown::new);

        if (breakdowns.isEmpty()) {
            System.out.println("\nNo tax breakdowns found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TAX BREAKDOWN BY PAYMENT");
        System.out.println("=".repeat(80));

        for (TaxBreakdown breakdown : breakdowns) {
            displayTaxBreakdown(breakdown);
        }

        System.out.println("=".repeat(80));
    }

    private static void displayTaxBreakdown(TaxBreakdown breakdown) {
        System.out.println("\nPayment ID: " + breakdown.getPaymentId());
        System.out.println("  Gross Pay: $" + String.format("%,d", breakdown.getGrossPay()));

        if (breakdown.getEstSelfEmploymentTax() > 0) {
            displayContractorBreakdown(breakdown);
        } else {
            displayEmployeeBreakdown(breakdown);
        }
    }

    private static void displayContractorBreakdown(TaxBreakdown breakdown) {
        System.out.println("  [1099 CONTRACTOR]");
        System.out.println("  Est. Self-Employment Tax: $" +
                String.format("%,d", breakdown.getEstSelfEmploymentTax()));
        System.out.println("  Net Pay (no withholding): $" +
                String.format("%,d", breakdown.getNetPay()));
    }

    private static void displayEmployeeBreakdown(TaxBreakdown breakdown) {
        System.out.println("  [W2 EMPLOYEE]");
        System.out.println("  Social Security: $" + String.format("%,d", breakdown.getSocialSecurityTax()));
        System.out.println("  Medicare: $" + String.format("%,d", breakdown.getMedicareTax()));
        System.out.println("  Federal: $" + String.format("%,d", breakdown.getFederalTax()));
        System.out.println("  State/Local: $" + String.format("%,d",
                breakdown.getStateTax() + breakdown.getLocalTax()));
        System.out.println("  " + "-".repeat(40));
        System.out.println("  Total Withheld: $" + String.format("%,d", breakdown.getTotalTaxes()));
        System.out.println("  Net Pay: $" + String.format("%,d", breakdown.getNetPay()));
    }

    public static void displayW2Summary() {
        Map<Integer, EmployeeSummary> summaries = aggregateW2Payments();

        if (summaries.isEmpty()) {
            System.out.println("\nNo W2 employee data found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("W2 EMPLOYEE SUMMARY (Current Year)");
        System.out.println("=".repeat(80));

        for (EmployeeSummary summary : summaries.values()) {
            System.out.println("\nWorker: " + summary.name);
            System.out.println("  Total Earnings: $" + String.format("%,d", summary.totalPay));
            System.out.println("  Total Taxes: $" + String.format("%,d", summary.totalTaxes));
            System.out.println("  Net Income: $" + String.format("%,d",
                    summary.totalPay - summary.totalTaxes));
        }

        System.out.println("\n" + "=".repeat(80));
    }

    public static void display1099Summary() {
        Map<Integer, ContractorSummary> summaries = aggregate1099Payments();

        if (summaries.isEmpty()) {
            System.out.println("\nNo 1099 contractor data found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("1099 CONTRACTOR SUMMARY (Current Year)");
        System.out.println("=".repeat(80));

        for (ContractorSummary summary : summaries.values()) {
            System.out.println("\nContractor: " + summary.name);
            System.out.println("  Total Payments: $" + String.format("%,d", summary.totalPayments));
            System.out.println("  Est. Self-Employment Tax: $" +
                    String.format("%,d", summary.estimatedTaxes));
            System.out.println("  (Contractor responsible for paying)");
        }

        System.out.println("\n" + "=".repeat(80));
    }

    private static void displayNoRecordsMessage() {
        System.out.println("\nNo tax records found.");
        System.out.println("Tax records are created when you:");
        System.out.println("  1. Process worker payroll (W2 employees)");
        System.out.println("  2. Pay wrestlers (1099 contractors)");
        System.out.println("\nProcess payments first to see tax data.");
    }

    private static Map<Integer, EmployeeSummary> aggregateW2Payments() {
        List<WorkerPayment> payments = DataCache.getAll(WorkerPayment::new);
        Map<Integer, EmployeeSummary> summaries = new HashMap<>();

        for (WorkerPayment payment : payments) {
            int workerId = payment.getWorkerId();
            summaries.putIfAbsent(workerId, new EmployeeSummary(workerId));
            summaries.get(workerId).addPayment(payment);
        }

        return summaries;
    }

    private static Map<Integer, ContractorSummary> aggregate1099Payments() {
        List<WrestlerPayment> payments = DataCache.getAll(WrestlerPayment::new);
        Map<Integer, ContractorSummary> summaries = new HashMap<>();

        for (WrestlerPayment payment : payments) {
            if (!payment.is1099())
                continue;

            int wrestlerId = payment.getWrestlerId();
            summaries.putIfAbsent(wrestlerId, new ContractorSummary(wrestlerId));
            summaries.get(wrestlerId).addPayment(payment);
        }

        return summaries;
    }

    private static class EmployeeSummary {
        String name;
        int totalPay;
        int totalTaxes;

        EmployeeSummary(int workerId) {
            Worker worker = DataCache.getById(workerId, Worker::new);
            this.name = worker != null ? worker.getName() : "Unknown Worker #" + workerId;
            this.totalPay = 0;
            this.totalTaxes = 0;
        }

        void addPayment(WorkerPayment payment) {
            this.totalPay += payment.getTotalPay();

            EmployeeTaxRecord record = TaxService.getOrCreateTaxRecord(payment.getWorkerId());
            this.totalTaxes = record.getTotalYtdTaxes();
        }
    }

    private static class ContractorSummary {
        String name;
        int totalPayments;
        int estimatedTaxes;

        ContractorSummary(int wrestlerId) {
            Wrestler wrestler = DataCache.getById(wrestlerId, Wrestler::new);
            this.name = wrestler != null ? wrestler.getName() : "Unknown Wrestler #" + wrestlerId;
            this.totalPayments = 0;
            this.estimatedTaxes = 0;
        }

        void addPayment(WrestlerPayment payment) {
            this.totalPayments += payment.getTotalPay();
            this.estimatedTaxes += payment.getEstSelfEmploymentTax();
        }
    }
}
