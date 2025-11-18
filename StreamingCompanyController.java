import java.util.ArrayList;
import java.util.List;

public class StreamingCompanyController implements Actor {

    private List<StreamingCompany> companies = DataCache.getAllStreamingCompanies();

    public StreamingCompanyController() {}

    @Override
    public void showOptions() {
        System.out.println("Streaming Company Options");

        while (true){
            System.out.println("0) Exit");
            System.out.println("1) Purchase media rights");
            System.out.println("2) View events");
            System.out.println("Enter option: ");

            int choice = UserInput.getIntInput(0, 2);

            if (choice == 0){
                break;
            }
            else if (choice == 1){
                purchaseMediaRights();
            }
            else if (choice == 2){
                viewEvents();
            }
        }
    }

    private void viewEvents() {
        List<Event> eventsList = DataCache.getAllEvents();
        System.out.println("0) Exit");

        if (eventsList.size() == 0){
            System.out.println("No events found. Exiting back to previous menu.");
            showOptions();
        }
        else {
            int eventNum = 1;

            // list all events
            for (Event event : eventsList) {
                System.out.println(eventNum + ". " + event.serialize());
                eventNum++;
            }

            // go back to options
            int choice = UserInput.getIntInput(0, 1);
            while (true) {
                if (choice == 0) {
                    showOptions();
                    break;
                }
            }
        }
    }

    private void purchaseMediaRights() {
        System.out.println("Enter Company ID: ");
        int companyId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        System.out.println("Enter Event ID: ");
        int eventId = UserInput.getIntInput(0, Integer.MAX_VALUE);

        while(true){
            Event event = DataCache.getEventById(eventId);
            StreamingCompany streamingCompany = DataCache.getStreamingCompanyById(companyId);

            if (event == null) {
                System.out.println("Error: Event not found.");
                break;
            }

            if (streamingCompany == null){
                System.out.println("Error: Company not found");
                break;
            }

            for (StreamingCompany company: companies){
                if (company.getPurchasedEvents().contains(event)){
                    System.out.println("Error: Event media rights has already been purchased.");
                    break;
                }
            }

            streamingCompany.getPurchasedEvents().add(event);
            System.out.println("Successful Purchase\nPurchased " + event.serialize());
            break;
        }
    }

}
