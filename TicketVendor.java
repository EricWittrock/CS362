import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class TicketVendor {
    public static void buyTicket() {
        ArrayList<Event> events = DataCache.getAll(Event::new);

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        OptionList options = new OptionList();
        for (Event e : events) {
            options.add(e.getDate() + " event: " + e.getVenue().getName(), () -> {
                chooseSection(e);
            });
        }
    }

    private static void chooseSection(Event event) {
        OptionList options = new OptionList();
        options.exitOption = "Back";
        for (Section s : event.getSections()) {
            options.add(
                s.getName() + 
                ", seats: (" + s.getNumSeats() +
                ") price: $" + s.getPrice() + ".00", 
                () -> {chooseSeat(event, s);}
            );
        }
        options.loopDisplayAndSelect("Choose a section:");
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
                Budget.get("Logistics").profit(section.getPrice());
                System.out.printf("\nSeat %d in section '%s' booked successfully!%n", seatNum, section.getName());
            }
            else
                System.out.println("\nSeat already taken!");
        }
    }
}
