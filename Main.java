import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
        DataCache.loadAll();

        while (!showOptions()) ;

        DataCache.saveAll();
	}

    public static boolean showOptions() {
        System.out.println("What type of user are you?");
        System.out.println("0) Exit Program");
        System.out.println("1) Customer");
        System.out.println("2) Logistics Department");
        System.out.println("3) Choreographer");
        System.out.println("4) Manager");
        System.out.println("5) Streaming Company");
        int response = UserInput.getIntInput(0, 5);

        Actor actor;
        switch(response) {
            case 0:
                return true;
            case 1:
                actor = new Customer();
                break;
            case 2:
                actor = new LogisticsDep();
                break;
            case 3:
                actor = new Choreographer();
                break;
            case 4:
                actor = new Manager();
                break;
            case 5:
                actor = new StreamingCompanyController();
                break;
            default:
                System.out.println("Invalid option. Exiting.");
                return true;
        }
        actor.showOptions();
        return false;
    }
}
