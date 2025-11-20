import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MerchandiseSupplies implements Actor {
    private static List<MerchandiseOrder> merchandiseOrders = new ArrayList<>();

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Back");
        options.add("Order Merchandise", this::orderMerchandise);
        options.add("View Merchandise By Type", this::viewMerchandiseByType);
        options.add("View Merchandise By Supplier", this::viewMerchandiseBySupplier);
        options.add("View Merchandise Order By Venue", this::viewMerchandiseOrderByVenue);
        options.add("View Merchandise Order", this::viewOrder);
        options.add("Remove Merchandise Order", this::removeOrder);
        
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
                merchandiseOrders.add(order);
                System.out.println("Merchandise will be delivered to " + venue.getName());
                return;
            }
        }
    }

    public void viewMerchandiseBySupplier() {
        System.out.println("0) Back");
        System.out.println("Enter supplier name: ");
        String supplier = UserInput.getStringInput();
        List<Merchandise> merchandises = DataCache.getAll(Merchandise::new).stream()
        .filter(merch -> merch.getSupplier() == supplier)
        .collect(Collectors.toList());
        System.out.println("Merchandise from " + supplier + ":");
        printMerchInfo(merchandises);
    }

    public void viewMerchandiseByType(){
        System.out.println("0) Back");
        System.out.println("Enter Type of Merchandise \n" +
        "(Accessory, Collectible, Hat, Belt, TShirt): ");
        String typeString = UserInput.getStringInput();
        MerchType type = Merchandise.getMerchTypeFromString(typeString);
        if(type == null)
        {
            System.out.println("No such type.");
            return;
        }

        List<Merchandise> merch = DataCache.getAll(Merchandise::new).stream()
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

        if (merchandiseOrders == null)
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

    public void removeOrder(){
        while (true)
        {
            System.out.println("Remove Merchandise Order");
            System.out.println("0) Back");
            System.out.println("Enter Merchandise Order ID: ");
            int merchandiseOrderId = UserInput.getIntInput(0, merchandiseOrders.size());
            if (merchandiseOrderId == 0)
            {
                return;
            }

            MerchandiseOrder order = DataCache.getById(merchandiseOrderId, MerchandiseOrder::new);
            if (order != null && merchandiseOrders.contains(order))
            {
                removeOrder(merchandiseOrderId);
                return;
            }
            else if (merchandiseOrders.isEmpty())
            {
                System.out.println("No orders in progress");
                return;
            }
            else
            {
                System.out.println("Invalid Merchandise Order ID.");
            }
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
        for (MerchandiseOrder m : merchandiseOrders)
        {
            if(m.getVenue().equals(venue))
            {
                venuMerchandise.add(m.getMerchandise());
            }
        }

        printMerchInfo(venuMerchandise);
    }

    private static void removeOrder(int merchandiseOrderId)
    {
        MerchandiseOrder remove = DataCache.getById(merchandiseOrderId, MerchandiseOrder::new);
        merchandiseOrders.remove(remove);
        System.out.println("Removing Merchandise Order " + merchandiseOrderId);
    }
}