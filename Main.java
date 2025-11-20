import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
        showOptions();

        DataCache.saveAll();
	}

    public static void showOptions() {
        OptionList options = new OptionList();
        options.addExitOption("Exit Application");
        options.add("Customer", () -> {new Customer().showOptions();});
        options.add("Logistics Department", () -> {new LogisticsDep().showOptions();});
        options.add("Consumer Logistics Department", () -> {new ConsumerLogistics().showOptions();});
        options.add("Marketing Department", () -> {new MarketingDepartment().showOptions();});
        options.add("Choreographer", () -> {new Choreographer().showOptions();});
        options.add("Stakeholder", () -> {new Stakeholder().showOptions();});
        options.add("Manager", () -> {new Manager().showOptions();});
        options.add("Streaming Company", () -> {new StreamingCompanyController().showOptions();});
        options.add("Budget Manager", () -> {new BudgetManager().showOptions();});
        options.add("Finance Manager", () -> {new FinanceManager().showOptions();});

        options.loopDisplayAndSelect("Select User Type:");
    }
}
