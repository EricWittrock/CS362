import java.util.List;

public class MarketingDepartment implements Actor {

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Exit");
        options.add("View Pending Media Contracts", this::viewPendingContracts);
        options.add("Pending Media Contracts", this::decidePendingContracts);
        options.add("View Approved Media Contracts", this::viewApprovedMediaContracts);
        options.add("View Suggested Advertisements", this::viewAdvertisements);
        options.add("Suggested Advertisements", this::decideAdvertisements);
        options.add("Archive Used Advertisements", this::archiveAdvertisements);
        options.loopDisplayAndSelect("\nMarketing Department\nEnter a number: ");
    }

    public void viewPendingContracts() {
        List<MediaContract> contracts = DataCache.getAllByFilter(c -> c.getStatus().
        equals(ScriptStatus.UNDER_REVIEW) && !c.isExpired(),
                MediaContract::new);

        if (contracts.isEmpty())
        {
            System.out.println("No pending media contracts");
            return;
        }

        System.out.println("Pending Media Contracts: ");
        for (MediaContract mediaContract : contracts) {
            mediaContract.printDetails();
        }
    }

    public void decidePendingContracts() {
        List<MediaContract> contracts = DataCache.getAllByFilter(c -> c.getStatus().equals(ScriptStatus.UNDER_REVIEW)
        , MediaContract::new);
        if (contracts.isEmpty())
        {
            System.out.println("No pending media contracts!");
            return;
        }
        OptionList options = new OptionList();
        options.addExitOption("Back");
        for (MediaContract mediaContract : contracts) {
            if (mediaContract.isExpired())
            {
                mediaContract.setStatus(ScriptStatus.ARCHIVED);
                contracts.remove(mediaContract);
            }
            options.add(mediaContract.serialize(), () -> {
                contractDecision(mediaContract);
            });
        }
        options.singleDisplayAndSelect("Select a Contract to Approve/Reject: ");
    }

    private void contractDecision(MediaContract contract) {
        while (true) {
            System.out.println("Approve or Reject Selected Media Contract");
            System.out.println("0) Back");
            System.out.println("Enter 1 to Approve, 2 to Reject: ");
            int choice = UserInput.getIntInput(0, 2);
            if (choice == 0) {
                return;
            } else if (choice == 1) {
                contract.setStatus(ScriptStatus.APPROVED);
                System.out.println("Media Contract Approved.");
                return;
            } else {
                contract.setStatus(ScriptStatus.REJECTED);
                System.out.println("Media Contract Rejected");
                return;
            }
        }
    }

    public void viewApprovedMediaContracts() {
        List<MediaContract> contracts = DataCache.getAllByFilter(c -> c.getStatus().equals(ScriptStatus.APPROVED)
        , MediaContract::new);
        if (contracts.isEmpty())
        {
            System.out.println("No approved media contracts");
            return;
        }
        for (MediaContract mediaContract : contracts) {
            if (mediaContract.isExpired())
            {
                mediaContract.setStatus(ScriptStatus.ARCHIVED);
            }
            mediaContract.printDetails();
        }
    }

    public void viewAdvertisements() {
        List<Advertisement> ads = DataCache.getAllByFilter(a -> a.getStatus()
        .equals(ScriptStatus.PROPOSED), Advertisement::new);
        if (ads.isEmpty())
        {
            System.out.println("No advertisement suggestions");
            return;
        }
        for (Advertisement advertisement : ads) {
            advertisement.printDetails();
        }
    }

    public void archiveAdvertisements() {
        List<Advertisement> ads = DataCache.getAllByFilter(a -> a.getStatus()
        .equals(ScriptStatus.APPROVED) || a.getStatus().equals(ScriptStatus.REJECTED)
        || a.getContract().isExpired(), Advertisement::new);
        if (ads.isEmpty())
        {
            System.out.println("No advertisement suggestions to archive.");
            return;
        }
        OptionList options = new OptionList();
        options.addExitOption("Back");
        for (Advertisement advertisement : ads) {
            advertisement.printDetails();
            options.add(advertisement.serialize(), () -> {
                archive(advertisement);
            });
        }
        options.singleDisplayAndSelect("Select an Advertisement to Archive: ");
    }

    private void archive(Advertisement advertisement)
    {
        while (true) {
            System.out.println("Archive Advertisement");
            System.out.println("Enter 0 to go Back or 1 to Archive: ");
            int choice = UserInput.getIntInput(0, 1);
            if (choice == 0) {
                return;
            } else {
                advertisement.setStatus(ScriptStatus.ARCHIVED);
                System.out.println("Advertisement Archived.");
                return;
            }
        }
    }

    public void decideAdvertisements()
    {
        List<Advertisement> ads = DataCache.getAllByFilter(a -> a.getStatus()
        .equals(ScriptStatus.PROPOSED), Advertisement::new);
        if (ads.isEmpty())
        {
            System.out.println("No advertisement suggestions available.");
            return;
        }
        OptionList options = new OptionList();
        options.addExitOption("Back");

        for (Advertisement advertisement : ads) {
            options.add(advertisement.serialize(), () -> {
                adDecision(advertisement);
            });
        }
        options.singleDisplayAndSelect("Select an Advertisement Suggestion to Approve/Reject: ");
    }

    private void adDecision(Advertisement advertisement) {
        while (true) {
            System.out.println("Approve or Reject Selected Advertisement Suggestion");
            System.out.println("0) Back");
            System.out.println("Enter 1 to Approve, 2 to Reject: ");
            int choice = UserInput.getIntInput(0, 2);
            if (choice == 0) {
                return;
            } else if (choice == 1) {
                advertisement.setStatus(ScriptStatus.APPROVED);
                System.out.println("Advertisement Suggestion Approved.");
                return;
            } else {
                advertisement.setStatus(ScriptStatus.REJECTED);
                System.out.println("Advertisement Suggestion Rejected");
                return;
            }
        }
    }
}
