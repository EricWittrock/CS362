import java.util.*;

public class BenefitsPlansService {

    public static void createDefaultPlans() {
        List<BenefitsPlan> existing = DataCache.getAll(BenefitsPlan::new);

        if (!existing.isEmpty()) {
            return; // Plans already exist
        }

        BenefitsPlan basicPlan = new BenefitsPlan(
                "Basic Plan",
                "Essential coverage for entry-level employees",
                150,
                200);
        basicPlan.addBenefit(BenefitsDeduction.DeductionType.HEALTH_INSURANCE, 100);
        basicPlan.addBenefit(BenefitsDeduction.DeductionType.DENTAL_INSURANCE, 30);
        basicPlan.addBenefit(BenefitsDeduction.DeductionType.RETIREMENT_401K, 20);

        BenefitsPlan standardPlan = new BenefitsPlan(
                "Standard Plan",
                "Comprehensive coverage for full-time employees",
                250,
                400);
        standardPlan.addBenefit(BenefitsDeduction.DeductionType.HEALTH_INSURANCE, 150);
        standardPlan.addBenefit(BenefitsDeduction.DeductionType.DENTAL_INSURANCE, 40);
        standardPlan.addBenefit(BenefitsDeduction.DeductionType.VISION_INSURANCE, 20);
        standardPlan.addBenefit(BenefitsDeduction.DeductionType.RETIREMENT_401K, 40);

        BenefitsPlan premiumPlan = new BenefitsPlan(
                "Premium Plan",
                "Premium coverage with maximum benefits",
                350,
                600);
        premiumPlan.addBenefit(BenefitsDeduction.DeductionType.HEALTH_INSURANCE, 200);
        premiumPlan.addBenefit(BenefitsDeduction.DeductionType.DENTAL_INSURANCE, 50);
        premiumPlan.addBenefit(BenefitsDeduction.DeductionType.VISION_INSURANCE, 30);
        premiumPlan.addBenefit(BenefitsDeduction.DeductionType.RETIREMENT_401K, 50);
        premiumPlan.addBenefit(BenefitsDeduction.DeductionType.HSA_CONTRIBUTION, 20);

        BenefitsPlan executivePlan = new BenefitsPlan(
                "Executive Plan",
                "Elite coverage for executive team",
                500,
                1000);
        executivePlan.addBenefit(BenefitsDeduction.DeductionType.HEALTH_INSURANCE, 300);
        executivePlan.addBenefit(BenefitsDeduction.DeductionType.DENTAL_INSURANCE, 75);
        executivePlan.addBenefit(BenefitsDeduction.DeductionType.VISION_INSURANCE, 50);
        executivePlan.addBenefit(BenefitsDeduction.DeductionType.RETIREMENT_401K, 100);
        executivePlan.addBenefit(BenefitsDeduction.DeductionType.HSA_CONTRIBUTION, 50);
        executivePlan.addBenefit(BenefitsDeduction.DeductionType.LIFE_INSURANCE, 25);

        System.out.println("✓ Created 4 default benefits plans");
    }

    public static List<BenefitsPlan> getActivePlans() {
        return DataCache.getAllByFilter(
                plan -> plan.isActive(),
                BenefitsPlan::new);
    }

    public static BenefitsPlan getWorkerPlan(int workerId) {
        List<BenefitsDeduction> deductions = BenefitsService.getActiveDeductions(workerId);

        if (deductions.isEmpty()) {
            return null;
        }

        for (BenefitsPlan plan : getActivePlans()) {
            if (deductionsMatchPlan(deductions, plan)) {
                return plan;
            }
        }

        return null;
    }

    private static boolean deductionsMatchPlan(List<BenefitsDeduction> deductions, BenefitsPlan plan) {
        Map<BenefitsDeduction.DeductionType, Integer> planBenefits = plan.getIncludedBenefits();

        if (deductions.size() != planBenefits.size()) {
            return false;
        }

        for (BenefitsDeduction deduction : deductions) {
            if (!planBenefits.containsKey(deduction.getType())) {
                return false;
            }
        }

        return true;
    }

    public static void unenrollWorker(int workerId) {
        List<BenefitsDeduction> deductions = BenefitsService.getActiveDeductions(workerId);

        for (BenefitsDeduction deduction : deductions) {
            deduction.setActive(false);
        }
    }
}
