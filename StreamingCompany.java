import java.util.ArrayList;
import java.util.List;

public class StreamingCompany implements DatabaseObject {

    private String companyName;
    private int companyId = 0;
    private ArrayList<Event> purchasedEvents;

    public StreamingCompany() {
        this.purchasedEvents = new ArrayList<Event>();
    }

    public StreamingCompany(String companyName){
        this.companyName = companyName;
        companyId += 1;
        purchasedEvents = new ArrayList<Event>();

        DataCache.addStreamingCompany(this);
    }

    public String getCompanyName(){
        return companyName;
    }

    public ArrayList<Event> getPurchasedEvents(){
        return purchasedEvents;
    }

	@Override
	public int getId() {
		return companyId;
	}

	@Override
	public String serialize() {
        return "Company ID: " + companyId + ", Streaming Company: " + companyName;
	}

	@Override
	public void deserialize(String data) {
        String[] parts = data.split(",", 2);
        this.companyId = Integer.parseInt(parts[0]);
        this.companyName = parts[1];
	}
}
