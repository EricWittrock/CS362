import java.util.*;
import java.util.stream.Collectors;

public class Stakeholder implements Actor {
    private String name;
    private String role; // Creative Director, Safety Officer, etc

    public Stakeholder() {
        System.out.println("Enter your name:");
        this.name = UserInput.getStringInput();
        System.out.println("Enter your role (e.g., Creative Director, Safety Officer):");
        this.role = UserInput.getStringInput();
    }

    @Override
    public void showOptions() {
        System.out.println("\n=== Stakeholder Menu ===");

        while (true) {
            System.out.println("\n0: Exit");
            System.out.println("1: View Pending Scripts");
            System.out.println("2: View Script Details");
            System.out.println("3: Validate & Approve Script");
            System.out.println("4: Reject Script");
            System.out.println("5: Request Script Revision");

            System.out.print("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 5);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                viewPendingScripts();
            } else if (choice == 2) {
                viewScriptDetails();
            } else if (choice == 3) {
                approveScript();
            } else if (choice == 4) {
                rejectScript();
            } else if (choice == 5) {
                requestRevision();
            }
        }
    }

    private void viewPendingScripts() {
        List<Script> pendingScripts = DataCache.getAll(Script::new).stream()
            .filter(s -> s.getStatus() == ScriptStatus.PROPOSED ||
                         s.getStatus() == ScriptStatus.UNDER_REVIEW)
            .collect(Collectors.toList());
        
        if (pendingScripts.isEmpty()) {
            System.out.println("\nNo pending scripts found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPT AWAITING REVIEW");
        System.out.println("=".repeat(80));
        
        for (Script script : pendingScripts) {
            Event event = DataCache.getById(script.getEventId(), Event::new);

            System.out.println("\nScript ID: " + script.getScriptId());
            System.out.println("  Event: " + (event != null ? event.getLocationName() : "Event ID " + script.getEventId()));
            System.out.println("  Choreographer: " + script.getChoreographer());
            System.out.println("  Status: " + script.getStatus());
            System.out.println("  Actions: " + script.getActionIds().size());
            System.out.println("  Risk Score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
            System.out.println("  Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
        }
        System.out.println("=".repeat(80));
    }

    private void viewScriptDetails() {
        List<Script> allScripts = DataCache.getAll(Script::new);
        if (allScripts.isEmpty()) {
            System.out.println("\nNo scripts found in system.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL SCRIPTS");
        System.out.println("=".repeat(80));
        
        for (int i = 0; i < allScripts.size(); i++) {
            Script s = allScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() + 
                             " | Choreographer: " + s.getChoreographer() +
                             " | Status: " + s.getStatus());
        }
        
        System.out.print("\nSelect script to view (1-" + allScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, allScripts.size());
        
        if (choice == 0) return;

        Script script = allScripts.get(choice - 1);
        displayFullScriptDetails(script);
    }

    private void displayFullScriptDetails(Script script) {
        Event event = DataCache.getById(script.getEventId(), Event::new);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPT DETAILS - ID: " + script.getScriptId());
        System.out.println("=".repeat(80));
        System.out.println("Event: " + (event != null ? event.getLocationName() : "Event ID " + script.getEventId()));
        System.out.println("Choreographer: " + script.getChoreographer());
        System.out.println("Status: " + script.getStatus());
        System.out.println("Proposed Date: " + new Date(script.getProposedDate()));
        
        if (script.getApprovedDate() > 0) {
            System.out.println("Approved Date: " + new Date(script.getApprovedDate()));
            System.out.println("Approved By: " + script.getApprovedBy());
        }
        
        if (script.getRejectionReason() != null) {
            System.out.println("Rejection/Revision Notes: " + script.getRejectionReason());
        }
        
        System.out.println("\nMETRICS:");
        System.out.println("  Risk Score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
        System.out.println("  Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
        System.out.println("  Total Actions: " + script.getActionIds().size());
        System.out.println("  Required Wrestlers: " + script.getRequiredWrestlerIds().size());
        
        System.out.println("\nACTIONS:");
        List<ScriptAction> actions = DataCache.getAllByFilter(
            a -> a.getScriptId() == script.getScriptId(), 
            ScriptAction::new
        );
        for (ScriptAction action : actions) {
            System.out.println("\n  #" + action.getSequenceOrder() + " - " + action.getActionType());
            System.out.println("      Description: " + action.getDescription());
            System.out.println("      Wrestlers: " + getWrestlerNames(action.getWrestlerIds()));
            System.out.println("      Danger: " + action.getDangerRating() + "/10");
            System.out.println("      Duration: " + action.getEstimatedDuration() + " minutes");
        }
        
        System.out.println("\n" + "=".repeat(80));
    }

    private String getWrestlerNames(List<Integer> wrestlerIds) {
        List<String> names = new ArrayList<>();
        for (Integer id : wrestlerIds) {
            Wrestler wrestler = DataCache.getById(id.intValue(), Wrestler::new);
            if (wrestler != null) {
                names.add(wrestler.getName());
            } else {
                names.add("ID " + id);
            }
        }
        return String.join(", ", names);
    }

    private void approveScript() {
        List<Script> approvableScripts = DataCache.getAll(Script::new).stream()
            .filter(s -> s.getStatus() == ScriptStatus.PROPOSED ||
                        s.getStatus() == ScriptStatus.UNDER_REVIEW)
            .collect(Collectors.toList());
        
        if (approvableScripts.isEmpty()) {
            System.out.println("\nNo scripts available for approval.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPTS AVAILABLE FOR APPROVAL");
        System.out.println("=".repeat(80));
        
        for (int i = 0; i < approvableScripts.size(); i++) {
            Script s = approvableScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() +
                             " | Choreographer: " + s.getChoreographer() +
                             " | Actions: " + s.getActionIds().size() +
                             " | Risk: " + String.format("%.2f", s.calculateTotalRisk()));
        }
        
        System.out.print("\nSelect script to approve (1-" + approvableScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, approvableScripts.size());
        
        if (choice == 0) return;
        
        Script script = approvableScripts.get(choice - 1);

        if (script.getStatus() == ScriptStatus.APPROVED) {
            System.out.println("Script is already approved.");
            return;
        }

        if (script.getStatus() == ScriptStatus.REJECTED) {
            System.out.println("Cannot approve a rejected script.");
            return;
        }

        // script summary
        System.out.println("\n--- Script Summary ---");
        System.out.println("Script ID: " + script.getScriptId());
        System.out.println("Choreographer: " + script.getChoreographer());
        System.out.println("Actions: " + script.getActionIds().size());
        System.out.println("Risk Score: " + String.format("%.2f", script.calculateTotalRisk()));
        System.out.println("Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));

        // validate script
        System.out.println("\n--- Running Validation ---");
        ScriptValidator.ValidateResult validation = ScriptValidator.validateScript(script);
        validation.print();

        if(!validation.isValid()) {
            System.out.println("\nScript failed validation. Cannot approve.");
            System.out.println("\nWould you like to request revision instead? (yes/no): ");
            String response = UserInput.getStringInput();
            if (response.equalsIgnoreCase("yes")) {
                requestRevisionWithError(script, validation.getErrors());
            }

            return;
        }

        // approve script
        script.setStatus(ScriptStatus.APPROVED);
        script.setApprovedBy(this.name + " (" + this.role + ")");
        script.setApprovedDate(System.currentTimeMillis());
        DataCache.addObject(script);

        System.out.println("Script " + script.getScriptId() + " has been APPROVED.");
        System.out.println("   Approved by: " + this.name);
        System.out.println("   Insurance cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
        System.out.println("   Choreographer '" + script.getChoreographer() + "' will be notified.");
    }

    private void rejectScript() {
        List<Script> rejectableScripts = DataCache.getAll(Script::new).stream()
            .filter(s -> s.getStatus() != ScriptStatus.APPROVED &&
                        s.getStatus() != ScriptStatus.REJECTED)
            .collect(Collectors.toList());
        
        if (rejectableScripts.isEmpty()) {
            System.out.println("\nNo scripts available to reject.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPTS AVAILABLE TO REJECT");
        System.out.println("=".repeat(80));
        
        for (int i = 0; i < rejectableScripts.size(); i++) {
            Script s = rejectableScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() +
                             " | Choreographer: " + s.getChoreographer() +
                             " | Status: " + s.getStatus());
        }
        
        System.out.print("\nSelect script to reject (1-" + rejectableScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, rejectableScripts.size());
        
        if (choice == 0) return;
        
        Script script = rejectableScripts.get(choice - 1);

        if (script.getStatus() == ScriptStatus.APPROVED) {
            System.out.println("Error: Cannot reject an approved script.");
            return;
        }

        System.out.println("\n Enter reason for rejection: ");
        String reason = UserInput.getStringInput();

        script.setStatus(ScriptStatus.REJECTED);
        script.setRejectionReason(reason);

        System.out.println("Script " + script.getScriptId() + " has been REJECTED.");
        System.out.println("   Reason: " + reason);
        System.out.println("   Choreographer '" + script.getChoreographer() + "' will be notified.");
        DataCache.addObject(script);
    }

    private void requestRevision() {
        List<Script> revisionableScripts = DataCache.getAll(Script::new).stream()
            .filter(s -> s.getStatus() == ScriptStatus.PROPOSED ||
                        s.getStatus() == ScriptStatus.UNDER_REVIEW)
            .collect(Collectors.toList());
        
        if (revisionableScripts.isEmpty()) {
            System.out.println("\nNo scripts available for revision request.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPTS AVAILABLE FOR REVISION REQUEST");
        System.out.println("=".repeat(80));
        
        for (int i = 0; i < revisionableScripts.size(); i++) {
            Script s = revisionableScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() +
                             " | Choreographer: " + s.getChoreographer() +
                             " | Status: " + s.getStatus());
        }
        
        System.out.print("\nSelect script for revision (1-" + revisionableScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, revisionableScripts.size());
        
        if (choice == 0) return;
        
        Script script = revisionableScripts.get(choice - 1);

        System.out.println("\n Enter revision notes/feedback: ");
        String notes = UserInput.getStringInput();

        script.setStatus(ScriptStatus.REQUIRES_REVISION);
        script.setRejectionReason(notes);

        System.out.println("\nRevision requested for Script " + script.getScriptId());
        System.out.println("  Choreographer '" + script.getChoreographer() + "' will receive these notes:");
        System.out.println("  \"" + notes + "\"");
        DataCache.addObject(script);
    }

    private void requestRevisionWithError(Script script, List<String> errors) {
        String notes = "Validation failed. Please address:\n" + String.join("\n", errors);
        script.setStatus(ScriptStatus.REQUIRES_REVISION);
        script.setRejectionReason(notes);
        DataCache.addObject(script);

        System.out.println("\nRevision requested for Script " + script.getScriptId());    
    }
}
