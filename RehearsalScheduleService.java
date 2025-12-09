import java.util.*;
import java.util.stream.Collectors;

import javax.xml.crypto.Data;

import java.text.SimpleDateFormat;

public class RehearsalScheduleService {
    private String choreographerName;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public RehearsalScheduleService(String choreographerName) {
        this.choreographerName = choreographerName;
    }

    public void scheduleRehearsal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCHEDULE REHEARSAL");
        System.out.println("=".repeat(60));

        // check for approved scripts by this choreographer
        ArrayList<Script> allScripts = DataCache.getAll(Script::new);
        List<Script> approvedScripts = allScripts.stream()
                .filter(s -> s.getChoreographer().equals(choreographerName))
                .filter(s -> s.getStatus() == ScriptStatus.APPROVED)
                .collect(Collectors.toList());

        if (approvedScripts.isEmpty()) {
            System.out.println("No approved scripts available for rehearsal.");
            System.out.print("Scripts must be approved by stakeholders before scheduling rehearsals.");
            return;
        }

        // display and select script
        System.out.println("\nYour Approved Scripts:");
        for (int i = 0; i < approvedScripts.size(); i++) {
            Script s = approvedScripts.get(i);
            Event event = DataCache.getById(s.getEventId(), Event::new);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() +
                    ", Event: " + (event != null ? event.getLocationName() : "Unknown") +
                    ", Actions: " + s.getActionIds().size() +
                    ", Risk: " + String.format("%.2f", s.calculateTotalRisk()));
        }

        System.out.println("\nSelect script (1-" + approvedScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, approvedScripts.size());
        if (choice == 0) { return; }

        Script script = approvedScripts.get(choice - 1);

        // display script details
        displayScriptDetails(script);

        // caclulate recommended duration
        int recommendedDuration = RehearsalValidator.calculateRecommendedDuration(script);
        System.out.println("Recommended rehearsal duration: " + recommendedDuration + " minutes");

        System.out.println("Use recommended duration? (yes/no): ");
        String useRecommended = UserInput.getStringInput().toLowerCase();

        int duration;
        if (useRecommended.equals("yes") || useRecommended.equals("y")) {
            duration = recommendedDuration;
        } else {
            System.out.println("Enter custom duration (30-480 minutes): ");
            duration = UserInput.getIntInput(30, 480);
        }

        // get date and time 
        System.out.println("\nEnter rehearsal date and time:");
        System.out.println("Date (YYYY-MM-DD): ");
        String dateStr = UserInput.getStringInput();
        // validate date format
        if (!dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD (e.g., 2025-12-25)");
            return;
        }

        System.out.println("Time (HH:MM in 24-hour format): ");
        String timeStr = UserInput.getStringInput();

        // validate time format
        if (!timeStr.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            System.out.println("Error: Invalid time format. Please use HH:MM (e.g., 08:00, 14:30)");
            return;
        }

        long scheduledDate;
        try {
            Date date = dateFormat.parse(dateStr + " " + timeStr);
            scheduledDate = date.getTime();

            // check if date is in the past
            if (scheduledDate <= System.currentTimeMillis()) {
                System.out.println("Error: Rehearsal date must be in the future");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid date/time format. Please use YYYY-MM-DD for date and HH:MM");
            return;
        }

        // display venue
        ArrayList<Venue> venues = DataCache.getAll(Venue::new);
        if(venues.isEmpty()) {
            System.out.println("No venues available in the system.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Available Venues:");
        System.out.println("=".repeat(60));
        for (int i = 0; i < venues.size(); i++) {
            Venue v = venues.get(i);
            System.out.println((i + 1) + ". " + v.getName() +
                    " | Location: " + v.getLocation());
        }

        System.out.println("\nSelect venue (1-" + venues.size() + ", 0 to cancel): ");
        int venueChoice = UserInput.getIntInput(0, venues.size());
        if (venueChoice == 0) { return; }

        Venue venue = venues.get(venueChoice - 1);

        // calculate cost
        double cost = RehearsalValidator.calcualteRehearsalCost(venue, duration);

        // summary
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Rehearsal Summary:");
        System.out.println("Script ID: " + script.getScriptId());
        Event event = DataCache.getById(script.getEventId(), Event::new);
        System.out.println("Event: " + (event != null ? event.getLocationName() : "Unknown"));
        System.out.println("Date/Time: " + dateFormat.format(new Date(scheduledDate)));
        System.out.println("Venue: " + venue.getName() + "-" + venue.getLocation());
        System.out.println("Wrestlers: " + script.getRequiredWrestlerIds().size());
        System.out.println("Total Cost: $" + String.format("%.2f", cost));
        System.out.println("=".repeat(60));

        System.out.println("Validating rehearsal request...");
        RehearsalValidator.ValidateResult validation = RehearsalValidator.validateRehearsal(
                script, venue, scheduledDate, duration
        );

        validation.print();

        if (!validation.isValid()) {
            System.out.println("Rehearsal scheduling failed due to validation errors.");
            return;
        }

        System.out.println("Confirm and create rehearsal? (yes/no): ");
        String confirm = UserInput.getStringInput().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Rehearsal scheduling cancelled.");
            return;
        }

        RehearsalSession rehearsal = new RehearsalSession(
                script.getScriptId(),
                script.getEventId(),
                venue.getId(),
                scheduledDate,
                duration,
                script.getRequiredWrestlerIds(),
                cost,
                ""
        );

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Rehearsal scheduled successfully");
        System.out.println("=".repeat(60));

        System.out.println("Rehearsal ID: " + rehearsal.getRehearsalId());
        System.out.println("Estimated Cost: $" + String.format("%.2f", cost));
        System.out.println("Wrestlers will be notified of rehearsal schedule.");
        System.out.println("=".repeat(60));
    }

    public void viewRehearsals() {
        ArrayList<RehearsalSession> allRehearsals = DataCache.getAll(RehearsalSession::new);
        
        if (allRehearsals.isEmpty()) {
            System.out.println("\nNo rehearsals scheduled.");
            return;
        }

        // Filter to show only this choreographer's rehearsals
        ArrayList<Script> myScripts = DataCache.getAll(Script::new);
        Set<Integer> myScriptIds = myScripts.stream()
            .filter(s -> s.getChoreographer().equals(choreographerName))
            .map(Script::getScriptId)
            .collect(Collectors.toSet());

        List<RehearsalSession> myRehearsals = allRehearsals.stream()
            .filter(r -> myScriptIds.contains(r.getScriptId()))
            .collect(Collectors.toList());

        if (myRehearsals.isEmpty()) {
            System.out.println("\nYou have no scheduled rehearsals.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("YOUR SCHEDULED REHEARSALS");
        System.out.println("=".repeat(80));

        for (RehearsalSession r : myRehearsals) {
            Script script = DataCache.getById(r.getScriptId(), Script::new);
            Event event = DataCache.getById(r.getEventId(), Event::new);
            Venue venue = DataCache.getById(r.getVenueId(), Venue::new);

            System.out.println("\nRehearsal ID: " + r.getRehearsalId());
            System.out.println("  Script: " + (script != null ? "ID " + script.getScriptId() : "Unknown"));
            System.out.println("  Event: " + (event != null ? event.getLocationName() : "Unknown"));
            System.out.println("  Venue: " + (venue != null ? venue.getName() : "Unknown"));
            System.out.println("  Date/Time: " + dateFormat.format(new Date(r.getScheduledDate())));
            System.out.println("  Duration: " + r.getDuration() + " minutes");
            System.out.println("  Wrestlers: " + r.getWrestlerIds().size());
            System.out.println("  Cost: $" + String.format("%.2f", r.getCost()));
            System.out.println("  Status: " + r.getStatus());
            if (!r.getNotes().isEmpty()) {
                System.out.println("  Notes: " + r.getNotes());
            }
        }
        System.out.println("=".repeat(80));
    }

    private void displayScriptDetails(Script script) {
        Event event = DataCache.getById(script.getEventId(), Event::new);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCRIPT DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("Script ID: " + script.getScriptId());
        System.out.println("Event: " + (event != null ? event.getLocationName() : "Unknown"));
        System.out.println("Choreographer: " + script.getChoreographer());
        System.out.println("Status: " + script.getStatus());
        System.out.println("Total Actions: " + script.getActionIds().size());
        System.out.println("Risk Score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
        System.out.println("Required Wrestlers: " + script.getRequiredWrestlerIds().size());
        
        ArrayList<ScriptAction> allActions = DataCache.getAll(ScriptAction::new);
        List<ScriptAction> actions = allActions.stream()
            .filter(a -> a.getScriptId() == script.getScriptId())
            .collect(Collectors.toList());
            
        if (!actions.isEmpty()) {
            System.out.println("\nActions:");
            for (ScriptAction action : actions) {
                System.out.println("  - " + action.getActionType() + 
                                 " (Danger: " + action.getDangerRating() + 
                                 ", Duration: " + action.getEstimatedDuration() + " min)");
            }
        }
        System.out.println("=".repeat(60));
    }
}
