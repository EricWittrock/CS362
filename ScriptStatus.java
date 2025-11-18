public enum ScriptStatus {
    PROPOSED, // submitted by the choreographer
    UNDER_REVIEW, // being reviewed by stakeholders
    REQUIRES_REVISION, // sent back for changes
    APPROVED, // accepted and ready for event
    REJECTED, // wont be used
    ARCHIVED // stored for future reference
}
