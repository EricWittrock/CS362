class Script implements DatabaseObject {
    private String eventId;
    private String content;
    private String choreographer;

    public Script() {}

    public Script(String eventId, String content, String choreographer) {
        this.eventId = eventId;
        this.content = content;
        this.choreographer = choreographer;

        DataCache.addScript(this);
    }

    public String getEventId() { return eventId; }
    public String getContent() { return content; }
    public String getChoreographer() { return choreographer; }

    @Override 
    
    public int getId() {
        return Integer.parseInt(eventId);
    }

    @Override
    public String serialize() {
        return eventId + "|" + choreographer + "|" + content.replace("\n", "\\n");
    }

    @Override
    public void deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        if (parts.length == 3) {
            this.eventId = parts[0];
            this.choreographer = parts[1];
            this.content = parts[2].replace("\\n", "\n");
        }
    }

    @Override
    public String toString() {
        return "Script{" +
                "eventId='" + eventId + '\'' +
                ", content='" + content + '\'' +
                ", choreographer='" + choreographer + '\'' +
                '}';
    }
}