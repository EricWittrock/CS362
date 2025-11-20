import java.util.ArrayList;
import java.util.List;

public class OptionList {
    List<Option> options;
    String exitOption = null; 

    public OptionList() {
        options = new ArrayList<>();
    }

    public void add(String name, OptionSelect callback) {
        this.options.add(new Option(name, callback));
    }

    public void addExitOption(String name) {
        exitOption = name;
    }

    // Exits after a user selects an option
    public void singleDisplayAndSelect(String prompt) {
        int choice;
        while(true) {
            if(exitOption != null) {
                System.out.println("0) " + exitOption);
            }
            for (int i = 0; i < options.size(); i++) {
                options.get(i).Display(i + 1);
            }
            System.out.println(prompt);
            choice = UserInput.getIntInput(0, options.size());
            boolean inBounds = choice > 0 && choice <= options.size();
            if (choice == 0) {
                return;
            } else if (inBounds) {
                if(exitOption == null) {
                    break;
                }
                options.get(choice - 1).select();
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
        if(exitOption == null) {
            options.get(choice - 1).select();
        }
    }

    // Loops until user selects exit option
    public void loopDisplayAndSelect(String prompt) {
        int choice;
        if(exitOption == null) {
            System.out.println("Exit option not set. Please set an exit option before calling loopDisplayAndSelect.");
            return;
        }
        while (true) {
            System.out.println("0) " + exitOption);
            for (int i = 0; i < options.size(); i++) {
                options.get(i).Display(i + 1);
            }
            System.out.println(prompt);
            choice = UserInput.getIntInput(0, options.size());
            if (choice == 0) {
                return;
            } else if (choice > 0 && choice <= options.size()) {
                options.get(choice - 1).select();
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }
}