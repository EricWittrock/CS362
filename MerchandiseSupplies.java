import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MerchandiseSupplies implements Actor {

    Venue venue;
    
    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Back");
        options.add("Order Merchandise", this::orderMerchandise);
        options.add("View Merchandise By Type", this::viewMerchandiseByType);
        options.add("View Merchandise Order By Venue", this::viewMerchandiseOrderByVenue);
        options.add("View Merchandise Order", this::viewOrder);
        
        options.loopDisplayAndSelect("\nMerchandise Supplies Menu\nEnter a number: ");
    }

    public void orderMerchandise() {
        System.out.println("Ordering Merchandise\n");
        System.out.println("Enter Merchandise Name: ");
        String name = UserInput.getStringInput();
        System.out.println("Enter Merchandise Type (TSHIRT, HAT, BELT, COLLECTIBLE, ACCESSORY): ");
        String type = UserInput.getStringInput();
        System.out.println("Enter quantity: ");
        int quantity = UserInput.getIntInput();
        System.out.println("Enter unit price: ");
        int unitPrice = UserInput.getIntInput();

        selectVenue("Select Venue for Merchandise: ");

        Merchandise merchandise = new Merchandise(name, MerchType.valueOf(type.toUpperCase()), unitPrice, quantity, venue);
        MerchandiseOrder merchandiseOrder = new MerchandiseOrder(merchandise);
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

    public void viewMerchandiseByType(){
        System.out.println("0) Back");
        System.out.println("Enter Type of Merchandise \n" +
        "(Accessory, Collectible, Hat, Belt, TShirt): ");
        String typeString = UserInput.getStringInput();
        MerchType type = MerchType.valueOf(typeString.toUpperCase());
        if(type == null)
        {
            System.out.println("No such type.");
            return;
        }

        List<Merchandise> merch = DataCache.getAll(Merchandise::new)
        .stream()
        .filter(m -> m.getType() == type)
        .collect(Collectors.toList());

        System.out.println("Available Merchandise in " + typeString.toUpperCase());
        for (Merchandise m : merch)
        {
            m.print();
        }
    }

    public void viewOrder() {
        System.out.println("Current Order:");

        List<MerchandiseOrder> merchandiseOrders = DataCache.getAll(MerchandiseOrder::new);

        if (merchandiseOrders.size() == 0)
        {
            System.out.println("No order in progress");
            return;
        }

        for (MerchandiseOrder order : merchandiseOrders) {
            order.print();
        }
    }

    public void viewMerchandiseOrderByVenue() {
        selectVenue("Select Venue to View Merchandise:");
        List<Merchandise> merchandises = DataCache.getAll(Merchandise::new).stream()
            .filter(c -> c.getVenueId() == venue.getId())
            .collect(Collectors.toList());
        System.out.println(" Merchandise at " + venue.getName() + " in " + venue.getLocation() + ":");
        for (Merchandise m : merchandises) {
            m.print();
        }
    }
}