import java.util.*;

public class EmployeeTaxRecord implements DatabaseObject {
    private static final int SOCIAL_SECURITY_WAGE_CAP = 168600;

    private int recordId;
    private int employeeId;
    private int taxYear;
    private int ytdGrossPay;
    private int ytdSocialSecurity;
    private int ytdMedicare;
    private int ytdFederal;
    private int ytdState;
    private long socialSecurityCapHitDate;

    public EmployeeTaxRecord() {
        this.taxYear = getCurrentTaxYear();
    }

    public EmployeeTaxRecord(int employeeId) {
        this.recordId = generateRecordId();
        this.employeeId = employeeId;
        this.taxYear = getCurrentTaxYear();
        this.ytdGrossPay = 0;
        this.ytdSocialSecurity = 0;
        this.ytdMedicare = 0;
        this.ytdFederal = 0;
        this.ytdState = 0;
        this.socialSecurityCapHitDate = 0;
        DataCache.addObject(this);
    }

    public void recordPayment(TaxBreakdown breakdown) {
        if (breakdown == null) {
            throw new IllegalArgumentException("Tax breakdown cannot be null");
        }

        this.ytdGrossPay += breakdown.getGrossPay();
        this.ytdSocialSecurity += breakdown.getSocialSecurityTax();
        this.ytdMedicare += breakdown.getMedicareTax();
        this.ytdFederal += breakdown.getFederalTax();
        this.ytdState += breakdown.getStateTax();

        checkSocialSecurityCapReached(breakdown);
        DataCache.addObject(this);
    }

    private void checkSocialSecurityCapReached(TaxBreakdown breakdown) {
        boolean capJustReached = breakdown.getSocialSecurityTax() == 0
                && this.ytdGrossPay > SOCIAL_SECURITY_WAGE_CAP
                && socialSecurityCapHitDate == 0;

        if (capJustReached) {
            socialSecurityCapHitDate = System.currentTimeMillis();
        }
    }

    public int getTotalYtdTaxes() {
        return ytdSocialSecurity + ytdMedicare + ytdFederal + ytdState;
    }

    public int getYtdNetIncome() {
        return ytdGrossPay - getTotalYtdTaxes();
    }

    public boolean hasSocialSecurityCapBeenReached() {
        return ytdGrossPay >= SOCIAL_SECURITY_WAGE_CAP;
    }

    // Getters
    public int getRecordId() {
        return recordId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getTaxYear() {
        return taxYear;
    }

    public int getYtdGrossPay() {
        return ytdGrossPay;
    }

    public int getYtdSocialSecurity() {
        return ytdSocialSecurity;
    }

    public int getYtdMedicare() {
        return ytdMedicare;
    }

    public int getYtdFederal() {
        return ytdFederal;
    }

    public int getYtdState() {
        return ytdState;
    }

    public long getSocialSecurityCapHitDate() {
        return socialSecurityCapHitDate;
    }

    @Override
    public int getId() {
        return recordId;
    }

    @Override
    public String serialize() {
        return String.join(",",
                String.valueOf(recordId),
                String.valueOf(employeeId),
                String.valueOf(taxYear),
                String.valueOf(ytdGrossPay),
                String.valueOf(ytdSocialSecurity),
                String.valueOf(ytdMedicare),
                String.valueOf(ytdFederal),
                String.valueOf(ytdState),
                String.valueOf(socialSecurityCapHitDate));
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 9);
        this.recordId = Integer.parseInt(parts[0]);
        this.employeeId = Integer.parseInt(parts[1]);
        this.taxYear = Integer.parseInt(parts[2]);
        this.ytdGrossPay = Integer.parseInt(parts[3]);
        this.ytdSocialSecurity = Integer.parseInt(parts[4]);
        this.ytdMedicare = Integer.parseInt(parts[5]);
        this.ytdFederal = Integer.parseInt(parts[6]);
        this.ytdState = Integer.parseInt(parts[7]);
        this.socialSecurityCapHitDate = Long.parseLong(parts[8]);
    }

    private static int getCurrentTaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private static int generateRecordId() {
        return new Random().nextInt(Integer.MAX_VALUE);
    }
}
