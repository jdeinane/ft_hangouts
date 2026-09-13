package com.jubaldo.fthangouts.model;

public class Message {

    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    private int id;
    private final int contactId;
    private final String body;
    private final long timestamp;
    private final int type;

    public Message(int contactId, String body, long timestamp, int type) {
        this.contactId = contactId;
        this.body = body;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }
}
