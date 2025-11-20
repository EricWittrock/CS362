public class StreamingCompanyController implements Actor {

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Exit");
        options.add("Propose Media Contract", this::proposeContract);
        options.add("View Media Contract", this::viewMediaContract);
        options.add("Edit Media Contract", this::editMediaContract);
        options.add("Submit Media Contract", this::submitMediaContract);
        options.add("Propose Advertisement", this::proposeAdvertisement);

        options.loopDisplayAndSelect("\nStreaming Company Options\nEnter a number: ");
    }

    public void proposeAdvertisement()
    {
        
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
        options.add("Change Event",
                    () -> {
                        changeEvent(contract.getEvent(), contract);
                    });
        options.add("Change Requested Payment Amount",
                    () -> {
                        changePayment(contract.getTotalPayment(), contract);
                    });
        options.add("Change Start Date",
                    () -> {
                        changeStartDate(contract.getStartDate(), contract);
                    });
        options.add("Change End Date",
                    () -> {
                        changeEndDate(contract.getEndDate(), contract);
                    });

        options.singleDisplayAndSelect("\nEdit Media Contract\nSelect Detail to Edit: ");
    }

    public void submitMediaContract()
    {
        StreamingCompany company = getCompany();
        MediaContract contract = company.getContract();
        if (contract == null)
        {
            System.out.println("No media contract to submit");
            return;
        }
        else if (contract.contractAccepted())
        {
            System.out.println("Media contract already accepted");
            return;
        }
        contract.setStatus(ScriptStatus.UNDER_REVIEW);
        DataCache.addObject(contract);
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
            System.out.println("Event does not exist");
            return;
        }
        else if (newEvent.equals(event))
        {
            System.out.println("No change needed");
            return;
        }

        contract.setEventCovered(newEvent);
        DataCache.addObject(contract);
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
        DataCache.addObject(contract);
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
        DataCache.addObject(contract);
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
