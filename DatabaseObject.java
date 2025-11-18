interface DatabaseObject {
    int getId();
    String serialize();
    void deserialize(String data);
}
