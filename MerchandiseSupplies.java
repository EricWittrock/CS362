import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MerchandiseSupplies implements Actor {
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
        OptionList options = new OptionList();
        options.addExitOption("Back");
        List<Merchandise> items = DataCache.getAll(Merchandise::new).stream()
            .collect(Collectors.toList());;

        for (Merchandise item : items) {
            options.add(item.getName() +
                    "\n Unit Price: $" + item.getUnitPrice(),
                    () -> {
                        chooseQuantity(item);
                    });
        }

        options.singleDisplayAndSelect("\nMerchandise\nSelect Merchandise to Order: ");
    }

    private static void chooseQuantity(Merchandise item) {
        while (true) {
            System.out.println("0) Back");
            System.out.println("Enter supplier name: ");
            String supplier = UserInput.getStringInput();
            Merchandise merch = new Merchandise(item.getName(), item.getType(), item.getUnitPrice());
            merch.setSupplier(supplier);

            System.out.println("Enter quantity: ");
            int quantity = UserInput.getIntInput();
            if (quantity <= 0)
                return;

            if (quantity > 0)
            {
                System.out.println("Enter ID of Venue to be Delivered to: ");
                int venueId = UserInput.getIntInput();
                Venue venue = DataCache.getById(venueId, Venue::new);
                if(venue == null)
                {
                    System.out.println("Invalid venue ID.");
                }

                MerchandiseOrder order = new MerchandiseOrder(merch, quantity, venue);
                System.out.println("Merchandise will be delivered to " + venue.getName());
                return;
            }
        }
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
        printMerchInfo(merch);
    }

    private void printMerchInfo(List<Merchandise> merchandise)
    {
        for (Merchandise m : merchandise)
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
            System.out.println("Merchandise: " + order.getMerchandise().getName() 
            + "\n Supplier: " + order.getMerchandise().getSupplier() + "\n Quantity: " + order.getQuantity() 
            + "\n Unit Price: " + order.getMerchandise().getUnitPrice()
            + "\n Venue: " + order.getVenue().getName());
        }
    }

    public void viewMerchandiseOrderByVenue()  {
        System.out.println("Enter Venue ID: ");
        int venueid = UserInput.getIntInput();
        Venue venue = DataCache.getById(venueid, Venue::new);
        if(venue == null)
        {
            System.out.println("Invalid Venue ID");
            return;
        }
        System.out.println("Merchandise Orders to be Delievered to " + venue.getName());
        List<Merchandise> venuMerchandise = new ArrayList<>();
        List<MerchandiseOrder> merchandiseOrders = DataCache.getAll(MerchandiseOrder::new);
        if (merchandiseOrders.size() == 0)
        {
            System.out.println("No order in progress");
            return;
        }
        for (MerchandiseOrder m : merchandiseOrders)
        {
            if(m.getVenue().getId() == venueid)
            {
                venuMerchandise.add(m.getMerchandise());
            }
        }

        printMerchInfo(venuMerchandise);
    }

}