import java.util.ArrayList;
import java.util.List;

public class BudgetManager implements Actor {
    private OptionList choices;
    private Budget payer;
    private Budget payee;

    public BudgetManager() {
        choices = new OptionList();
        choices.addExitOption("Exit");
        choices.add("View Budgets", this::viewBudget);
        choices.add("Allocate Funds", this::allocateFundsMenu);
        choices.add("View All Funds", this::viewAllFunds);
        choices.add("View Profits", this::viewProfits);
        choices.add("Create New Department Budget", this::createDepartmentBudget);
    }

    private void viewBudget() {
        List<Budget> budgets = DataCache.getAll(Budget::new);
        OptionList options = new OptionList();
        options.addExitOption("Back");
        for (Budget budget : budgets) {
            options.add("View " + budget.getName() + " Budget", () -> {budget.print();});
        }
        options.loopDisplayAndSelect("Select a Budget to View:");
    }

    private void allocatateFunds(int amount) {
        if(payer.funds() >= amount) {
            payer.charge(amount);
            payee.fund(amount);
            System.out.println("Allocated $" + amount + " from " + payer.getName() + " to " + payee.getName());
        } else {
            System.out.println("Insufficient funds. Only " + payer.funds() + " in " + payer.getName() + " budget.");
        }
    }

    private void viewAllFunds() {
        List<Budget> budgets = DataCache.getAll(Budget::new);
        System.out.println("Budget Summary:");
        for (Budget budget : budgets) {
            System.out.println(budget.getName() + ": $" + budget.funds());
        }
    }

    private void viewProfits() {
        List<Budget> budgets = DataCache.getAll(Budget::new);
        System.out.println("Budget Summary:");
        for (Budget budget : budgets) {
            System.out.println(budget.getName() + ": $" + budget.profits());
        }
    }
    
    private void createDepartmentBudget() {
        System.out.print("Enter Department Name: ");
        String name = UserInput.getStringInput();
        System.out.print("Enter Initial Funds: ");
        int funds = UserInput.getIntInput();
        Budget newBudget = new Budget(name, funds);
        System.out.println("Created budget for " + name + " with $" + funds);
    }

    private void allocateFundsMenu() {
        List<Budget> budgets = DataCache.getAll(Budget::new);
        OptionList payers = new OptionList();
        OptionList payees = new OptionList();
        for (Budget budget : budgets) {
            payers.add(budget.getName(), () -> {
                this.payer = budget;
            });
            payees.add(budget.getName(), () -> {
                this.payee = budget;
            });
        }
        payers.singleDisplayAndSelect("Select Payer:");
        payees.singleDisplayAndSelect("Select Payee:");
        System.out.println("Enter amount 0 to cancel");
        System.out.println("Or any amount to continue: ");
        int amount = UserInput.getIntInput();
        if(amount == 0) {
            return;
        }
        allocatateFunds(amount);
    }

    @Override
    public void showOptions() {
        choices.loopDisplayAndSelect("Budget Manager Options:");
    }
}