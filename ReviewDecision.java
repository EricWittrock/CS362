public class ReviewDecision {
    private boolean approved;
    private String reason; 

    public ReviewDecision(boolean approved, String reason) {
        this.approved = approved;
        this.reason = reason;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getReason() {
        return reason;
    }
}
