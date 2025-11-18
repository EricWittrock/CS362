import java.util.ArrayList;

class Customer implements Actor {
    ArrayList<Option> options;

    Customer() {
        options = new ArrayList<Option>();
        options.add(new Option("Buy Ticket", () -> {TicketVendor.buyTicket();}));
    }

    @Override
    public void showOptions() {
        System.out.println("\nCustomer Options:");       
        while (true) {
            // Print options
            int numNonOptions = 1;
			System.out.println("0) Exit");

			for(int i = 0; i < options.size(); i++)
				options.get(i).Display(i + numNonOptions);

            //Get user choice
            System.out.print("Choose option: ");
            int choice = UserInput.getIntInput();

            // Execute choice
            if(choice == 0)
            	break;
            options.get(choice - numNonOptions).select();
        }
    }
}