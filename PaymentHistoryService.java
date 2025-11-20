import java.util.*;

public class PaymentHistoryService {

    public void viewWrestlerPaymentHistory() {
        System.out.println("\n=== Wrestler Payment History ===");

        List<WrestlerPayment> payments = DataCache.getAll(WrestlerPayment::new);
        if (payments.isEmpty()) {
            System.out.println("\nNo payment records found.");
            return;
        }

        System.out.println("\n" + String.format("%-25s %-20s %-15s %-12s %12s",
                "Wrestler", "Event", "Date", "Base+Bonus", "Total"));
        System.out.println("=".repeat(90));

        for (WrestlerPayment p : payments) {
            Wrestler w = DataCache.getById(p.getWrestlerId(), Wrestler::new);
            String wrestlerName = w != null ? w.getName() : "ID " + p.getWrestlerId();
            String dateStr = String.format("%tF", new Date(p.getPaymentDate()));

            System.out.println(String.format("%-25s %-20s %-15s $%-11d $%11d",
                    wrestlerName, p.getPaymentPeriod(), dateStr,
                    p.getBasePay() + p.getBonusAmount(), p.getTotalPay()));
        }

        int total = payments.stream().mapToInt(WrestlerPayment::getTotalPay).sum();
        System.out.println("=".repeat(90));
        System.out.println(String.format("%77s $%11d", "Total Paid:", total));
    }

    public void viewWorkerPaymentHistory() {
        System.out.println("\n=== Worker Payment History ===");

        List<WorkerPayment> payments = DataCache.getAll(WorkerPayment::new);
        if (payments.isEmpty()) {
            System.out.println("\nNo payment records found.");
            return;
        }

        System.out.println("\n" + String.format("%-20s %-15s %-10s %10s %12s",
                "Worker", "Period", "Date", "Hours", "Total"));
        System.out.println("=".repeat(75));

        for (WorkerPayment p : payments) {
            Worker w = DataCache.getById(p.getWorkerId(), Worker::new);
            String workerName = w != null ? w.getName() : "ID " + p.getWorkerId();
            String dateStr = String.format("%tF", new Date(p.getPaymentDate()));

            System.out.println(String.format("%-20s %-15s %-10s %10d $%11d",
                    workerName, p.getPaymentPeriod(), dateStr,
                    p.getTotalHours(), p.getTotalPay()));
        }

        int total = payments.stream().mapToInt(WorkerPayment::getTotalPay).sum();
        System.out.println("=".repeat(75));
        System.out.println(String.format("%62s $%11d", "Total Paid:", total));
    }
}