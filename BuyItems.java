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

        int noRent = 0;
        for (Event e : events) {
            if (!e.getVenue().getName().equalsIgnoreCase("empty"))
            {
                options.add("Event: " + e.getVenue().getLocation() + 
                "; Venue: " + e.getVenue().getName().toUpperCase(), () -> {
                    chooseVenue(e.getVenue());
                    });
            }
            else noRent++;
        }

        if (noRent == events.size())
        {
            System.out.println("No venue rented. Unable to sell concessions and merchandise.");
            return;
        }

        options.singleDisplayAndSelect("Choose a Venue:");
    }

    private static void chooseVenue(Venue v) {
        OptionList options = new OptionList();
        options.exitOption = "Back";
        List<Concession> concessions = DataCache.getAllByFilter(o -> o.getVenueId() == v.getId(), Concession::new);
        List<Merchandise> merchandises = DataCache.getAllByFilter(f -> f.getVenueId() == v.getId(), Merchandise::new);

        if (concessions.isEmpty() && merchandises.isEmpty()) {
            System.out.println("No products at "+ v.getName().toUpperCase() + " available at the moment!");
            System.out.println();
            return;
        }
        for (Concession c : concessions) {
            int price = c.getUnitPrice() / v.getSections().size();
            options.add(c.getName().toUpperCase() + ", $" + price, () -> {
                chooseConQuantity(c.getId(), price, v);
            });
        }

        for (Merchandise m : merchandises) {
            int price = m.getUnitPrice() / v.getSections().size();
            options.add(m.getName().toUpperCase() + ", $" + price, () -> {
                chooseMerchQuantity(m.getId(), price, v);
            });
        }

        options.singleDisplayAndSelect("Choose an Item to Purchase:");
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

    private static double calculateTotal(int price, int quantity, Venue v) {
        StateTaxInfo tax = StateTaxRates.getStateTaxInfo(StaticDataHandler.getCityByName(v.getLocation()));
        double cost = (Math.max(tax.localRate, tax.stateRate) * price * quantity) + (price * quantity);
        return cost;
    }

    private static void charge(int price, int quantity, Venue v)
    {
        double totalPrice = calculateTotal(price, quantity, v);
        StateTaxInfo tax = StateTaxRates.getStateTaxInfo(StaticDataHandler.getCityByName(v.getLocation()));
        System.out.println("Quantity: " + quantity);
        System.out.println("Price per item: $" + price);
        System.out.println("Tax: " + Math.max(tax.localRate, tax.stateRate));
        System.out.println("------------------------");
        System.out.printf("Order Total: $%.2f", totalPrice);
        System.out.println();
        Budget.get("Logistics").profit((int)totalPrice);
    }

}