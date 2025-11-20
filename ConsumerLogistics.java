public class ConsumerLogistics implements Actor {
    ConcessionSupplies concessionSupplies = new ConcessionSupplies();
    MerchandiseSupplies merchandiseSupplies = new MerchandiseSupplies();

    @Override
    public void showOptions() {
        OptionList options = new OptionList();
        options.add("Concession Supplies", this.concessionSupplies::showOptions);
        options.add("Merchandise Supplies", this.merchandiseSupplies::showOptions);
        options.addExitOption("Exit");
        options.loopDisplayAndSelect("Select an option: ");
    }
}