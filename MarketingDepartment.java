public class MarketingDepartment implements Actor {

    @Override
    public void showOptions()
    {
        OptionList options = new OptionList();
        options.addExitOption("Exit");
        options.add("View Pending Media Contracts", this::viewPendingContracts);
        options.add("View Media Contracts", this::viewMediaContracts);
        options.add("Approve/Reject Media Contract", this::contractDecision);
        options.add("View Suggested Advertisements", this::viewAdvertisements);
        options.add("Approve/Reject Suggested Advertisements", this::adDecision);
        options.loopDisplayAndSelect("\nMarketing Department\nEnter a number: ");
    }

    public void viewPendingContracts()
    {

    }

    public void viewMediaContracts()
    {

    }

    public void contractDecision()
    {

    }

    private void chooseContract()
    {

    }

    public void viewAdvertisements()
    {

    }

    public void adDecision()
    {

    }

    private void chooseAd()
    {
        
    }
}
