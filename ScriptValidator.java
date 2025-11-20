import java.util.*;

public class ScriptValidator {
    private static final double MAX_RISK_THRESHOLD = 7.5;
    private static final double MAX_INSURANCE_BUDGET = 5000.0;
    
    public static class ValidateResult {
        private boolean isValid;
        private List<String> errors;
        private List<String> warnings;

        public ValidateResult() {
            this.isValid = true;
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
        }

        public void addError(String error) {
            errors.add(error);
            isValid = false;
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        public boolean isValid() { return isValid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }

        public void print() {
            if(!errors.isEmpty()) {
                System.out.println("ERRORS:");
                for(String error : errors) {
                    System.out.println(" - " + error);
                }
            }

            if(!warnings.isEmpty()) {
                System.out.println("WARNINGS:");
                for(String warning : warnings) {
                    System.out.println(" - " + warning);
                }
            }

            if (isValid && warnings.isEmpty()) {
                System.out.println("\n Script passed validation with no issues.");
            }
        }
    }

    public static ValidateResult validateScript(Script script) {
        ValidateResult result = new ValidateResult();

        // check 1
        if (script.getActionIds().isEmpty()) {
            result.addError("Script has no actions.");
            return result;
        }

        // check 2
        double totalRisk = script.calculateTotalRisk();
        if (totalRisk > MAX_RISK_THRESHOLD) {
            result.addError("Total risk score(" + String.format("%.2f", totalRisk) + ") exceeds maximum threshold (" + MAX_RISK_THRESHOLD + ").");
        } else if (totalRisk > MAX_RISK_THRESHOLD * 0.8) {
            result.addWarning("Total risk score(" + String.format("%.2f", totalRisk) + ") is approaching maximum threshold (" + MAX_RISK_THRESHOLD + ").");
        }
        
        // check 3
        double insuranceCost = script.calculateInsuranceCost();
        if (insuranceCost > MAX_INSURANCE_BUDGET) {
            result.addError("Insurance cost ($" + String.format("%.2f", insuranceCost) + ") exceeds budget ($" + MAX_INSURANCE_BUDGET + ").");
        }

        // check 4
        List<Integer> wrestlerIds = script.getRequiredWrestlerIds();
        for (Integer wrestlerId : wrestlerIds) {
            Wrestler wrestler = DataCache.getById(wrestlerId.intValue(), Wrestler::new);
            if (wrestler == null) {
                result.addError("Wrestler with ID " + wrestlerId + " not found in the system.");
                continue;
            }
            
            WrestlerInsurance insurance = DataCache.getByFilter(
                i -> i.getWrestlerId() == wrestlerId.intValue(), 
                WrestlerInsurance::new
            );
            if (insurance == null) {
                result.addError("Wrestler " + wrestler.getName() + " has no insurance.");
                continue;
            }

            if (insurance.isExpired()) {
                result.addError("Wrestler " + wrestler.getName() + "'s insurance is expired.");
                continue;
            }

            // check if all actions involving this wrestler are covered
            for (Integer actionId : script.getActionIds()) {
                ScriptAction action = DataCache.getById(actionId.intValue(), ScriptAction::new);
                if (action != null && action.getWrestlerIds().contains(wrestlerId)) {
                    if (!insurance.coversAction(action)) {
                        result.addError("Wrestler '" + wrestler.getName() + 
                                      "' insurance doesn't cover action: '" + 
                                      action.getDescription() + "' (Type: " + 
                                      action.getActionType() + ", Danger: " + 
                                      action.getDangerRating() + ")");
                    }
                }
            }
        }

        // check 5
        try {
            int eventId = script.getEventId();
            Event event = DataCache.getById(eventId, Event::new);
            if (event == null) {
                result.addError("Event with ID " + eventId + " not found");
            } else {
                // check if another script is already approved for this event
                List<Script> allScripts = DataCache.getAll(Script::new);
                for (Script other : allScripts) {
                    if (other.getScriptId() != script.getScriptId() &&
                        other.getEventId() == script.getEventId() &&
                        other.getStatus() == ScriptStatus.APPROVED) {
                        result.addError("Event already has an approved script (Script ID: " + other.getScriptId() + ")");
                        break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            result.addError("Invalid event ID format: " + script.getEventId());
        }

        return result;
    }
}
