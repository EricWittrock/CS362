import java.util.*;
import java.util.stream.Collectors;

public class BenefitsService {

    public static List<BenefitsDeduction> getActiveDeductions(int employeeId) {
        return DataCache.getAllByFilter(
                d -> d.getEmployeeId() == employeeId && d.isActive(),
                BenefitsDeduction::new);
    }

    public static int calculateTotalPreTaxDeductions(int employeeId) {
        return getActiveDeductions(employeeId).stream()
                .filter(BenefitsDeduction::isPreTax)
                .mapToInt(BenefitsDeduction::getAmountPerPayPeriod)
                .sum();
    }

    public static int calculateTotalPostTaxDeductions(int employeeId) {
        return getActiveDeductions(employeeId).stream()
                .filter(d -> !d.isPreTax())
                .mapToInt(BenefitsDeduction::getAmountPerPayPeriod)
                .sum();
    }

    public static int calculateTaxableIncome(int employeeId, int grossPay) {
        int totalPreTaxDeductions = calculateTotalPreTaxDeductions(employeeId);
        return Math.max(0, grossPay - totalPreTaxDeductions);
    }

    public static void displayDeductions(int employeeId) {
        List<BenefitsDeduction> deductions = getActiveDeductions(employeeId);

        if (deductions.isEmpty()) {
            System.out.println("  No benefit deductions enrolled.");
            return;
        }

        System.out.println("\n  BENEFIT DEDUCTIONS:");
        int totalPreTax = 0;
        int totalPostTax = 0;

        for (BenefitsDeduction d : deductions) {
            String taxType = d.isPreTax() ? "(Pre-Tax)" : "(Post-Tax)";
            System.out.println("    • " + d.getType().getDisplayName() + ": $" +
                    d.getAmountPerPayPeriod() + " " + taxType);

            if (d.isPreTax()) {
                totalPreTax += d.getAmountPerPayPeriod();
            } else {
                totalPostTax += d.getAmountPerPayPeriod();
            }
        }

        System.out.println("    " + "-".repeat(50));
        System.out.println("    Total Pre-Tax Deductions: $" + totalPreTax);
        System.out.println("    Total Post-Tax Deductions: $" + totalPostTax);
    }
}
