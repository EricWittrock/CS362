import java.util.*;

public class BenefitsPlan implements DatabaseObject {
    private int planId;
    private String planName;
    private String description;
    private int monthlyEmployeeCost; // Total employee pays per month
    private int monthlyEmployerCost; // What company contributes
    private boolean isActive;
    private Map<BenefitsDeduction.DeductionType, Integer> includedBenefits;

    public BenefitsPlan() {
        this.includedBenefits = new HashMap<>();
    }

    public BenefitsPlan(String planName, String description, int monthlyEmployeeCost, int monthlyEmployerCost) {
        this.planId = new Random().nextInt(Integer.MAX_VALUE);
        this.planName = planName;
        this.description = description;
        this.monthlyEmployeeCost = monthlyEmployeeCost;
        this.monthlyEmployerCost = monthlyEmployerCost;
        this.isActive = true;
        this.includedBenefits = new HashMap<>();

        DataCache.addObject(this);
    }

    public void addBenefit(BenefitsDeduction.DeductionType type, int monthlyAmount) {
        includedBenefits.put(type, monthlyAmount);
        DataCache.addObject(this);
    }

    public void enrollWorker(int workerId) {
        for (Map.Entry<BenefitsDeduction.DeductionType, Integer> entry : includedBenefits.entrySet()) {
            int perPaycheckAmount = (entry.getValue() * 12) / 26;

            new BenefitsDeduction(workerId, entry.getKey(), perPaycheckAmount);
        }
    }

    public int getAnnualEmployeeCost() {
        return monthlyEmployeeCost * 12;
    }

    public int getAnnualEmployerCost() {
        return monthlyEmployerCost * 12;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        DataCache.addObject(this);
    }

    // Getters
    public int getPlanId() {
        return planId;
    }

    public String getPlanName() {
        return planName;
    }

    public String getDescription() {
        return description;
    }

    public int getMonthlyEmployeeCost() {
        return monthlyEmployeeCost;
    }

    public int getMonthlyEmployerCost() {
        return monthlyEmployerCost;
    }

    public boolean isActive() {
        return isActive;
    }

    public Map<BenefitsDeduction.DeductionType, Integer> getIncludedBenefits() {
        return new HashMap<>(includedBenefits);
    }

    @Override
    public int getId() {
        return planId;
    }

    @Override
    public String serialize() {
        StringBuilder benefitsStr = new StringBuilder();
        for (Map.Entry<BenefitsDeduction.DeductionType, Integer> entry : includedBenefits.entrySet()) {
            benefitsStr.append(entry.getKey().name())
                    .append(":")
                    .append(entry.getValue())
                    .append(";");
        }

        return planId + "," + planName + "," + description + "," +
                monthlyEmployeeCost + "," + monthlyEmployerCost + "," +
                isActive + "," + benefitsStr.toString();
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 7);
        this.planId = Integer.parseInt(parts[0]);
        this.planName = parts[1];
        this.description = parts[2];
        this.monthlyEmployeeCost = Integer.parseInt(parts[3]);
        this.monthlyEmployerCost = Integer.parseInt(parts[4]);
        this.isActive = Boolean.parseBoolean(parts[5]);

        this.includedBenefits = new HashMap<>();
        if (parts.length > 6 && !parts[6].isEmpty()) {
            String[] benefits = parts[6].split(";");
            for (String benefit : benefits) {
                if (!benefit.trim().isEmpty()) {
                    String[] pair = benefit.split(":");
                    BenefitsDeduction.DeductionType type = BenefitsDeduction.DeductionType.valueOf(pair[0]);
                    int amount = Integer.parseInt(pair[1]);
                    includedBenefits.put(type, amount);
                }
            }
        }
    }

    public void print() {
        System.out.println("\n=== " + planName + " ===");
        System.out.println("Description: " + description);
        System.out.println("Employee Cost: $" + monthlyEmployeeCost + "/month ($" + getAnnualEmployeeCost() + "/year)");
        System.out.println("Employer Contribution: $" + monthlyEmployerCost + "/month");
        System.out.println("Status: " + (isActive ? "Active" : "Inactive"));

        if (!includedBenefits.isEmpty()) {
            System.out.println("\nIncluded Benefits:");
            for (Map.Entry<BenefitsDeduction.DeductionType, Integer> entry : includedBenefits.entrySet()) {
                System.out.println("  • " + entry.getKey().getDisplayName() + ": $" + entry.getValue() + "/month");
            }
        }

        System.out.println("Total Coverage Value: $" + (monthlyEmployeeCost + monthlyEmployerCost) + "/month");
    }
}
