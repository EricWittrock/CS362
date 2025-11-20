import java.util.*;
import java.util.stream.Collectors;

public class ScriptAction implements DatabaseObject {
    private int actionId;
    private int scriptId;
    private ActionType actionType;
    private String description;
    private List<Integer> wrestlerIds;
    private int dangerRating; // scale of 1-10
    private int estimatedDuration; // in minutes
    private int sequenceOrder;

    // constructors
    public ScriptAction() {}

    public ScriptAction(int scriptId, ActionType actionType, String description,
                        List<Integer> wrestlerIds, int dangerRating, int estimatedDuration, int sequenceOrder) {
        this.actionId = new Random().nextInt(Integer.MAX_VALUE);
        this.scriptId = scriptId;
        this.actionType = actionType;
        this.description = description;
        this.wrestlerIds = wrestlerIds;

        if (dangerRating < 1 || dangerRating > 10) {
            throw new IllegalArgumentException("Danger rating must be between 1 and 10");
        }
        this.dangerRating = dangerRating;

        if (estimatedDuration <= 0) {
            throw new IllegalArgumentException("Estimated duration must be positive");
        }

        this.estimatedDuration = estimatedDuration;
        this.sequenceOrder = sequenceOrder;

        DataCache.addObject(this);
    }

    // getters
    public int getActionId() { return actionId; }
    public int getScriptId() { return scriptId; }
    public ActionType getActionType() { return actionType; }
    public String getDescription() { return description; }
    public List<Integer> getWrestlerIds() { return wrestlerIds; }
    public int getDangerRating() { return dangerRating; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public int getSequenceOrder() { return sequenceOrder; }

    // setters for editing
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setWrestlerIds(List<Integer> wrestlerIds) {
        this.wrestlerIds = new ArrayList<>(wrestlerIds);
    }
    
    public void setDangerRating(int dangerRating) {
        if (dangerRating < 1 || dangerRating > 10) {
            throw new IllegalArgumentException("Danger rating must be between 1 and 10");
        }
        this.dangerRating = dangerRating;
    }
    
    public void setDuration(int estimatedDuration) {
        if (estimatedDuration <= 0) {
            throw new IllegalArgumentException("Estimated duration must be positive");
        }
        this.estimatedDuration = estimatedDuration;
    }

    public double getInsuranceMultiplier() {
        if (dangerRating >= 8) {
            return 1.5;
        } else if (dangerRating >= 5) {
            return 1.2;
        } else {
            return 1.0;
        }
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    @Override
    public int getId() {
        return actionId;
    }

    @Override
    public String serialize() {
        String wrestlerIdsString = wrestlerIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(";"));
        
        return actionId + "," + scriptId + "," + actionType + "," + 
                description + "," + wrestlerIdsString + "," + dangerRating + "," +
                estimatedDuration + "," + sequenceOrder;
    }

    @Override
    public void deserialize(String data) {
        String [] parts = data.split(",", 8);
        this.actionId = Integer.parseInt(parts[0]);
        this.scriptId = Integer.parseInt(parts[1]);
        this.actionType = ActionType.valueOf(parts[2]);
        this.description = parts[3];

        this.wrestlerIds = new ArrayList<>();
        if (!parts[4].isEmpty()) {
            String[] idStrings = parts[4].split(";");
            for (String id : idStrings) {
                wrestlerIds.add(Integer.parseInt(id));
            }
        }

        this.dangerRating = Integer.parseInt(parts[5]);
        this.estimatedDuration = Integer.parseInt(parts[6]);
        this.sequenceOrder = Integer.parseInt(parts[7]);
    }

}
