import java.util.*;
import java.util.function.Supplier;

class Choreographer implements Actor {
    private static final String SCRIPT_FILE = "scripts.txt";
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
        System.out.println("\nChoreographer Options:");

        while (true) {
            System.out.println("0: Exit");
            System.out.println("1: Submit Script");
            System.out.println("2: View Scripts");
            System.out.print("Enter choice: ");
            int choice = UserInput.getIntInput(0, 2);

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                submitScript();
            } else if (choice == 2) {
                viewScripts();
            }
        }
    }

    private void submitScript() {
        // list events
        List<Event> events = DataCache.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found in the system.");
            return;
        }

        System.out.println("\nAvailable Events:");
        for (Event e : events) {
            System.out.println("ID: " + e.getId() + " | Date: " + e.getDate() + " | Location: " + e.getLocationName());
        }

        System.out.print("Enter Event ID: ");
        int eventId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // check if event exists
        Event event = DataCache.getEventById(eventId);
        if (event == null) {
            System.out.println("Error: Event not found.");
            return;
        }

        // check if script exists
        Database db = new Database();
        List<Script> scripts = db.getAll(SCRIPT_FILE, Script::new);
        for (Script s : scripts) {
            if (s != null && Integer.parseInt(s.getEventId()) == eventId) {
                System.out.println("Error: Script for this event already exists.");
                return;
            }
        }

        System.out.print("Enter script content: ");
        String content = UserInput.getStringInput();

        if (content == null || content.isEmpty()) {
            System.out.println("Error: Script content cannot be empty.");
            return;
        }

        Script script = new Script(String.valueOf(eventId), content, choreographerName);
        db.add(script, SCRIPT_FILE);
        System.out.println("Script submitted successfully.");
    }

    private void viewScripts() {
        System.out.print("Enter event id to view script: ");
        int eventId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        // check if event exists
        Event event = DataCache.getEventById(eventId);
        if (event == null) {
            System.out.println("Error: Event not found.");
            return;
        }

        Database db = new Database();
        List<Script> scripts = db.getAll(SCRIPT_FILE, Script::new);
        Script foundScript = null;

        for (Script s : scripts) {
            if (s != null && Integer.parseInt(s.getEventId()) == eventId) {
                foundScript = s;
                break;
            }
        }

        if (foundScript == null) {
            System.out.println("No script found for the given event ID: " + eventId);
        } else {
            System.out.println("\n--- Script for Event " + eventId + " ---");
            System.out.println("Choreographer: " + foundScript.getChoreographer());
            System.out.println("Content:\n" + foundScript.getContent());
            System.out.println("--------------------------------");            
        }
    }

}