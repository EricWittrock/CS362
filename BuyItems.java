import java.util.List;

public class BuyItems {
    public static void buyItem() {
        List<Event> events = DataCache.getAll(Event::new);

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        OptionList options = new OptionList();
        options.exitOption = "Exit";

        for (Event e : events) {
            options.add("Venue: " + e.getVenue().getName().toUpperCase(), () -> {
                chooseVenue(e.getVenue());
            });
        }
        options.singleDisplayAndSelect("Choose a Venue to Purchase From");
    }

    private static void chooseVenue(Venue v) {
        OptionList options = new OptionList();
        options.exitOption = "Back";
        List<Concession> concessions = DataCache.getAllByFilter(o -> o.getVenueId() == v.getId(), Concession::new);
        List<Merchandise> merchandises = DataCache.getAllByFilter(f -> f.getVenueId() == v.getId(), Merchandise::new);

        if (concessions.isEmpty() && merchandises.isEmpty()) {
            System.out.println("No products available at the moment!");
            System.out.println();
            return;
        }
        for (Concession c : concessions) {
            int price = (c.getAmount() / c.getUnitPrice());
            options.add(c.getName().toUpperCase() + ", $" + price, () -> {
                chooseConQuantity(c.getId(), price, v);
            });
        }

        for (Merchandise m : merchandises) {
            int price = (m.getQuantity() / m.getUnitPrice());
            options.add(m.getName().toUpperCase() + ", $" + price, () -> {
                chooseMerchQuantity(m.getId(), price, v);
            });
        }

        options.singleDisplayAndSelect("Items Available for Purchase at " + v.getName().toUpperCase() + ":");
    }

    private static void chooseConQuantity(int itemId, int price, Venue venue) {
        System.out.println("Choose the Quantity");
        System.out.println("0) Cancel");
        System.out.print("Enter quantity: ");
        int quantity = UserInput.getIntInput();
        if (quantity <= 0) {
            return;
        }

        System.out.println();
        System.out.print("Enter customer name: ");
        String customerName = UserInput.getStringInput();

        System.out.println();

        Concession concession = DataCache.getById(itemId, Concession::new);
        if (concession.getAmount() < quantity) {
            System.out.println("Out of stock.");
            System.out.println();
            return;
        }
        concession.setAmount(concession.getAmount() - quantity);
        System.out.println("Item: " + concession.getName().toUpperCase());

        CustomerOrder order = new CustomerOrder(customerName, quantity, itemId);
        charge(price, quantity, venue);
    }

    private static void chooseMerchQuantity(int itemId, int price, Venue venue) {
        System.out.println("Choose the Quantity");
        System.out.println("0) Cancel");
        System.out.print("Enter quantity: ");
        int quantity = UserInput.getIntInput();
        if (quantity <= 0) {
            return;
        }

        System.out.println();
        System.out.print("Enter customer name: ");
        String customerName = UserInput.getStringInput();

        System.out.println();

        Merchandise merchandise = DataCache.getById(itemId, Merchandise::new);
        if (merchandise.getQuantity() < quantity) {
            System.err.println("Out of Stock");
            System.out.println();
            return;
        }
        merchandise.setQuantity(merchandise.getQuantity() - quantity);
        System.out.println("Item: " + merchandise.getName().toUpperCase());

        CustomerOrder order = new CustomerOrder(customerName, quantity, itemId);
        charge(price, quantity, venue);
    }

    private static int calculateTotal(int price, int quantity, Venue v) {
        City city = StaticDataHandler.getCityByName(v.getLocation());
        int cityPopulation = city.getPopulation();

        int cost = (int) (Math.log((double) cityPopulation) * price * quantity);
        return cost;
    }

    private static void charge(int price, int quantity, Venue v)
    {
        int totalPrice = calculateTotal(price, quantity, v);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price per item: $" + price);
        System.out.println("------------------------");
        System.out.println("Order Total: $" + totalPrice);
        Budget.get("Logistics").profit(totalPrice);
    }

}