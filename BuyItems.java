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
        options.exitOption = "Cancel";
        List<Concession> concessions = DataCache.getAllByFilter(o -> o.getVenueId() == v.getId(), Concession::new);
        List<MerchandiseOrder> merchandises = DataCache.getAllByFilter(o -> o.getVenue() == v, MerchandiseOrder::new);

        options.singleDisplayAndSelect("Items Available for Purchase:");
    }
}
