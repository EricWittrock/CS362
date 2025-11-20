import java.util.*;
import java.util.stream.Collectors;

class Choreographer implements Actor {
    private String choreographerName;

    public Choreographer() {
        System.out.println("Enter your name: ");
        this.choreographerName = UserInput.getStringInput();
    }

    public Choreographer(String name) {
        this.choreographerName = name;
    }

    @Override
    public void showOptions() {
        System.out.println("\n=== Choreographer Menu ===");

        while (true) {
            System.out.println("\n0: Exit");
            System.out.println("1: Create New Script");
            System.out.println("2: View My Scripts");
            System.out.println("3: Edit Script");
            System.out.println("4: Submit Script for Review");
            System.out.println("5: View Script Details");
            System.out.print("Enter choice: ");
            int choice = UserInput.getIntInput(0, 5);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                createNewScript();
            } else if (choice == 2) {
                viewMyScripts();
            } else if (choice == 3) {
                editScript();
            } else if (choice == 4) {
                submitScriptForReview();
            } else if (choice == 5) {
                viewScriptDetails();
            }
        }
    }

    private void createNewScript() {
        System.out.println("\n=== Create New Script ===");

        // available events
        ArrayList<Event> events = DataCache.getAll(Event::new);
        if (events.isEmpty()) {
            System.out.println("No events available to create a script for.");
            return;
        }

        System.out.println("\nAvailable Events:");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            Venue venue = e.getVenue();
            String venueName = (venue != null) ? venue.getName() : "Unknown Venue";
            System.out.println((i + 1) + ". Date: " + e.getDate() + " | Location: " + venueName);
        }

        System.out.print("\nSelect event (1-" + events.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, events.size());
        
        if (choice == 0) return;
        
        Event event = events.get(choice - 1);
        int eventId = event.getId();

        // check if script already exists for event
        ArrayList<Script> existingScripts = DataCache.getAll(Script::new);
        for (Script s : existingScripts) {
            if (s.getEventId() == eventId &&
                s.getStatus() == ScriptStatus.APPROVED){
                System.out.println("Error: A script for this event already exists.");
                return;
            }
        }

        Script script = new Script(eventId, choreographerName);

        System.out.println("\n Script created successfully!");
        System.out.println("    Script ID: " + script.getScriptId());
        System.out.println("    Status: " + script.getStatus());
        System.out.println("\n Next steps:");
        System.out.println("   1. Use 'Edit Script' (option 3)to add actions to your script.");
        System.out.println("   2. Use 'Submit Script for Review' (option 4) when ready.");
    }

    private void viewMyScripts() {
        List<Script> myScripts = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getChoreographer().equals(choreographerName))
                .collect(Collectors.toList());

        if (myScripts.isEmpty()) {
            System.out.println("\nYou have no scripts.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n=== My Scripts ===");
        System.out.println("\n" + "=".repeat(80));

        for (Script script : myScripts) {
            Event event = DataCache.getById(script.getEventId(), Event::new);

            String eventName = (event != null) ? event.getLocationName() : "Event ID " + script.getEventId();

            System.out.println("\nScript ID: " + script.getScriptId());
            System.out.println("  Event: " + eventName);
            System.out.println("  Status: " + script.getStatus());
            System.out.println("  Actions: " + script.getActionIds().size());

            if (script.getActionIds().size() > 0) {
                System.out.println("  Risk Score: " + String.format("%.2f", script.calculateTotalRisk()));
                System.out.println("  Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
            }

            if (script.getRejectionReason() != null) {
                System.out.println("  Feedback: " + script.getRejectionReason());
            }
        }
        System.out.println("\n" + "=".repeat(80));
    }

    private void editScript() {
        // show only scripts that can be edited
        List<Script> myScripts = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getChoreographer().equals(choreographerName))
                .filter(s -> s.getStatus() == ScriptStatus.PROPOSED || s.getStatus() == ScriptStatus.REQUIRES_REVISION)
                .collect(Collectors.toList());

        if (myScripts.isEmpty()) {
            System.out.println("\nYou have no scripts available for editing.");
            System.out.println("\n  (Only scripts with status PROPOSED or REQUIRES_REVISION can be edited.)");
            return;
        }

        System.out.println("\n=== Your Edit Scripts ===");
        for (int i = 0; i < myScripts.size(); i++) {
            Script s = myScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() + 
                                " | Status: " + s.getStatus() + 
                                " | Actions: " + s.getActionIds().size());
        }

        System.out.println("\nSelect script to edit (1-" + myScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, myScripts.size());

        if (choice == 0) { return; }

        Script script = myScripts.get(choice - 1);
        editScriptMenu(script);
    }

    private void editScriptMenu(Script script) {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("EDITING SCRIPT: " + script.getScriptId());
            System.out.println("=".repeat(60));
            System.out.println("Status: " + script.getStatus());
            System.out.println("Actions: " + script.getActionIds().size());
            
            if (script.getActionIds().size() > 0) {
                System.out.println("Risk Score: " + String.format("%.2f", script.calculateTotalRisk()));
                System.out.println("Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
            }

            if (script.getRejectionReason() != null) {
                System.out.println("Revision Notes: " + script.getRejectionReason());
            }

            System.out.println("\n1. View Actions");
            System.out.println("2. Add Action");
            System.out.println("3. Edit Action");
            System.out.println("4. Remove Action");
            System.out.println("5. Reorder Actions");
            System.out.println("0. Return to Main Menu");

            System.out.println("\nEnter choice: ");
            int choice = UserInput.getIntInput(0, 5);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                viewScriptActions(script);
            } else if (choice == 2) {
                addActionToScript(script);
            } else if (choice == 3) {
                editAction(script);
            } else if (choice == 4) {
                removeActionFromScript(script);
            } else if (choice == 5) {
                reorderActions(script);
            }
        }
    }

    private void viewScriptActions(Script script) {
        List<ScriptAction> actions = DataCache.getAll(ScriptAction::new).stream()
            .filter(a -> a.getScriptId() == script.getScriptId())
            .collect(Collectors.toList());

        if (actions.isEmpty()) {
            System.out.println("\nNo actions in this script.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPT ACTIONS");
        System.out.println("=".repeat(80));

        for (ScriptAction action : actions) {
            System.out.println("\n#" + action.getSequenceOrder() + " - " + action.getActionType());
            System.out.println("    Action ID: " + action.getActionId());
            System.out.println("    Description: " + action.getDescription());
            System.out.println("    Wrestlers: " + getWrestlerNames(action.getWrestlerIds()));
            System.out.println("    Danger Rating: " + action.getDangerRating());
            System.out.println("    Duration: " + action.getEstimatedDuration() + " mins");
            System.out.println("    Insurance Multiplier: " + action.getInsuranceMultiplier() + "x");
        }
        System.out.println("\n" + "=".repeat(80));
    }

    private void addActionToScript(Script script) {
        System.out.println("\n=== Add New Action ===");

        // show action types
        System.out.println("\nAction Types:");
        ActionType[] types = ActionType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + "." + types[i]);
        }

        System.out.println("\nSelect type (1-" + types.length + "):" );
        int typeChoice = UserInput.getIntInput(1, types.length);
        ActionType type = types[typeChoice - 1];

        System.out.println("\nDescription: ");
        String description = UserInput.getStringInput();

        System.out.println("Danger Rating (1-10): ");
        int danger = UserInput.getIntInput(1, 10);

        System.out.println("Duration (minutes): ");
        int duration = UserInput.getIntInput(1, 180);

        // get wrestlers
        List<Wrestler> allWrestlers = DataCache.getAll(Wrestler::new);
        if (allWrestlers.isEmpty()) {
            System.out.println("\nError: No wrestlers found in system. Ask Manager to add wrestlers first.");
            return;
        }
        
        System.out.println("\nNumber of wrestlers in this action: ");
        int numWrestlers = UserInput.getIntInput(1, Math.min(10, allWrestlers.size()));

        List<Integer> wrestlerIds = new ArrayList<>();
        for (int i = 0; i < numWrestlers; i++) {
            System.out.println("\nAvailable Wrestlers:");
            for (int j = 0; j < allWrestlers.size(); j++) {
                Wrestler w = allWrestlers.get(j);
                System.out.println((j + 1) + ". " + w.getName() + " (" + w.getSpecialty() + ")");
            }
            
            System.out.print("Select wrestler " + (i + 1) + " (1-" + allWrestlers.size() + "): ");
            int wrestlerChoice = UserInput.getIntInput(1, allWrestlers.size());
            wrestlerIds.add(allWrestlers.get(wrestlerChoice - 1).getId());
        }

        int sequenceOrder = script.getActionIds().size() + 1;

        // create action
        ScriptAction action = new ScriptAction (
            script.getScriptId(),
            type,
            description,
            wrestlerIds,
            danger,
            duration,
            sequenceOrder
        );

        script.addActionId(action.getActionId());
        DataCache.addObject(script);

        System.out.println("\nAction added successfully!");
        System.out.println(" New total actions: " + script.getActionIds().size());
        System.out.println(" New risk score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
        System.out.println(" New insurance cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
    }

    private void editAction(Script script) {
        List<ScriptAction> actions = DataCache.getAll(ScriptAction::new).stream()
            .filter(a -> a.getScriptId() == script.getScriptId())
            .collect(Collectors.toList());
        
        if (actions.isEmpty()) {
            System.out.println("\nNo actions to edit.");
            return;
        }

        System.out.println("\n=== Edit Action ===");
        for (int i = 0; i < actions.size(); i++) {
            ScriptAction a = actions.get(i);
            System.out.println((i + 1) + ". #" + a.getSequenceOrder() + " - " + 
                             a.getActionType() + ": " + a.getDescription() +
                             " (Danger: " + a.getDangerRating() + "/10)");
        }

        System.out.print("\nSelect action to edit (1-" + actions.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, actions.size());
        
        if (choice == 0) return;

        ScriptAction action = actions.get(choice - 1);
        
        System.out.println("\n--- Current Action Details ---");
        System.out.println("Type: " + action.getActionType());
        System.out.println("Description: " + action.getDescription());
        System.out.println("Wrestlers: " + getWrestlerNames(action.getWrestlerIds()));
        System.out.println("Danger: " + action.getDangerRating() + "/10");
        System.out.println("Duration: " + action.getEstimatedDuration() + " minutes");
        
        System.out.println("\n--- What would you like to edit? ---");
        System.out.println("1. Action Type");
        System.out.println("2. Description");
        System.out.println("3. Wrestlers");
        System.out.println("4. Danger Rating");
        System.out.println("5. Duration");
        System.out.println("0. Cancel");
        
        System.out.print("\nEnter choice: ");
        int editChoice = UserInput.getIntInput(0, 5);
        
        if (editChoice == 0) return;
        
        switch (editChoice) {
            case 1: // Edit Action Type
                System.out.println("\nAction Types:");
                ActionType[] types = ActionType.values();
                for (int i = 0; i < types.length; i++) {
                    System.out.println((i + 1) + ". " + types[i]);
                }
                System.out.print("\nSelect new type (1-" + types.length + "): ");
                int typeChoice = UserInput.getIntInput(1, types.length);
                action.setActionType(types[typeChoice - 1]);
                System.out.println(" Action type updated to: " + types[typeChoice - 1]);
                DataCache.addObject(action);
                break;
                
            case 2: // Edit Description
                System.out.print("\nEnter new description: ");
                String newDesc = UserInput.getStringInput();
                action.setDescription(newDesc);
                System.out.println("Description updated");
                DataCache.addObject(action);
                break;
                
            case 3: // Edit Wrestlers
                List<Wrestler> allWrestlers = DataCache.getAll(Wrestler::new);
                if (allWrestlers.isEmpty()) {
                    System.out.println("\nError: No wrestlers found in system.");
                    return;
                }
                
                System.out.print("\nNumber of wrestlers for this action: ");
                int numWrestlers = UserInput.getIntInput(1, Math.min(10, allWrestlers.size()));
                
                List<Integer> newWrestlerIds = new ArrayList<>();
                for (int i = 0; i < numWrestlers; i++) {
                    System.out.println("\nAvailable Wrestlers:");
                    for (int j = 0; j < allWrestlers.size(); j++) {
                        Wrestler w = allWrestlers.get(j);
                        System.out.println((j + 1) + ". " + w.getName() + " (" + w.getSpecialty() + ")");
                    }
                    
                    System.out.print("Select wrestler " + (i + 1) + " (1-" + allWrestlers.size() + "): ");
                    int wrestlerChoice = UserInput.getIntInput(1, allWrestlers.size());
                    newWrestlerIds.add(allWrestlers.get(wrestlerChoice - 1).getId());
                }
                action.setWrestlerIds(newWrestlerIds);
                System.out.println("Wrestlers updated");
                DataCache.addObject(action);
                break;
                
            case 4: // Edit Danger Rating
                System.out.print("\nEnter new danger rating (1-10): ");
                int newDanger = UserInput.getIntInput(1, 10);
                action.setDangerRating(newDanger);
                System.out.println("Danger rating updated to: " + newDanger);
                DataCache.addObject(action);
                break;
                
            case 5: // Edit Duration
                System.out.print("\nEnter new duration (minutes): ");
                int newDuration = UserInput.getIntInput(1, 180);
                action.setDuration(newDuration);
                System.out.println("Duration updated to: " + newDuration + " minutes");
                DataCache.addObject(action);
                break;
        }
        
        System.out.println("\nAction updated successfully!");
        System.out.println("  New risk score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
        System.out.println("  New insurance cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
    }

    private void removeActionFromScript(Script script) {
        List<ScriptAction> actions = DataCache.getAll(ScriptAction::new).stream()
            .filter(a -> a.getScriptId() == script.getScriptId())
            .collect(Collectors.toList());

        if (actions.isEmpty()) {
            System.out.println("\nNo actions to remove.");
            return;
        }

        System.out.println("\n=== Remove Action ===");
        for (int i = 0; i < actions.size(); i++) {
            ScriptAction a = actions.get(i);
            System.out.println((i + 1) + ". #" + a.getSequenceOrder() + " - " + a.getActionType() + ": " + a.getDescription());
        }

        System.out.println("\nSelect action to remove (1-" + actions.size() + ", 0 to cancel):");
        int choice = UserInput.getIntInput(0, actions.size());

        if (choice == 0) { return; }

        ScriptAction toRemove = actions.get(choice - 1);
        script.removeActionId(toRemove.getActionId());
        DataCache.addObject(script);

        // reorder remaining actions
        List<ScriptAction> remaining = DataCache.getAll(ScriptAction::new).stream()
            .filter(a -> a.getScriptId() == script.getScriptId())
            .collect(Collectors.toList());
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setSequenceOrder(i + 1);
            DataCache.addObject(remaining.get(i));
        }

        System.out.println("\nAction removed successfully!");
        System.out.println(" Remaining actions: " + script.getActionIds().size());
        System.out.println(" New risk score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
        System.out.println(" New insurance cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
    }

    private void reorderActions(Script script) {
        List<ScriptAction> actions = DataCache.getAll(ScriptAction::new).stream()
            .filter(a -> a.getScriptId() == script.getScriptId())
            .collect(Collectors.toList());

        if (actions.size() < 2) {
            System.out.println("\n Need at least 2 actions to reorder.");
            return;
        }

        System.out.println("\n=== Reorder Actions ===");
        System.out.println("Current Order:");
        for (ScriptAction a : actions) {
            System.out.println(" #" + a.getSequenceOrder() + " - " + a.getDescription());
        }

        System.out.println("\nEnter action number to move: ");
        int fromPos = UserInput.getIntInput(1, actions.size());

        System.out.println("Move to position: ");
        int toPos = UserInput.getIntInput(1, actions.size());

        if (fromPos == toPos) {
            System.out.println("\nNo change needed.");
            return;
        }

        ScriptAction moving = actions.get(fromPos - 1);
        actions.remove(fromPos - 1);
        actions.add(toPos - 1, moving);

        // update sequence orders
        for (int i = 0; i < actions.size(); i++) {
            actions.get(i).setSequenceOrder(i + 1);
            DataCache.addObject(actions.get(i));
        }

        System.out.println("\nActions reordered successfully!");
        System.out.println(" New order:");
        for (ScriptAction a : actions) {
            System.out.println(" #" + a.getSequenceOrder() + " - " + a.getDescription());
        }
    }

    private void submitScriptForReview() {
        List<Script> myScripts = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getChoreographer().equals(choreographerName))
                .filter(s -> s.getStatus() == ScriptStatus.PROPOSED || s.getStatus() == ScriptStatus.REQUIRES_REVISION)
                .collect(Collectors.toList());
        
        if (myScripts.isEmpty()) {
            System.out.println("\nNo scripts ready for submission.");
            return;
        }

        System.out.println("\n=== Submit Script for Review ===");
        for (int i = 0; i < myScripts.size(); i++) {
            Script s = myScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() + 
                                " | Status: " + s.getStatus() + 
                                " | Actions: " + s.getActionIds().size());
        }

        System.out.println("\nSelect script to submit (1-" + myScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, myScripts.size());

        if (choice == 0) { return; }

        Script script = myScripts.get(choice - 1);

        if (script.getActionIds().isEmpty()) {
            System.out.println("\nError: Cannot submit script with no actions.");
            System.out.println(" Add actions first using the 'Edit Script' option.");
            return;
        }

        // change status
        script.setStatus(ScriptStatus.UNDER_REVIEW);
        script.setRejectionReason(null); // clear old feedback
        DataCache.addObject(script);

        System.out.println("\n Script submitted for review successfully!");
        System.out.println("  Script ID: " + script.getScriptId());
        System.out.println("  Total Actions: " + script.getActionIds().size());
        System.out.println("  Risk Score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
        System.out.println("  Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
        System.out.println("\n  A stakeholder will review your script shortly.");
    }

    private void viewScriptDetails() {
        List<Script> myScripts = DataCache.getAll(Script::new).stream()
                .filter(s -> s.getChoreographer().equals(choreographerName))
                .collect(Collectors.toList());

        if (myScripts.isEmpty()) {
            System.out.println("\nYou have no scripts.");
            return;
        }

        System.out.println("\n=== Your Scripts ===");
        for (int i = 0; i < myScripts.size(); i++) {
            Script s = myScripts.get(i);
            System.out.println((i + 1) + ". Script ID: " + s.getScriptId() + 
                             " | Status: " + s.getStatus() +
                             " | Actions: " + s.getActionIds().size());
        }

        System.out.print("\nSelect script to view (1-" + myScripts.size() + ", 0 to cancel): ");
        int choice = UserInput.getIntInput(0, myScripts.size());
        
        if (choice == 0) return;

        Script script = myScripts.get(choice - 1);

        displayScriptSummary(script);
        viewScriptActions(script);
    }

    private void displayScriptSummary(Script script) {
        Event event = DataCache.getById(script.getEventId(), Event::new);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCRIPT SUMMARY - ID: " + script.getScriptId());
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Event: " + (event != null ? event.getLocationName() : "Event ID " + script.getEventId()));
        System.out.println("Choreographer: " + script.getChoreographer());
        System.out.println("Status: " + script.getStatus());
        System.out.println("Proposed: " + new Date(script.getProposedDate()));

        if (script.getApprovedDate() > 0) {
            System.out.println("Approved: " + new Date(script.getApprovedDate()));
            System.out.println("ApprovedBy: " + script.getApprovedBy());
        }

        if (script.getRejectionReason() != null) {
            System.out.println("\nFeedback: ");
            System.out.println("   " + script.getRejectionReason());
        }

        if (script.getActionIds().size() > 0) {
            System.out.println("\nMetrics:");
            System.out.println("  Total Actions: " + script.getActionIds().size());
            System.out.println("  Risk Score: " + String.format("%.2f", script.calculateTotalRisk()) + "/10");
            System.out.println("  Insurance Cost: $" + String.format("%.2f", script.calculateInsuranceCost()));
        }
        System.out.println("\n" + "=".repeat(80));
    }

    private String getWrestlerNames(List<Integer> wrestlerIds) {
        List<String> names = new ArrayList<>();
        for (int id : wrestlerIds) {
            Wrestler w = DataCache.getById(id, Wrestler::new);
            if (w != null) {
                names.add(w.getName());
            } else {
                names.add("Wrestler ID " + id);
            }
        }
        return String.join(", ", names);
    }
}
