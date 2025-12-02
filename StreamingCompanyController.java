import java.util.List;

public class StreamingCompanyController implements Actor {

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Exit");
        options.add("Propose Media Contract", this::proposeContract);
        options.add("View Media Contract", this::viewMediaContract);
        options.add("Edit Media Contract", this::editMediaContract);
        options.add("Submit Media Contract", this::submitMediaContract);
        options.add("Suggest Advertisement", this::suggestAdvertisement);

        options.loopDisplayAndSelect("\nStreaming Company Options\nEnter a number: ");
    }

    public void suggestAdvertisement()
    {
        StreamingCompany company = getCompany();
        if (company == null)
        {
            return;
        }
        List<MediaContract> contract = DataCache.getAllByFilter(c -> c.getStreamingCompany().getCompanyName() == company.getCompanyName(), MediaContract::new);
        if (contract.isEmpty() || !contract.get(0).getStatus().equals(ScriptStatus.APPROVED))
        {
            System.out.println("Cannot propose an advertisement without an approved media contract.");
            return;
        }
        System.out.println("Enter advertisement content proposal: ");
        String adContent = UserInput.getStringInput().trim();
        Advertisement ad = new Advertisement(contract.get(0), company, adContent);
    }

    public void proposeContract()
    {
        StreamingCompany company = getCompany();
        if (company == null)
        {
            return;
        }
        System.out.println("Enter Desired Event ID: ");
        int eventId = UserInput.getIntInput();
        Event event = DataCache.getById(eventId, Event::new);
        if (event == null)
        {
            return;
        }
        System.out.println("Enter Desired Payment: ");
        int payment = UserInput.getIntInput();
        System.out.println("Enter Start Date: ");
        long startDate = UserInput.getLongInput();
        if (startDate < System.currentTimeMillis())
        {
            System.out.println("Invalid start date.");
            return;
        }
        System.out.println("Enter End Date: ");
        long endDate = UserInput.getLongInput();
        if (endDate < startDate)
        {
            System.out.println("Invalid end date.");
            return;
        }

        MediaContract proposedContract = new MediaContract(event, company, payment, startDate, endDate);
        company.setContract(proposedContract);
    }

    public void viewMediaContract()
    {
        StreamingCompany company = getCompany();
        if (company == null)
        {
            return;
        }
        List<MediaContract> contract = DataCache.getAllByFilter(c -> c.getStreamingCompany().getCompanyName() == company.getCompanyName(), MediaContract::new);
        if (contract.size() == 0)
        {
            System.out.println("No contract exists");
            return;
        }
        System.out.println("Media Contract Details: ");
        Event event = contract.get(0).getEvent();
        int payment = contract.get(0).getTotalPayment();
        long startDate = contract.get(0).getStartDate();
        long endDate = contract.get(0).getEndDate();
        System.out.println("Contract Status: " + contract.get(0).getStatus().toString().toUpperCase());
        System.out.println("Event: " + event.getId() + " at " + event.getVenue().getLocation());
        System.out.println("Payment: $" + payment);
        System.out.println("Start Date: $" + startDate);
        System.out.println("End Date: $" + endDate);
    }

    public void editMediaContract()
    {
        StreamingCompany company = getCompany();
        if (company == null)
        {
            return;
        }
        List<MediaContract> contract = DataCache.getAllByFilter(c -> c.getStreamingCompany().getCompanyName() == company.getCompanyName(), MediaContract::new);
        if (contract.size() == 0)
        {
            System.out.println("No contract exists");
            return;
        }
        OptionList options = new OptionList();
        options.add("Change Event",
                    () -> {
                        changeEvent(contract.get(0).getEvent(), contract.get(0));
                    });
        options.add("Change Requested Payment Amount",
                    () -> {
                        changePayment(contract.get(0).getTotalPayment(), contract.get(0));
                    });
        options.add("Change Start Date",
                    () -> {
                        changeStartDate(contract.get(0).getStartDate(), contract.get(0));
                    });
        options.add("Change End Date",
                    () -> {
                        changeEndDate(contract.get(0).getEndDate(), contract.get(0));
                    });

        options.singleDisplayAndSelect("\nEdit Media Contract\nSelect Detail to Edit: ");
    }

    public void submitMediaContract()
    {
        StreamingCompany company = getCompany();
        if (company == null)
        {
            return;
        }
        List<MediaContract> contract = DataCache.getAllByFilter(c -> c.getStreamingCompany().getCompanyName() == company.getCompanyName(), MediaContract::new);
        if (contract.size() == 0)
        {
            System.out.println("No media contract to submit");
            return;
        }
        else if (contract.get(0).contractAccepted())
        {
            System.out.println("Media contract already accepted");
            return;
        }
        contract.get(0).setStatus(ScriptStatus.UNDER_REVIEW);
        System.out.println("Media contract has been submitted.\nAwaiting approval.");
    }

    private void changeEvent(Event event, MediaContract contract)
    {
        System.out.println("Changing Event");
        System.out.println("Enter new desired event ID: ");
        int choice = UserInput.getIntInput();
        Event newEvent = DataCache.getById(choice, Event::new);
        if (newEvent == null)
        {
            return;
        }
        else if (newEvent.equals(event))
        {
            System.out.println("No change needed");
            return;
        }

        contract.setEventCovered(newEvent);
        System.out.println("Event has been updated.");
    }


    private void changePayment(int payment, MediaContract contract)
    {
        System.out.println("Changing Payment Requested");
        System.out.println("Enter new desired payment amount: ");
        int newPayment = UserInput.getIntInput();
        if (payment == newPayment)
        {
            System.out.println("No change needed.");
            return;
        }
        contract.setTotalPayment(newPayment);
        System.out.println("Payment has been updated.");
    }


    private void changeStartDate(long startDate, MediaContract contract)
    {
        System.out.println("Changing Start Date");
        System.out.println("Enter new desired start date: ");
        long date = UserInput.getLongInput();

        if (date == startDate)
        {
            System.out.println("No change needed");
            return;
        }
        else if (date >= contract.getEndDate())
        {
            System.out.println("Invalid start date");
            return;
        }
        contract.setStartDate(date);
        System.out.println("Start Date has been updated.");
    }

    private void changeEndDate(long endDate, MediaContract contract)
    {
        System.out.println("Changing End Date");
        System.out.println("Enter new desired end date: ");
        long date = UserInput.getLongInput();

        if (date == endDate)
        {
            System.out.println("No change needed");
            return;
        }
        else if (date <= contract.getStartDate())
        {
            System.out.println("Invalid end date");
            return;
        }
        contract.setEndDate(date);
        DataCache.addObject(contract);
        System.out.println("End Date has been updated.");
    }

    private StreamingCompany getCompany()
    {
        System.out.println("Enter Streaming Company ID: ");
        int companyId = UserInput.getIntInput();
        StreamingCompany company = DataCache.getById(companyId, StreamingCompany::new);
        return company;
    }
}
