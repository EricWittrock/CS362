import java.util.List;

public class PaymentCalculator {
    private static final int HIGH_RISK_BONUS_PERCENT = 20;
    private static final int HIGH_RISK_THRESHOLD = 8;
    private static final int OVERTIME_HOURS_THRESHOLD = 160;
    private static final int OVERTIME_MULTIPLIER_NUMERATOR = 3;
    private static final int OVERTIME_MULTIPLIER_DENOMINATOR = 2;
    private static final int HAZARD_BONUS_PERCENT = 20;
    private static final int DANGER_THRESHOLD_FOR_HAZARD = 7;

    public static int calculateWrestlerBonus(int basePay, List<ScriptAction> actions) {
        int bonusAmount = 0;
        for (ScriptAction action : actions) {
            if (action.getDangerRating() >= HIGH_RISK_THRESHOLD) {
                bonusAmount += basePay / 5; // 20%
            }
        }
        return bonusAmount;
    }

    public static int countHighRiskActions(List<ScriptAction> actions) {
        int count = 0;
        for (ScriptAction action : actions) {
            if (action.getDangerRating() >= HIGH_RISK_THRESHOLD) {
                count++;
            }
        }
        return count;
    }

    public static int calculateOvertimePay(int overtimeHours, int hourlyRate) {
        return (overtimeHours * hourlyRate * OVERTIME_MULTIPLIER_NUMERATOR)
                / OVERTIME_MULTIPLIER_DENOMINATOR;
    }

    public static int calculateHazardPay(int hazardousHours, int hourlyRate) {
        return (hazardousHours * hourlyRate) / 5; // 20%
    }

    public static boolean isHazardousEvent(Event event) {
        // Check if event has high-danger script
        Script script = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getEventId() == event.getId())
                .filter(s -> s.getStatus() == ScriptStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (script == null)
            return false;

        for (Integer actionId : script.getActionIds()) {
            ScriptAction action = DataCache.getById(actionId, ScriptAction::new);
            if (action != null && action.getDangerRating() >= DANGER_THRESHOLD_FOR_HAZARD) {
                return true;
            }
        }
        return false;
    }
}
