public class StateTaxInfo {
    public String stateCode;
    public String jurisdictionName;
    public double stateRate;
    public double localRate;
    public double suiRate; // State unemployment insurance

    public StateTaxInfo(String stateCode, String jurisdictionName, 
                        double stateRate, double localRate, double suiRate) {
        this.stateCode = stateCode;
        this.jurisdictionName = jurisdictionName;
        this.stateRate = stateRate;
        this.localRate = localRate;
        this.suiRate = suiRate;
    }
}
