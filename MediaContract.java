import java.util.Date;
import java.util.Random;

public class MediaContract implements DatabaseObject
{
    private int id;
    private StreamingCompany contractedCompany;
    private int totalPayment;
    private Event eventCovered;
    private ScriptStatus status;
    private long startDate;
    private long endDate;

    public MediaContract(){}

    public MediaContract(Event eventCovered, StreamingCompany contractedCompany,
    int totalPayment, long startDate, long endDate){
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.contractedCompany = contractedCompany;
        this.eventCovered = eventCovered;
        this.totalPayment = totalPayment;
        this.status = ScriptStatus.PROPOSED;
        this.startDate = startDate;
        this.endDate = endDate;
        DataCache.addObject(this);
    }

    public Event getEvent()
    {
        return eventCovered;
    }

    public StreamingCompany getStreamingCompany()
    {
        return contractedCompany;
    }

    public int getTotalPayment()
    {
        return totalPayment;
    }

    public ScriptStatus getStatus()
    {
        return status;
    }

    public void setStatus(ScriptStatus status)
    {
        this.status = status;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }
    
    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }

    public void setEventCovered(Event eventCovered) {
        this.eventCovered = eventCovered;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean contractAccepted()
    {
        return status.equals(ScriptStatus.APPROVED);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endDate;
    }

    public void printDetails()
    {
        System.out.println("Streaming Company: " + contractedCompany.getCompanyName());
        System.out.println("Start Date: " + new Date(startDate));
        System.out.println("End Date: " + new Date(endDate));
        System.out.println("Total Payment: " + totalPayment);
        System.out.println("EventID to Cover: " + eventCovered.getId());
        System.out.println("Current Status: " + status.toString().toUpperCase());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + eventCovered.getId() + "," + contractedCompany.getCompanyName()
        + "," + totalPayment + "," + startDate + "," + endDate + "," + status;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.eventCovered = DataCache.getById(Integer.parseInt(parts[1]), Event::new);
        this.contractedCompany = DataCache.getByFilter(c -> c.getCompanyName().equalsIgnoreCase(parts[2]),
            StreamingCompany::new);
        this.totalPayment = Integer.parseInt(parts[3]);
        this.startDate = Long.parseLong(parts[4]);
        this.endDate = Long.parseLong(parts[5]);
        this.status = ScriptStatus.valueOf(parts[6]);
    }
}