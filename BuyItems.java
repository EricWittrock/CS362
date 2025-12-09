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
        options.singleDisplayAndSelect("Choose a Venue to Purchase From");
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
        System.out.println("0) Cancel");
        System.out.println("Enter quantity: ");
        int quantity = UserInput.getIntInput();
        if (quantity <= 0)
        {
            return;
        }

        System.out.println("Enter customer name: ");
        String customerName = UserInput.getStringInput();

        Merchandise merchandise = DataCache.getById(itemId, Merchandise::new);
        if (merchandise == null)
        {
            Concession concession = DataCache.getById(itemId, Concession::new);
            if (concession.getAmount() < quantity)
            {
                System.out.println("Out of stock.");
                return;
            }
            concession.setAmount(concession.getAmount() - quantity);
            System.out.println("Item: " + concession.getName().toUpperCase());
        }
        else
        {
            MerchandiseOrder merchOrder = DataCache.getByFilter(mo -> mo.getMerchandise().equals(merchandise), MerchandiseOrder::new);
            if (merchOrder.getQuantity() < quantity)
            {
                System.err.println("Out of Stock");
                return;
            }
            merchOrder.setQuantity(merchOrder.getQuantity() - quantity);
            System.out.println("Item: " + merchandise.getName().toUpperCase());
        }

        CustomerOrder order = new CustomerOrder(customerName, quantity, itemId);
        int totalPrice = price * quantity;
        System.out.println("Quantity: " + quantity);
        System.out.println("Price per item: $" + price);
        System.out.println("------------------------");
        System.out.println("Order Total: $" + totalPrice);
        Budget.get("Logistics").profit(totalPrice);
    }

}