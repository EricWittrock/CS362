import java.util.ArrayList;
import java.util.List;

public class StreamingCompany implements DatabaseObject {

    private String companyName;
    private int companyId = 0;
    private ArrayList<Event> purchasedEvents;
    private MediaContract contract;

    public StreamingCompany() {
    }

    public StreamingCompany(String companyName){
        this.companyName = companyName;
        companyId += 1;
        purchasedEvents = new ArrayList<Event>();

        DataCache.addObject(this);
    }

    public String getCompanyName(){
        return companyName;
    }

    public ArrayList<Event> getPurchasedEvents(){
        return purchasedEvents;
    }

    public MediaContract getContract()
    {
        return contract;
    }

    public void setContract(MediaContract contract)
    {
        this.contract = contract;
    }

	@Override
	public int getId() {
		return companyId;
	}

	@Override
	public String serialize() {
        return companyId + "," + companyName;
	}

	@Override
	public void deserialize(String data) {
        String[] parts = data.split(",", 2);
        this.companyId = Integer.parseInt(parts[0]);
        this.companyName = parts[1];
	}
}
