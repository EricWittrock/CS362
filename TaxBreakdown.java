import java.util.*;

public class TaxBreakdown implements DatabaseObject {
    private int breakdownId;
    public int paymentId;
    public int grossPay;
    public int socialSecurityTax;
    public int medicareTax;
    public int federalTax;
    public int stateTax;
    public int localTax;
    public int estSelfEmploymentTax;
    public int netPay;
    private int taxYear;

    public TaxBreakdown() {
        this.taxYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    public TaxBreakdown(int paymentId, int grossPay) {
        this.breakdownId = new Random().nextInt(Integer.MAX_VALUE);
        this.paymentId = paymentId;
        this.grossPay = grossPay;
        this.taxYear = Calendar.getInstance().get(Calendar.YEAR);
        DataCache.addObject(this);
    }

    public int getTotalTaxes() {
        return socialSecurityTax + medicareTax + federalTax + stateTax + localTax;
    }

    // Getters
    public int getBreakdownId() {
        return breakdownId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getGrossPay() {
        return grossPay;
    }

    public int getSocialSecurityTax() {
        return socialSecurityTax;
    }

    public int getMedicareTax() {
        return medicareTax;
    }

    public int getFederalTax() {
        return federalTax;
    }

    public int getStateTax() {
        return stateTax;
    }

    public int getLocalTax() {
        return localTax;
    }

    public int getNetPay() {
        return netPay;
    }

    public int getEstSelfEmploymentTax() {
        return estSelfEmploymentTax;
    }

    public int getTaxYear() {
        return taxYear;
    }

    @Override
    public int getId() {
        return breakdownId;
    }

    @Override
    public String serialize() {
        return breakdownId + "," + paymentId + "," + grossPay + "," +
                socialSecurityTax + "," + medicareTax + "," + federalTax + "," +
                stateTax + "," + localTax + "," + estSelfEmploymentTax + "," +
                netPay + "," + taxYear;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 11);
        this.breakdownId = Integer.parseInt(parts[0]);
        this.paymentId = Integer.parseInt(parts[1]);
        this.grossPay = Integer.parseInt(parts[2]);
        this.socialSecurityTax = Integer.parseInt(parts[3]);
        this.medicareTax = Integer.parseInt(parts[4]);
        this.federalTax = Integer.parseInt(parts[5]);
        this.stateTax = Integer.parseInt(parts[6]);
        this.localTax = Integer.parseInt(parts[7]);
        this.estSelfEmploymentTax = Integer.parseInt(parts[8]);
        this.netPay = Integer.parseInt(parts[9]);
        this.taxYear = Integer.parseInt(parts[10]);
    }
}
