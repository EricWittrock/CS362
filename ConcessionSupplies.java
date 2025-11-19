import java.util.List;
import java.util.ArrayList;

public class ConcessionSupplies implements Actor {
    Venue venue;

    @Override
    public void showOptions() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Order Concessions", this::orderSupplies));
        options.add(new Option("View Concessions by Venue", this::viewSupplies));
        options.add(new Option("View Concession Orders by Name", this::viewOrdersByName));
        options.add(new Option("View Concession Orders by Supplier", this::viewOrdersBySupplier));

        int choice = 0;
        while (true) { 
            System.out.println("Concession Supplies Menu:");
            System.out.println("0) Back");
            for (int i = 0; i < options.size(); i++)
                options.get(i).Display(i + 1);
            choice = UserInput.getIntInput();
            if (choice == 0) return;
            if (choice < 1 || choice > options.size()) {
                System.out.println("Invalid option.");
                continue;
            }
            options.get(choice - 1).select();
        }
    }

    public void orderSupplies() {
        System.out.println("Ordering concessions...\n");
        System.out.println("Enter name on order: ");
        String orderer = UserInput.getStringInput();
        System.out.println("Enter supplier on order: ");
        String supplier = UserInput.getStringInput();
        System.out.println("Enter Concession name: ");
        String name = UserInput.getStringInput();
        System.out.println("Enter quantity: ");
        int quantity = UserInput.getIntInput();
        System.out.println("Enter unit price: ");
        int unitPrice = UserInput.getIntInput();
        System.out.println("Enter date ordered (YYYY-MM-DD): ");
        String orderDate = UserInput.getStringInput();
        System.out.println("Enter expiration date (YYYY-MM-DD): ");
        String expirationDate = UserInput.getStringInput();

        selectVenue("Select venue for concession order:");
        
        Concession concession = new Concession(name, quantity, expirationDate, venue);
        ConcessionOrder order = new ConcessionOrder(concession, unitPrice, orderDate, orderer, supplier);
    }

    public void viewSupplies() {
        selectVenue("Select venue to view concessions:");
        List<Concession> concessions = DataCache.getConcessionByVenue(venue);
        System.out.println(venue.getName() + " at " + venue.getLocation() + " Concessions:");
        for (Concession c : concessions) {
            c.println();
        }
    }

    public void viewOrdersByName() {
        System.out.println("Enter name on orders to view: ");
        String name = UserInput.getStringInput();
        List<ConcessionOrder> orders = DataCache.getConcessionOrderByName(name);
        System.out.println("Concession Orders for " + name + ":");
        for (ConcessionOrder o : orders) {
            o.print();
        }
    }

    public void viewOrdersBySupplier() {
        System.out.println("Enter supplier name: ");
        String supplier = UserInput.getStringInput();
        List<ConcessionOrder> orders = DataCache.getConcessionOrderBySupplier(supplier);
        System.out.println("Concession Orders from " + supplier + ":");
        for (ConcessionOrder o : orders) {
            o.print();
        }
    }

    private void selectVenue(String prompt) {
        List<Venue> venues = DataCache.getAllVenues();
        List<Option> options = new ArrayList<>();
        for (Venue v : venues) {
            options.add(new Option(
                venue.getName() + " at " + venue.getLocation(),
                () -> {setVenue(v);}
            ));
        }
        int choice = 0;
        while (true) { 
            System.out.println(prompt);
            for (int i = 0; i < options.size(); i++)
                options.get(i).Display(i + 1);
            System.out.println("Enter option number: ");
            choice = UserInput.getIntInput();
            if (choice == 0) return;
            if (choice < 1 || choice > venues.size()) {
                System.out.println("Invalid option.");
                continue;
            }
            break;
        }
        options.get(choice - 1).select();
    }

    private void setVenue(Venue v) {
        this.venue = v;
    }
}