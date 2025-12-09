import java.util.*;

public class BenefitsDeduction implements DatabaseObject {
    private int deductionId;
    private int employeeId;
    private DeductionType type;
    private int amountPerPayPeriod; // Amount deducted per pay period
    private boolean isPreTax;
    private boolean isActive;
    private long startDate;
    private long endDate;

    public enum DeductionType {
        HEALTH_INSURANCE("Health Insurance", true),
        DENTAL_INSURANCE("Dental Insurance", true),
        VISION_INSURANCE("Vision Insurance", true),
        RETIREMENT_401K("401(k) Contribution", true),
        HSA_CONTRIBUTION("Health Savings Account", true),
        FSA_CONTRIBUTION("Flexible Spending Account", true),
        LIFE_INSURANCE("Life Insurance", false),
        UNION_DUES("Union Dues", false),
        WAGE_GARNISHMENT("Wage Garnishment", false);

        private final String displayName;
        private final boolean defaultPreTax;

        DeductionType(String displayName, boolean defaultPreTax) {
            this.displayName = displayName;
            this.defaultPreTax = defaultPreTax;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isDefaultPreTax() {
            return defaultPreTax;
        }
    }

    public BenefitsDeduction() {
    }

    public BenefitsDeduction(int employeeId, DeductionType type, int amountPerPayPeriod) {
        this.deductionId = new Random().nextInt(Integer.MAX_VALUE);
        this.employeeId = employeeId;
        this.type = type;
        this.amountPerPayPeriod = amountPerPayPeriod;
        this.isPreTax = type.isDefaultPreTax();
        this.isActive = true;
        this.startDate = System.currentTimeMillis();
        this.endDate = startDate + (365L * 24 * 60 * 60 * 1000); // 1 year default

        DataCache.addObject(this);
    }

    public int calculateTaxableIncome(int grossPay) {
        if (!isActive || !isPreTax) {
            return grossPay;
        }
        return Math.max(0, grossPay - amountPerPayPeriod);
    }

    public int getPostTaxDeduction() {
        return (isActive && !isPreTax) ? amountPerPayPeriod : 0;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endDate;
    }

    public boolean isActive() {
        return isActive && !isExpired();
    }

    public void setActive(boolean active) {
        this.isActive = active;
        DataCache.addObject(this);
    }

    // Getters
    public int getDeductionId() {
        return deductionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public DeductionType getType() {
        return type;
    }

    public int getAmountPerPayPeriod() {
        return amountPerPayPeriod;
    }

    public boolean isPreTax() {
        return isPreTax;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    @Override
    public int getId() {
        return deductionId;
    }

    @Override
    public String serialize() {
        return deductionId + "," + employeeId + "," + type.name() + "," +
                amountPerPayPeriod + "," + isPreTax + "," + isActive + "," +
                startDate + "," + endDate;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 8);
        this.deductionId = Integer.parseInt(parts[0]);
        this.employeeId = Integer.parseInt(parts[1]);
        this.type = DeductionType.valueOf(parts[2]);
        this.amountPerPayPeriod = Integer.parseInt(parts[3]);
        this.isPreTax = Boolean.parseBoolean(parts[4]);
        this.isActive = Boolean.parseBoolean(parts[5]);
        this.startDate = Long.parseLong(parts[6]);
        this.endDate = Long.parseLong(parts[7]);
    }

    public void print() {
        System.out.println("\n" + type.getDisplayName());
        System.out.println("  Amount: $" + amountPerPayPeriod + " per pay period");
        System.out.println("  Type: " + (isPreTax ? "Pre-Tax" : "Post-Tax"));
        System.out.println("  Status: " + (isActive ? "Active" : "Inactive"));
        System.out.println("  Expires: " + new Date(endDate));
    }
}
