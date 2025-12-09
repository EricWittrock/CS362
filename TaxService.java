import java.util.*;

public class TaxService {

    public static EmployeeTaxRecord getOrCreateTaxRecord(int employeeId) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        EmployeeTaxRecord existingRecord = DataCache.getByFilter(
                record -> record.getEmployeeId() == employeeId &&
                        record.getTaxYear() == currentYear,
                EmployeeTaxRecord::new);

        return existingRecord != null ? existingRecord : new EmployeeTaxRecord(employeeId);
    }

    public static TaxBreakdown processEmployeePayment(int employeeId, int grossPay, String location) {
        EmployeeTaxRecord taxRecord = getOrCreateTaxRecord(employeeId);

        TaxBreakdown breakdown = TaxCalculator.calculateTaxes(
                grossPay,
                false, // W2 employee
                location,
                taxRecord.getYtdGrossPay());

        taxRecord.recordPayment(breakdown);
        return breakdown;
    }

    public static TaxBreakdown processContractorPayment(int contractorId, int grossPay, String location) {
        EmployeeTaxRecord taxRecord = getOrCreateTaxRecord(contractorId);

        TaxBreakdown breakdown = TaxCalculator.calculateTaxes(
                grossPay,
                true, // 1099 contractor
                location,
                taxRecord.getYtdGrossPay());

        taxRecord.recordPayment(breakdown);
        return breakdown;
    }

    public static List<EmployeeTaxRecord> getTaxRecordsByYear(int year) {
        return DataCache.getAllByFilter(
                record -> record.getTaxYear() == year,
                EmployeeTaxRecord::new);
    }

    public static TaxLiabilitySummary calculateTotalLiability(int year) {
        List<EmployeeTaxRecord> records = getTaxRecordsByYear(year);

        TaxLiabilitySummary summary = new TaxLiabilitySummary(year);
        for (EmployeeTaxRecord record : records) {
            summary.addRecord(record);
        }

        return summary;
    }
}
