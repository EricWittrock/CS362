import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RehearsalValidator {
    private static final double CREW_HOURLY_RATE = 50.0;
    private static final int MIN_CREW_COUNT = 5;
    private static final int MIN_ADVANCE_HOURS = 24;

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
                System.out.println("\nERRORS:");
                for(String error : errors) {
                    System.out.println(" - " + error);
                }
            }

            if(!warnings.isEmpty()) {
                System.out.println("\nWARNINGS:");
                for(String warning : warnings) {
                    System.out.println(" - " + warning);
                }
            }

            if (isValid && warnings.isEmpty()) {
                System.out.println("\n Rehearsal session passed validation with no issues.");
            }
        }
    }

    public static ValidateResult validateRehearsal(Script script, Venue venue,
                                                 long scheduledDate, int duration) {
        ValidateResult result = new ValidateResult();
        // check 1
        if (script.getStatus() != ScriptStatus.APPROVED) {
            result.addError("Script must be APPROVED before scheduling a rehearsal. Current status: " + script.getStatus());
            return result;
        }

        // check 2
        if (script.getActionIds().isEmpty()) {
            result.addError("Script has no actions.");
            return result;
        }

        // check 3
        long now = System.currentTimeMillis();
        long hoursUntilRehearsal = (scheduledDate - now) / (1000 * 60 * 60);
        if (hoursUntilRehearsal < MIN_ADVANCE_HOURS) {
            result.addError("Rehearsal must be scheduled at least " + MIN_ADVANCE_HOURS + " hours in advance. Currently: " + hoursUntilRehearsal + " hours.");
        }

        // check 4
        if (scheduledDate <= now) {
            result.addError("Scheduled date must be in the future.");
        }

        // check 5
        List<Integer> wrestlerIds = script.getRequiredWrestlerIds();
        if (wrestlerIds.isEmpty()) {
            result.addError("Script has no wrestlers assigned.");
            return result;
        }

        for (Integer wrestlerId : wrestlerIds) {
            Wrestler wrestler = DataCache.getById(wrestlerId, Wrestler::new);
            if (wrestler == null) {
                result.addError("Wrestler with ID " + wrestlerId + " not found.");
                continue;
            }

            // check conflict
            if (hasScheduleConflict(wrestlerId, scheduledDate, duration)) {
                result.addError("Wrestler " + wrestler.getName() +  " has a scheduling conflict.");
            }
        }

        // check 6
        if (duration < 30) {
            result.addError("Rehearsal duration must be at least 30 minutes.");
        } else if (duration > 480) {
            result.addWarning("Rehearsal duration exceeds 8 hours. This may require additional crew shifts.");
        }

        // check 7: Budget validation
        Budget rehearsalBudget = Budget.get("Rehearsal");
        if (rehearsalBudget != null) {
            double estimatedCost = calculateRehearsalCost(venue, duration);
            int costToCharge = (int) Math.ceil(estimatedCost);
            if (rehearsalBudget.funds() < costToCharge) {
                result.addError("Rehearsal budget is insufficient. Required: $" + costToCharge +
                               " (estimated: $" + String.format("%.2f", estimatedCost) + ")" +
                               ", Available: $" + rehearsalBudget.funds());
            }
        } else {
            result.addError("Rehearsal budget does not exist. Please create a rehearsal budget first.");
        }

        // check 8
        double riskScore = script.calculateTotalRisk();
        if (riskScore > 7.0) {
            result.addWarning("High risk script (risk: " + String.format("%.2f", riskScore) + "). Consider extended rehearsal time.");

            int recommended = calculateRecommendedDuration(script);
            if (duration < recommended) {
                result.addWarning("Recommended rehearsal duration for this script is at least " + recommended + " minutes.");
            }
        }
        return result;
    }

    private static boolean hasScheduleConflict(int wrestlerId, long rehearsalDate, int duration) {
        long rehearsalEnd = rehearsalDate + (duration * 60 * 1000L);

        // Check conflicts with other rehearsals
        ArrayList<RehearsalSession> allRehearsals = DataCache.getAll(RehearsalSession::new);
        for (RehearsalSession existing : allRehearsals) {
            if (existing.getStatus() == RehearsalStatus.CANCELED) {
                continue;
            }

            if (!existing.getWrestlerIds().contains(wrestlerId)) {
                continue;
            }

            long existingEnd = existing.getScheduledDate() +
                                (existing.getDuration() * 60 * 1000L);
            
            // check overlap
            if (!(rehearsalEnd <= existing.getScheduledDate() ||
                  rehearsalDate >= existingEnd)) {
                return true;
            }
        }

        // Check conflicts with events
        ArrayList<WrestlerSchedule> allSchedules = DataCache.getAll(WrestlerSchedule::new);
        for (WrestlerSchedule schedule : allSchedules) {
            if (schedule.getWrestlerId() != wrestlerId) {
                continue;
            }

            if (schedule.isEvent() && schedule.getEventId() != -1) {
                Event event = DataCache.getById(schedule.getEventId(), Event::new);
                if (event != null) {
                    try {
                        SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date eventDate = eventDateFormat.parse(event.getDate());
                        long eventStart = eventDate.getTime();
                        long eventEnd = eventStart + (240 * 60 * 1000L); // assuming 4 hour event
                        
                        if (!(rehearsalEnd <= eventStart || rehearsalDate >= eventEnd)) {
                            return true;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        return false;
    }

    public static double calculateRehearsalCost(Venue venue, int duration) {
        double hours = duration / 60.0;
        double crewCost = MIN_CREW_COUNT * CREW_HOURLY_RATE * hours;

        // simple cost calculation. use base hourly cost of venue
        double venueHourlyRate = 100.0; // default
        double venueCost = venueHourlyRate * hours;

        return venueCost + crewCost;
    }

    public static int calculateRecommendedDuration(Script script) {
        int baseDuration = 60; // base 1 hour min
        int actionCount = script.getActionIds().size();
        int actionTime = actionCount * 15; // 15 mins per action

        int totalDuration = baseDuration + actionTime;

        // add more time for high risk actions
        double riskScore = script.calculateTotalRisk();
        if (riskScore > 7.0) {
            totalDuration = (int)(totalDuration * 1.5);
        }

        return totalDuration;
    }
}
