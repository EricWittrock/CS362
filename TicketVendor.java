import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class TicketVendor {
    private static int balance = 0;
    public static void buyTicket() {
        List<Event> events = DataCache.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        List<Option> options = new ArrayList<>();
        for (Event e : events) {
            options.add(new Option(e.getDate() + " event: " + e.getVenue().getName(), () -> {
                chooseSection(e);
            }));
        }


        while (true) {
            System.out.println("\nAvailable Events:");
            System.out.println("0) Back");
            int option_start = 1;
            for (int i = 0; i < options.size(); i++) {
                options.get(i).Display(i + option_start);
            }

            System.out.print("Choose an event: ");
            int choice = UserInput.getIntInput();

            if (choice == 0) {
                System.out.println();
            	return;
            }
            if (choice < 1 || choice > events.size()) {
                System.out.println("Invalid option.");
                continue;
            }

            options.get(choice - option_start).select();
        }
    }

    private static void chooseSection(Event event) {
        while (true) {
            System.out.println("\nSections for " + event.getVenue().getName() + ":");
            System.out.println("0) Back");
            List<Option> options = new ArrayList<>();
            NumberFormat formatter = new DecimalFormat("0.00");
            for (Section s : event.getSections()) {
                options.add(new Option(
                    s.getName() + 
                    ", seats: (" + s.getNumSeats() +
                    ") price: $" + s.getPrice(), 
                    () -> {chooseSeat(event, s);}
                ));
            }
            for (int i = 0; i < options.size(); i++)
                options.get(i).Display(i + 1);

            System.out.print("Choose a section: ");
            int choice = UserInput.getIntInput();
            if (choice == 0) return;
            if (choice < 1 || choice > event.getSections().size()) {
                System.out.println("Invalid option.");
                continue;
            }
            options.get(choice - 1).select();
        }
    }

    private static void chooseSeat(Event event, Section section) {
        while (true) {
            System.out.printf("%nSection '%s' has %d seats.%n", section.getName(), section.getNumSeats());
            System.out.print("Enter seat number (1-" + section.getNumSeats() + ") or 0 to go back:");
            int seatNum = UserInput.getIntInput();
            if (seatNum == 0) return;
            if (seatNum < 1 || seatNum > section.getNumSeats()) {
                System.out.println("Invalid seat number.");
                continue;
            }
            if (section.tryTakeSeat(seatNum)) {
                balance += section.getPrice();
                System.out.printf("\nSeat %d in section '%s' booked successfully!%n", seatNum, section.getName());
            }
            else
                System.out.println("\nSeat already taken!");
        }
    }
}
