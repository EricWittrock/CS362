import java.util.Random;

public class Advertisement implements DatabaseObject
{
    private int id;
    private String adContents;
    private StreamingCompany company;
    private MediaContract contract;
    private ScriptStatus status;

    public Advertisement(){}

    public Advertisement(MediaContract contract, StreamingCompany company, 
    String adContents){
        id = new Random().nextInt(Integer.MAX_VALUE);
        this.contract = contract;
        this.company = company;
        this.adContents = adContents;
        status = ScriptStatus.PROPOSED;
        DataCache.addObject(this);
    }

    public StreamingCompany getCompany()
    {
        return company;
    }

    public MediaContract getContract()
    {
        return contract;
    }

    public String getAdContents()
    {
        return adContents;
    }

    public ScriptStatus getStatus()
    {
        return status;
    }

    public void setStatus(ScriptStatus status)
    {
        this.status = status;
    }

    public void printDetails()
    {
        System.out.println("Streaming Company: " + company.getCompanyName());
        System.out.println("Current Status: " + status.toString().toUpperCase());
        System.out.println("Ad Content: " + adContents);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + company.getCompanyName() + "," + adContents + "," + status;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.company = DataCache.getByFilter(c -> c.getCompanyName().equalsIgnoreCase(parts[1]),
            StreamingCompany::new);
        this.adContents = parts[2];
        this.status = ScriptStatus.valueOf(parts[3]);
    }
}