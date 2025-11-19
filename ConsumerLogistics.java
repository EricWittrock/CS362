import java.util.ArrayList;
import java.util.List;

public class ConsumerLogistics implements Actor {
    ConcessionSupplies concessionSupplies = new ConcessionSupplies();

    @Override
    public void showOptions() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Concession Supplies", this.concessionSupplies::showOptions));

        while(true) {
            System.out.println("Consumer Logistics Options:");
            System.out.println("0) Exit");
            for (int i = 0; i < options.size(); i++)
                options.get(i).Display(i + 1);
            int choice = UserInput.getIntInput();

            if (choice == 0) {
                return;
            } else if (choice > 0 && choice < options.size() - 1) {
                options.get(choice - 1).select();
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }
}