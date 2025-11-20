import java.util.List;

public class MarketingDepartment implements Actor {

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Exit");
        options.add("View Pending Media Contracts", this::viewPendingContracts);
        options.add("Pending Media Contracts", this::decidePendingContracts);
        options.add("View Approved Media Contracts", this::viewMediaContracts);
        options.add("View Suggested Advertisements", this::viewAdvertisements);
        options.add("Suggested Advertisements", this::decideAdvertisements);
        options.loopDisplayAndSelect("\nMarketing Department\nEnter a number: ");
    }

    public void viewPendingContracts() {
        List<MediaContract> contracts = DataCache.getAllByFilter(c -> c.getStatus().equals(ScriptStatus.UNDER_REVIEW),
                MediaContract::new);

        System.out.println("Pending Media Contracts: ");
        for (MediaContract mediaContract : contracts) {
            mediaContract.printDetails();
        }
    }

    public void decidePendingContracts() {
        List<MediaContract> contracts = DataCache.getAllByFilter(c -> c.getStatus().equals(ScriptStatus.UNDER_REVIEW),
                MediaContract::new);
        OptionList options = new OptionList();
        options.addExitOption("Back");
        for (MediaContract mediaContract : contracts) {
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

    public void viewMediaContracts() {
        List<MediaContract> contracts = DataCache.getAll(MediaContract::new);
        for (MediaContract mediaContract : contracts) {
            mediaContract.printDetails();
        }
    }

    public void viewAdvertisements() {
        List<Advertisement> ads = DataCache.getAllByFilter(a -> a.getStatus()
        .equals(ScriptStatus.PROPOSED), Advertisement::new);

        for (Advertisement advertisement : ads) {
            advertisement.printDetails();
        }
    }

    public void decideAdvertisements()
    {
        List<Advertisement> ads = DataCache.getAllByFilter(a -> a.getStatus()
        .equals(ScriptStatus.PROPOSED), Advertisement::new);
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
