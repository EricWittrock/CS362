import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MerchandiseOrder {
    private static List<Merchandise> order = new ArrayList<>();
    private static List<Merchandise> items = DataCache.getAllMerchandise();

    private static void orderMerchandise() {
        System.out.println("\nAvailable Merchandise:");
        System.out.println("0) Back");
        List<Option> options = new ArrayList<>();

        for (Merchandise item : items) {
            options.add(new Option(item.getName() +
                    "\n Price per Unit: " + item.getPrice(),
                    () -> {
                        chooseQuantity(item);
                    }));
        }

        options.add(new Option("View Order", () -> {
            viewOrder();
        }));

        while (true) {
            for (int i = 1; i < options.size() - 1; i++)
                options.get(i).Display(i);

            System.out.println("Select merchandise to order: ");
            int choice = UserInput.getIntInput();
            if (choice == 0)
                return;
            options.get(choice).select();
        }
    }

    private static void chooseQuantity(Merchandise item) {
        while (true) {
            System.out.println("Enter -1 to go back to merchandise catalogue.");
            System.out.println("Enter quantity: ");
            int quantity = UserInput.getIntInput();
            if (quantity == -1)
                return;

            if (quantity != 0)
            {
                order.add(item);
                item.setQuantity(quantity);
                int total = item.getPrice() * quantity;
                System.out.println("Price of " + quantity + " " + item.getName() + ": " + total);
            }

            if (quantity == 0)
            {
                order.remove(item);
                item.setQuantity(0);
            }
        }
    }

    private static void viewOrder() {
        System.out.println("Current Order:");

        if (order.isEmpty())
        {
            System.err.println("No order in progress");
            return;
        }

        for (Merchandise item : order) {
            System.out.println(item.getName() + " Quantity: " + item.getQuantity());
        }
        while (true) {
            System.out.println("Enter 0 to go back: ");
            int choice = UserInput.getIntInput();
            if (choice != 0) {
                System.out.println("Invalid choice.");
            } else {
                return;
            }
        }
    }
}