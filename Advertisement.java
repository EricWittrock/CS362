public class Advertisement implements DatabaseObject
{
    private int id = 0;
    private Script adContents;
    private StreamingCompany company;
    private MediaContract contract;
    private Budget budget;

    public Advertisement(){}

    public Advertisement(MediaContract contract, StreamingCompany company, 
    Script adContents){
        id++;
        this.contract = contract;
        this.company = company;
        this.adContents = adContents;
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

    public Script getScript()
    {
        return adContents;
    }

    public Budget getBudget()
    {
        return budget;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String serialize() {
        return id + "," + adContents.getChoreographer() + "," + company.getCompanyName()
        + "," + budget.getTotalBudget();
    }

    @Override
    public void deserialize(String data) {
    }
}