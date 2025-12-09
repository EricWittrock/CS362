public class TaxLiabilitySummary {
    private final int taxYear;
    private int totalEmployees;
    private int totalGrossPayroll;
    private int totalSocialSecurity;
    private int totalMedicare;
    private int totalFederalWithholding;
    private int totalStateWithholding;

    public TaxLiabilitySummary(int taxYear) {
        this.taxYear = taxYear;
        this.totalEmployees = 0;
        this.totalGrossPayroll = 0;
        this.totalSocialSecurity = 0;
        this.totalMedicare = 0;
        this.totalFederalWithholding = 0;
        this.totalStateWithholding = 0;
    }

    public void addRecord(EmployeeTaxRecord record) {
        this.totalEmployees++;
        this.totalGrossPayroll += record.getYtdGrossPay();
        this.totalSocialSecurity += record.getYtdSocialSecurity();
        this.totalMedicare += record.getYtdMedicare();
        this.totalFederalWithholding += record.getYtdFederal();
        this.totalStateWithholding += record.getYtdState();
    }

    public int getTotalTaxLiability() {
        return totalSocialSecurity + totalMedicare +
                totalFederalWithholding + totalStateWithholding;
    }

    public void print() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TAX LIABILITY SUMMARY - " + taxYear);
        System.out.println("=".repeat(60));
        System.out.println("Total Employees: " + totalEmployees);
        System.out.println("\nPAYROLL TOTALS:");
        System.out.println("  Gross Payroll: $" + String.format("%,d", totalGrossPayroll));
        System.out.println("\nTAX WITHHOLDINGS:");
        System.out.println("  Social Security: $" + String.format("%,d", totalSocialSecurity));
        System.out.println("  Medicare: $" + String.format("%,d", totalMedicare));
        System.out.println("  Federal: $" + String.format("%,d", totalFederalWithholding));
        System.out.println("  State/Local: $" + String.format("%,d", totalStateWithholding));
        System.out.println("  " + "-".repeat(40));
        System.out.println("  TOTAL LIABILITY: $" + String.format("%,d", getTotalTaxLiability()));
        System.out.println("=".repeat(60));
    }

    // Getters
    public int getTaxYear() {
        return taxYear;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getTotalGrossPayroll() {
        return totalGrossPayroll;
    }

    public int getTotalSocialSecurity() {
        return totalSocialSecurity;
    }

    public int getTotalMedicare() {
        return totalMedicare;
    }

    public int getTotalFederalWithholding() {
        return totalFederalWithholding;
    }

    public int getTotalStateWithholding() {
        return totalStateWithholding;
    }
}
