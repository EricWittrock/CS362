public class MediaContract implements DatabaseObject
{
    private int id = 0;
    private StreamingCompany contractedCompany;
    private int totalPayment;
    private Event eventCovered;
    private ScriptStatus status;
    private long startDate;
    private long endDate;

    public MediaContract(){}

    public MediaContract(Event eventCovered, StreamingCompany contractedCompany,
    int totalPayment, long startDate, long endDate){
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

    public void acceptContract()
    {
        status = ScriptStatus.APPROVED;
    }

    public void rejectContract()
    {
        status = ScriptStatus.REJECTED;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endDate;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + eventCovered.getId() + "," + contractedCompany.getCompanyName()
        + "," + totalPayment + "," + startDate + "," + endDate;
    }

    @Override
    public void deserialize(String data) {
    }
}