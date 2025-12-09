import java.util.ArrayList;

class Customer implements Actor {
    OptionList options;

    Customer() {
        options = new OptionList();
        options.addExitOption("Exit");
        options.add("Buy Ticket", () -> {TicketVendor.buyTicket();});
        options.add("Buy Merchandise and Concession", () -> {BuyItems.buyItem();});
    }

    @Override
    public void showOptions() {
        System.out.println("\nCustomer Options:");
        options.loopDisplayAndSelect("Choose an option:");
    }
}