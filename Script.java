import java.util.*;
import java.util.stream.Collectors;

class Script implements DatabaseObject {
    private int eventId;
    private String choreographer;
    private int scriptId;
    private ScriptStatus status; // current status of the script
    private List<Integer> actionIds; // associated ScriptAction IDs
    private String rejectionReason; // reason for rejection
    private long proposedDate; // When the script was submitted
    private long approvedDate; // When the script was approved (0 if not approved yet)
    private String approvedBy; // Stakeholder name who approved 
    private int totalInsuranceCost; // calaculated cost

    public Script() {
        this.actionIds = new ArrayList<>();
    }

    public Script(int eventId, String choreographer) {
        this.eventId = eventId;
        this.choreographer = choreographer;
        this.scriptId = new Random().nextInt(Integer.MAX_VALUE);

        this.status = ScriptStatus.PROPOSED;
        this.actionIds = new ArrayList<>();
        this.rejectionReason = null;
        this.proposedDate = System.currentTimeMillis();
        this.approvedDate = 0;
        this.approvedBy = null;
        this.totalInsuranceCost = 0;
        DataCache.addObject(this);
    }

    public int getEventId() { return eventId; }
    public String getChoreographer() { return choreographer; }
    public int getScriptId() { return scriptId; }
    public ScriptStatus getStatus() { return status; }
    public List<Integer> getActionIds() { return actionIds; }
    public String getRejectionReason() { return rejectionReason; }
    public long getProposedDate() { return proposedDate; }
    public long getApprovedDate() { return approvedDate; }
    public String getApprovedBy() { return approvedBy; }
    public int getTotalInsuranceCost() { return totalInsuranceCost; }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setApprovedDate(long approvedDate) {
        this.approvedDate = approvedDate;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void addActionId(int actionId) {
        if (!this.actionIds.contains(actionId)) {
            this.actionIds.add(actionId);
        }
    }

    public void removeActionId(int actionId) {
        this.actionIds.remove(Integer.valueOf(actionId));
    }

    public double calculateTotalRisk() {
        if (actionIds.isEmpty()) {
            return 0.0;
        }

        double totalWeightedRisk = 0.0;
        int totalDuration = 0;

        for (Integer actionId : actionIds) {
            ScriptAction action = DataCache.getById(actionId, ScriptAction::new);
            
            if (action != null) {
                int duration = action.getEstimatedDuration();
                int danger = action.getDangerRating();

                totalWeightedRisk += danger * duration;
                totalDuration += duration;
            }
        }

        return totalDuration > 0 ? totalWeightedRisk / totalDuration : 0.0;

    }

    public double calculateInsuranceCost() {
        int baseCost = 5000; // base insurance cost
        int totalMultiplier = 1;

        if (actionIds.isEmpty()) {
            this.totalInsuranceCost = baseCost;
            return baseCost;
        }

        for (Integer actionId : actionIds) {
            ScriptAction action = DataCache.getById(actionId, ScriptAction::new);
            if (action != null) {
                totalMultiplier *= action.getInsuranceMultiplier();
            }
        }

        this.totalInsuranceCost = baseCost * totalMultiplier;
        return this.totalInsuranceCost;

    }

    public List<Integer> getRequiredWrestlerIds() {
        Set<Integer> uniqueWrestlerIds = new HashSet<>();
        
        for (Integer actionId : actionIds) {
            ScriptAction action = DataCache.getById(actionId, ScriptAction::new);
            if (action != null) {
                uniqueWrestlerIds.addAll(action.getWrestlerIds());
            }
        }

        return new ArrayList<>(uniqueWrestlerIds);
    }

    @Override 
    public int getId() {
        return scriptId;
    }

    @Override
    public String serialize() {
        String actionIdsStr = "";
        if (!actionIds.isEmpty()) {
            actionIdsStr = actionIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"));
        }

        return scriptId + "," +
               eventId + "," +
               choreographer + "," + 
               status + "," +
               (rejectionReason != null ? rejectionReason : "") + "," +
               proposedDate + "," +
               approvedDate + "," +
               (approvedBy != null ? approvedBy : "") + "," +
               totalInsuranceCost + "," +
               actionIdsStr;
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split(",", 10);
        this.scriptId = Integer.parseInt(parts[0]);
        this.eventId = Integer.parseInt(parts[1]);
        this.choreographer = parts[2];
        this.status = ScriptStatus.valueOf(parts[3]);
        this.rejectionReason = parts[4].isEmpty() ? null : parts[4];
        this.proposedDate = Long.parseLong(parts[5]);
        this.approvedDate = Long.parseLong(parts[6]);
        this.approvedBy = parts[7].isEmpty() ? null : parts[7];
        this.totalInsuranceCost = Integer.parseInt(parts[8]);

        this.actionIds = new ArrayList<>();
        if (!parts[9].isEmpty()) {
            String[] idStrings = parts[9].split(";");
            for (String idStr : idStrings) {
                actionIds.add(Integer.parseInt(idStr));
            }
        }
    }
}
