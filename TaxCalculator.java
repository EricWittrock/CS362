import java.util.*;

public class TaxCalculator {
    private static final double SOCIAL_SECURITY_RATE = 0.062;
    private static final double MEDICARE_RATE = 0.0145;
    private static final double ADDITIONAL_MEDICARE_RATE = 0.009;
    private static final int SOCIAL_SECURITY_WAGE_CAP = 168600;
    private static final int ADDITIONAL_MEDICARE_THRESHOLD = 200000;
    private static final double SELF_EMPLOYMENT_TAX_RATE = 0.153;

    public static TaxBreakdown calculateTaxes(int grossPay, boolean isContractor,
            String eventLocation, int yearToDateEarnings,
            int employeeId) {
        TaxBreakdown breakdown = new TaxBreakdown();
        breakdown.grossPay = grossPay;

        if (isContractor) {
            return calculateContractorTaxes(breakdown, grossPay);
        }

        // Calculate taxable income after pre-tax deductions
        int preTaxDeductions = BenefitsService.calculateTotalPreTaxDeductions(employeeId);
        int taxableIncome = Math.max(0, grossPay - preTaxDeductions);

        City eventCity = findEventCity(eventLocation);
        boolean isDomesticEvent = isDomesticLocation(eventCity);

        if (isDomesticEvent) {
            calculateFederalTaxes(breakdown, taxableIncome, yearToDateEarnings);
            calculateStateAndLocalTaxes(breakdown, taxableIncome, eventCity);
        } else {
            calculateFederalTaxes(breakdown, taxableIncome, yearToDateEarnings);
        }

        // Calculate net pay: gross - taxes - post-tax deductions
        int postTaxDeductions = BenefitsService.calculateTotalPostTaxDeductions(employeeId);
        breakdown.netPay = grossPay - breakdown.getTotalTaxes() - postTaxDeductions;

        return breakdown;
    }

    public static TaxBreakdown calculateTaxes(int grossPay, boolean isContractor,
            String eventLocation, int yearToDateEarnings) {
        return calculateTaxes(grossPay, isContractor, eventLocation, yearToDateEarnings, -1);
    }

    private static TaxBreakdown calculateContractorTaxes(TaxBreakdown breakdown, int grossPay) {
        breakdown.estSelfEmploymentTax = (int) (grossPay * SELF_EMPLOYMENT_TAX_RATE);
        breakdown.netPay = grossPay;
        return breakdown;
    }

    private static City findEventCity(String location) {
        City city = StaticDataHandler.getCityByName(location);

        if (city == null) {
            ArrayList<City> matches = StaticDataHandler.fuzzySearchCityNames(location, 1);
            city = matches.isEmpty() ? null : matches.get(0);
        }

        return city;
    }

    private static boolean isDomesticLocation(City city) {
        return city != null && "United States".equals(city.getCountry());
    }

    private static void calculateFederalTaxes(TaxBreakdown breakdown, int taxableIncome, int ytdEarnings) {
        breakdown.socialSecurityTax = calculateSocialSecurityTax(taxableIncome, ytdEarnings);
        breakdown.medicareTax = calculateMedicareTax(taxableIncome, ytdEarnings);
        breakdown.federalTax = calculateFederalWithholding(taxableIncome);
    }

    private static void calculateStateAndLocalTaxes(TaxBreakdown breakdown, int taxableIncome, City city) {
        StateTaxInfo stateInfo = StateTaxRates.getStateTaxInfo(city);
        if (stateInfo != null) {
            breakdown.stateTax = (int) (taxableIncome * stateInfo.stateRate);
            breakdown.localTax = (int) (taxableIncome * stateInfo.localRate);
        }
    }

    private static int calculateSocialSecurityTax(int taxableIncome, int ytdEarnings) {
        int remainingBeforeCap = SOCIAL_SECURITY_WAGE_CAP - ytdEarnings;

        if (remainingBeforeCap <= 0) {
            return 0;
        }

        int taxableAmount = Math.min(taxableIncome, remainingBeforeCap);
        return (int) (taxableAmount * SOCIAL_SECURITY_RATE);
    }

    private static int calculateMedicareTax(int taxableIncome, int ytdEarnings) {
        int baseMedicare = (int) (taxableIncome * MEDICARE_RATE);

        int newYtdEarnings = ytdEarnings + taxableIncome;
        if (newYtdEarnings <= ADDITIONAL_MEDICARE_THRESHOLD) {
            return baseMedicare;
        }

        int amountOverThreshold = newYtdEarnings - ADDITIONAL_MEDICARE_THRESHOLD;
        int additionalMedicare = (int) (amountOverThreshold * ADDITIONAL_MEDICARE_RATE);

        return baseMedicare + additionalMedicare;
    }

    private static int calculateFederalWithholding(int taxableIncome) {
        if (taxableIncome <= 11000) {
            return (int) (taxableIncome * 0.10);
        }
        if (taxableIncome <= 44725) {
            return 1100 + (int) ((taxableIncome - 11000) * 0.12);
        }
        if (taxableIncome <= 95375) {
            return 5147 + (int) ((taxableIncome - 44725) * 0.22);
        }
        return 16290 + (int) ((taxableIncome - 95375) * 0.24);
    }
}
