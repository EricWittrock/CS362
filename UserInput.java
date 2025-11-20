import java.util.Scanner;

public class UserInput {
	private static Scanner sc;
	public static int getIntInput(int min, int max) {
		if(sc == null)
			sc = new Scanner(System.in);
	    while (true) {
	        try {
	            String line = sc.nextLine();
	            if (line == null || line.trim().isEmpty()) continue;
	            int value = Integer.parseInt(line.trim());
	            if (value < min || value > max) {
	                System.out.print("Enter a number between " + min + " and " + max + ": ");
	                continue;
	            }
	            return value;
	        } catch (NumberFormatException e) {
	            System.out.print("Enter a number: ");
	        }
	    }
	}

	public static int getIntInput() {
		if(sc == null)
			sc = new Scanner(System.in);
	    while (true) {
	        try {
	            String line = sc.nextLine();
	            if (line == null || line.trim().isEmpty()) continue;
	            int value = Integer.parseInt(line.trim());
	            return value;
	        } catch (NumberFormatException e) {
	            System.out.print("Enter a number: ");
	        }
	    }
	}

	public static long getLongInput() {
		if(sc == null)
			sc = new Scanner(System.in);
	    while (true) {
	        try {
	            String line = sc.nextLine();
	            if (line == null || line.trim().isEmpty()) continue;
	            long value = Long.parseLong(line.trim());
	            return value;
	        } catch (NumberFormatException e) {
	            System.out.print("Enter a number: ");
	        }
	    }
	}

    public static String getStringInput() {
        if(sc == null)
            sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line == null || line.trim().isEmpty()) {
                System.out.print("Enter a valid input: ");
                continue;
            }
            return line.trim();
        }
    }
}