import java.util.*;
import java.util.stream.Collectors;

public class WrestlerInsurance implements DatabaseObject { 
    private int insuranceId;
    private int wrestlerId;
    private int coverageAmount;
    private long expirationDate;
    private int maxDangerRating; // maximum danger rating covered (1-10)
    private List<ActionType> coveredActionTypes;

    public WrestlerInsurance() {
        this.coveredActionTypes = new ArrayList<>();
    }

    public WrestlerInsurance(int wrestlerId, int coverageAmount, long expirationDate,
                            int maxDangerRating, List<ActionType> coveredActionTypes) {
        this.insuranceId = new Random().nextInt(Integer.MAX_VALUE);
        this.wrestlerId = wrestlerId;
        this.coverageAmount = coverageAmount;
        this.expirationDate = expirationDate;

        if (maxDangerRating < 1 || maxDangerRating > 10) {
            throw new IllegalArgumentException("Max danger rating must be between 1 and 10");
        }
        this.maxDangerRating = maxDangerRating;
        this.coveredActionTypes = coveredActionTypes;

        DataCache.addObject(this);
    }

    public int getInsuranceId() { return insuranceId; }
    public int getWrestlerId() { return wrestlerId; }
    public int getCoverageAmount() { return coverageAmount; }
    public long getExpirationDate() { return expirationDate; }
    public int getMaxDangerRating() { return maxDangerRating; }
    public List<ActionType> getCoveredActionTypes() { return coveredActionTypes; }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationDate;
    }

    public boolean coversAction(ScriptAction action) {
        if (!coveredActionTypes.contains(action.getActionType())) {
            return false;
        }

        if (action.getDangerRating() > maxDangerRating) {
            return false;
        }

        if (isExpired()) {
            return false;
        }

        return true;
    }

    @Override
    public int getId() {
        return insuranceId;
    }

    @Override
    public String serialize() {
        String actionTypesStr = coveredActionTypes.stream()
            .map(ActionType::name)
            .collect(Collectors.joining(";"));

        return insuranceId + "," + 
                wrestlerId + "," + 
                coverageAmount + "," + 
                expirationDate + "," + 
                maxDangerRating + "," + 
                actionTypesStr;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 6);
        this.insuranceId = Integer.parseInt(parts[0]);
        this.wrestlerId = Integer.parseInt(parts[1]);
        this.coverageAmount = Integer.parseInt(parts[2]);
        this.expirationDate = Long.parseLong(parts[3]);
        this.maxDangerRating = Integer.parseInt(parts[4]);

        this.coveredActionTypes = new ArrayList<>();
        if (!parts[5].isEmpty()) {
            String[] types = parts[5].split(";");
            for (String type : types) {
                coveredActionTypes.add(ActionType.valueOf(type));
            }
        }
    }
}