import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ConcessionSupplies implements Actor {
    Venue venue;

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Back");
        options.add("Order Concessions", this::orderSupplies);
        options.add("View Concessions by Venue", this::viewSupplies);
        options.add("View Concession Orders by Buyer", this::viewOrdersByBuyer);
        options.add("View Concession Orders by Supplier", this::viewOrdersBySupplier);

        int choice = 0;
        options.loopDisplayAndSelect("Concession Supplies Menu");
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
        
        Concession concession = new Concession(name, quantity, unitPrice, expirationDate, venue);
        ConcessionOrder order = new ConcessionOrder(concession, orderDate, orderer, supplier);
    }

    public void viewSupplies() {
        selectVenue("Select venue to view concessions:");
        List<Concession> concessions = DataCache.getAll(Concession::new).stream()
            .filter(c -> c.getVenueId() == venue.getId())
            .collect(Collectors.toList());
        System.out.println(venue.getName() + " at " + venue.getLocation() + " Concessions:");
        for (Concession c : concessions) {
            c.println();
        }
    }

    public void viewOrdersByBuyer() {
        System.out.println("Enter name on orders to view: ");
        String name = UserInput.getStringInput();
        List<ConcessionOrder> orders = DataCache.getAll(ConcessionOrder::new).stream()
            .filter(co -> co.getBuyer().equals(name))
            .collect(Collectors.toList());
                
        System.out.println("Concession Orders for " + name + ":");
        for (ConcessionOrder o : orders) {
            o.print();
        }
    }

    public void viewOrdersBySupplier() {
        System.out.println("Enter supplier name: ");
        String supplier = UserInput.getStringInput();
        List<ConcessionOrder> orders = DataCache.getAllByFilter(
            co -> co.getSupplier().equals(supplier),
            ConcessionOrder::new
        );
        System.out.println("Concession Orders from " + supplier + ":");
        for (ConcessionOrder o : orders) {
            o.print();
        }
    }

    private void selectVenue(String prompt) {
        List<Venue> venues = DataCache.getAll(Venue::new);
        OptionList options = new OptionList();
        for (Venue v : venues) {
            options.add(
                v.getName() + " at " + v.getLocation(),
                () -> {setVenue(v);}
            );
        }
        int choice = 0;
        options.singleDisplayAndSelect("Select Venue");
    }

    private void setVenue(Venue v) {
        this.venue = v;
    }
}