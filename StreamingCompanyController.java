import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamingCompanyController implements Actor {

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Exit");
        options.add("Propose Media Contract", this::proposeContract);
        options.add("View Media Contracts", this::viewMediaContract);
        options.add("Edit Media Contract", this::editMediaContract);
        // options.add("Submit Media Contract", this::submitMediaContract);
        // options.add("Accept/Refuse Media Contract", this::acceptRefuseContract);
        // options.add("Create Advertisement", this::createAd);
        // options.add("Set Advertisement Budget", this::setBudget);

        options.loopDisplayAndSelect("\nStreaming Company Options\nEnter a number: ");
    }

    public void proposeContract()
    {
        StreamingCompany company = getCompany();
        System.out.println("Enter Desired Event ID: ");
        int eventId = UserInput.getIntInput();
        Event event = DataCache.getById(eventId, Event::new);
        System.out.println("Enter Desired Payment: ");
        int payment = UserInput.getIntInput();
        System.out.println("Enter Start Date: ");
        long startDate = UserInput.getLongInput();
        System.out.println("Enter End Date: ");
        long endDate = UserInput.getLongInput();

        MediaContract proposedContract = new MediaContract(event, company, payment, startDate, endDate);
        company.setContract(proposedContract);
    }

    public void viewMediaContract()
    {
        StreamingCompany company = getCompany();
        MediaContract contract = company.getContract();
        System.out.println("Media Contract Details: ");
        Event event = contract.getEvent();
        int payment = contract.getTotalPayment();
        long startDate = contract.getStartDate();
        long endDate = contract.getEndDate();
        System.out.println("Contract Status: " + contract.getStatus().toString().toUpperCase());
        System.out.println("Event: " + event.getId() + " at " + event.getVenue().getLocation());
        System.out.println("Payment: $" + payment);
        System.out.println("Start Date: $" + startDate);
        System.out.println("End Date: $" + endDate);
    }

    public void editMediaContract()
    {
        StreamingCompany company = getCompany();
        MediaContract contract = company.getContract();
        OptionList options = new OptionList();
        options.add("Change Event", edit(contract.getEvent().getId()));
        options.add("Change Requested Payment Amount", edit(contract.getTotalPayment()));
        options.add("Change Start Date", edit(contract.getStartDate()));
        options.add("Change End Date", edit(contract.getEndDate()));
        options.singleDisplayAndSelect("\nEdit Media Contract\nSelect Detail to Edit: ");
    }

    public void edit(int id)
    {

    }

    private StreamingCompany getCompany()
    {
        System.out.println("Enter Streaming Company ID: ");
        int companyId = UserInput.getIntInput();
        StreamingCompany company = DataCache.getById(companyId, StreamingCompany::new);
        return company;
    }

}
