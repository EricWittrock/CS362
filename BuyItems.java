import java.util.List;

public class BuyItems
{
    public static void buyItem()
    {
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
        options.loopDisplayAndSelect("Choose a Venue to Purchase From");
    }

    private static void chooseVenue(Venue v)
    {
        OptionList options = new OptionList();
        options.exitOption = "Back";
        List<Concession> concessions = DataCache.getAllByFilter(o -> o.getVenueId() == v.getId(), Concession::new);
        List<Merchandise> merchandises = DataCache.getAllByFilter(o -> o.getVenue() == v, Merchandise::new);

        for (Concession c: concessions)
        {
            int price = (c.getUnitPrice() / c.getAmount()) + 2;
            options.add(c.getName().toUpperCase() + ", $" + price, () -> {
                chooseQuantity(c.getId(), price);
            });
        }

        for (Merchandise m : merchandises)
        {
            MerchandiseOrder mo = DataCache.getByFilter(o -> o.getMerchandise().equals(m), MerchandiseOrder::new);
            int price = (m.getUnitPrice() / mo.getQuantity()) + 5;
            options.add(m.getName().toUpperCase() + ", $" + price, () -> {
                chooseQuantity(m.getId(), price);
            });
        }

        options.singleDisplayAndSelect("Items Available for Purchase at " + v.getName().toUpperCase() + ":");
    }

    private static void chooseQuantity(int itemId, int price)
    {
        System.out.println("Enter customer name: ");
        String customerName = UserInput.getStringInput();
        System.out.println("Enter quantity: ");
        int quantity = UserInput.getIntInput();
        CustomerOrder order = new CustomerOrder(customerName, quantity, itemId);
    }
}
